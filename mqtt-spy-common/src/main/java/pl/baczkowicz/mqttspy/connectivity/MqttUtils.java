package pl.baczkowicz.mqttspy.connectivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pl.baczkowicz.mqttspy.utils.ConversionUtils;

public class MqttUtils
{
	public final static String TCP_PREFIX = "tcp://";
	public final static String SSL_PREFIX = "ssl://";
	
	public static final String MULTI_LEVEL_WILDCARD = "#";
	
	public static final String SINGLE_LEVEL_WILDCARD = "+";
	
	public static final String TOPIC_DELIMITER = "/";
	
	public static final int MAX_CLIENT_LENGTH = 23;

	private static final String TIMESTAMP_FORMAT = "HHmmssSSS";
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(TIMESTAMP_FORMAT);
	
	private static final String CLIENT_TIMESTAMP_DELIMITER = "_";
	
	public static String removeLastDelimiter(String topic)
	{
		if (topic.endsWith(TOPIC_DELIMITER))
		{
			topic = topic.substring(0, topic.length() - TOPIC_DELIMITER.length());
		}
		
		return topic;
	}		

	public static String generateClientIdWithTimestamp(final String clientId)
	{
		final int addedLength = TIMESTAMP_FORMAT.length() + CLIENT_TIMESTAMP_DELIMITER.length();
		final int index = clientId.lastIndexOf(CLIENT_TIMESTAMP_DELIMITER);
		String newClientId = clientId;
		
		// for e.g. k-01; index = 1; added length = 3; length == 4
		if (index >= 0 && (index + addedLength == newClientId.length()))
		{
			newClientId = newClientId.substring(0, index); 
		}		
	
		if (newClientId.length() + addedLength > MAX_CLIENT_LENGTH)
		{
			newClientId = newClientId.substring(0, 23 - addedLength);
		}

		newClientId = newClientId + CLIENT_TIMESTAMP_DELIMITER + SDF.format(new Date());

		return newClientId;
	}
	
	public static String getCompleteServerURI(final String brokerAddress)
	{
		String serverURI = brokerAddress;

		if (serverURI.startsWith(TCP_PREFIX)
				|| serverURI.startsWith(SSL_PREFIX))
		{
			return serverURI;
		}

		return TCP_PREFIX + serverURI;
	}
	
	public static String encodePassword(final String value)
	{
		return ConversionUtils.stringToBase64(value);
	}
	
	public static String decodePassword(final String value)
	{
		return ConversionUtils.base64ToString(value);
	}
	
	public static boolean recordTopic(final String newTopic, final List<String> topics)
	{
		for (final String topic : topics)
		{
			if (topic.equals(newTopic))
			{
				// If the topic is already on the list, don't add it again
				return false;
			}
		}

		topics.add(newTopic);
		return true;
	}
}
