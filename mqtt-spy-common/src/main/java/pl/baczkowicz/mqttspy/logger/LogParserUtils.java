package pl.baczkowicz.mqttspy.logger;

import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import pl.baczkowicz.mqttspy.common.generated.MessageLog;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;

public class LogParserUtils
{
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
}
