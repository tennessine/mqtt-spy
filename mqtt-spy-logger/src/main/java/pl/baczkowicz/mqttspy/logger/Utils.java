package pl.baczkowicz.mqttspy.logger;

import org.apache.commons.codec.binary.Base64;

import pl.baczkowicz.mqttspy.common.messages.ReceivedMqttMessage;

public class Utils
{
	
	public static String createLog(final ReceivedMqttMessage message)
	{
		final StringBuffer logMessage = new StringBuffer();
		logMessage.append("<MqttMessage ");
		
		appendAttribute(logMessage, "id", String.valueOf(message.getId()));
		appendAttribute(logMessage, "timestamp", String.valueOf( message.getDate().getTime()));		
		appendAttribute(logMessage, "source", "SUB");		
		appendAttribute(logMessage, "topic", message.getTopic());		
		appendAttribute(logMessage, "qos", String.valueOf(message.getMessage().getQos()));		
		appendAttribute(logMessage, "retained", String.valueOf(message.getMessage().isRetained()));
		appendAttribute(logMessage, "payload", Base64.encodeBase64String(message.getMessage().getPayload()));		
		
		logMessage.append("/>");
		
		return logMessage.toString();
	}
	
	private static void appendAttribute(final StringBuffer logMessage, final String attributeName, final String attributeValue)
	{
		logMessage.append(attributeName + "=\"" + attributeValue + "\" ");
	}
}
