package pl.baczkowicz.mqttspy.utils;

import org.apache.commons.codec.binary.Base64;

import pl.baczkowicz.mqttspy.common.generated.MessageLog;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;

public class MessageLoggingUtils
{
	public static String createLog(final ReceivedMqttMessage message, final MessageLog messageLog)
	{
		final StringBuffer logMessage = new StringBuffer();
		logMessage.append("<MqttMessage ");
		
		appendAttribute(logMessage, "id", String.valueOf(message.getId()));
		appendAttribute(logMessage, "timestamp", String.valueOf( message.getDate().getTime()));		
		// appendAttribute(logMessage, "source", "SUB");		
		appendAttribute(logMessage, "topic", message.getTopic());		
		appendAttribute(logMessage, "qos", String.valueOf(message.getMessage().getQos()));		
		appendAttribute(logMessage, "retained", String.valueOf(message.getMessage().isRetained()));		
		
		logMessage.append(">");
		
		if (MessageLog.XML_ENCODED_PAYLOAD.equals(messageLog))
		{
			appendElement(logMessage, "Payload", Base64.encodeBase64String(message.getMessage().getPayload()));
		}
		else
		{
			appendElement(logMessage, "Payload", "<![CDATA[" + new String(message.getMessage().getPayload()) + "]]>");
		}
		
		logMessage.append("</MqttMessage>");
		
		return logMessage.toString();
	}
	
	private static void appendAttribute(final StringBuffer logMessage, final String attributeName, final String attributeValue)
	{
		logMessage.append(attributeName + "=\"" + attributeValue + "\" ");
	}
	
	private static void appendElement(final StringBuffer logMessage, final String elementName, final String elementValue)
	{
		logMessage.append("<" + elementName + ">" + elementValue + "</" + elementName + ">");
	}
}
