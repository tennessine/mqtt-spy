package pl.baczkowicz.mqttspy.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.LoggedMqttMessage;
import pl.baczkowicz.mqttspy.common.generated.MessageLog;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.utils.ProgressUpdater;

public class LogParserUtils
{
	private final static Logger logger = LoggerFactory.getLogger(LogParserUtils.class);
	
	/** Pattern that is used to decide whether a string should be wrapped in CDATA. */
    private static final Pattern XML_CHARS = Pattern.compile("[&<>]");
    
	public static String createLog(final ReceivedMqttMessage message, final MessageLog messageLog)
	{
		final StringBuffer logMessage = new StringBuffer();
		logMessage.append("<MqttMessage ");
		
		appendAttribute(logMessage, "id", String.valueOf(message.getId()));
		appendAttribute(logMessage, "timestamp", String.valueOf( message.getDate().getTime()));				
		appendAttribute(logMessage, "topic", message.getTopic());		
		appendAttribute(logMessage, "qos", String.valueOf(message.getMessage().getQos()));		
		appendAttribute(logMessage, "retained", String.valueOf(message.getMessage().isRetained()));		
		
		if (MessageLog.XML_WITH_ENCODED_PAYLOAD.equals(messageLog))
		{
			appendAttribute(logMessage, "encoded", "true");
		}
		
		logMessage.append(">");
		
		if (MessageLog.XML_WITH_ENCODED_PAYLOAD.equals(messageLog))
		{
			appendValue(logMessage, Base64.encodeBase64String(message.getMessage().getPayload()));
		}
		else
		{
			final String payload = new String(message.getMessage().getPayload());
			final boolean useCData = XML_CHARS.matcher(payload).find();
			if (useCData)
			{
				appendValue(logMessage, "<![CDATA[" + payload + "]]>");
			}
			else
			{
				appendValue(logMessage, payload);
			}
		}
		
		logMessage.append("</MqttMessage>");
		
		return logMessage.toString();
	}
	
	public static void appendAttribute(final StringBuffer logMessage, final String attributeName, final String attributeValue)
	{
		logMessage.append(attributeName + "=\"" + attributeValue + "\" ");
	}
	
	public static void appendElement(final StringBuffer logMessage, final String elementName, final String elementValue)
	{
		logMessage.append("<" + elementName + ">" + elementValue + "</" + elementName + ">");
	}
	
	public static void appendValue(final StringBuffer logMessage, final String value)
	{
		logMessage.append(value);
	}
	
	// public static List<ReceivedMqttMessage> readAndConvertMessageLog(final
	// File selectedFile) throws MqttSpyException
	// {
	// return processMessageLog(parseMessageLog(readMessageLog(selectedFile)));
	// }
	
	public static List<String> readMessageLog(final File selectedFile) throws MqttSpyException
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(selectedFile));
	        String str;
			        
	        final List<String> list = new ArrayList<String>();
	        while((str = in.readLine()) != null)
	        {
	        	list.add(str);	        	
	        }
	        
	        in.close();
	        logger.info("Read {} messages from {}", list.size(), selectedFile.getAbsoluteFile());		        		       
	        
	        return list;
		}
		catch (IOException e)
		{
			throw new MqttSpyException("Can't open the message log file at " + selectedFile.getAbsolutePath(), e);
		}
	}
	
	public static List<LoggedMqttMessage> parseMessageLog(final List<String> messages, 
			final ProgressUpdater progress, final long current, final long max) throws MqttSpyException
	{
		try
		{
			final LogParser parser = new LogParser();
			        
	        final List<LoggedMqttMessage> list = new ArrayList<LoggedMqttMessage>();
	        long item = 0;
	        
	        for (final String message : messages)
	        {
	        	item++;
	        	if (item % 1000 == 0)
	        	{
	        		progress.update(current + item, max);
	        	}
	        	
	        	try
	        	{
	        		list.add(parser.parse(message));
	        	}
	        	catch (XMLException e)
	        	{
	        		logger.error("Can't process message " + message, e);
	        	}
	        }
	        
	        logger.info("Parsed {} messages", list.size());		        		       
	        
	        return list;
		}
		catch (XMLException e)
		{
			throw new MqttSpyException("Can't parse the message log file", e);
		}
	}
	
	public static List<ReceivedMqttMessage> processMessageLog(final List<LoggedMqttMessage> list, 
			final ProgressUpdater progress, final long current, final long max)
	{
		final List<ReceivedMqttMessage> mqttMessageList = new ArrayList<ReceivedMqttMessage>();
		long item = 0;
		
		// Process the messages
        for (final LoggedMqttMessage loggedMessage : list)
        {
        	item++;
        	if (item % 1000 == 0)
        	{
        		progress.update(current + item, max);
        	}
        	
        	final MqttMessage mqttMessage = new MqttMessage();
        	
        	if (logger.isTraceEnabled())
        	{
        		logger.trace("Processing message with payload {}", loggedMessage.getValue());
        	}
        	
        	if (Boolean.TRUE.equals(loggedMessage.isEncoded()))
        	{
        		mqttMessage.setPayload(Base64.decodeBase64(loggedMessage.getValue()));
        	}
        	else
        	{
        		mqttMessage.setPayload(loggedMessage.getValue().getBytes());
        	}
        	
        	mqttMessage.setQos(loggedMessage.getQos());
        	mqttMessage.setRetained(loggedMessage.isRetained());
        	
        	mqttMessageList.add(new ReceivedMqttMessage(loggedMessage.getId(), loggedMessage.getTopic(), mqttMessage, new Date(loggedMessage.getTimestamp())));
        }
        return mqttMessageList;
	}
}
