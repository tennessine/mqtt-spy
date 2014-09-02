package pl.baczkowicz.mqttspy.connectivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;

public class MqttUtils
{
	public static final String MULTI_LEVEL_WILDCARD = "#";
	
	public static final String SINGLE_LEVEL_WILDCARD = "+";
	
	public static final String TOPIC_DELIMITER = "/";
	
	public static final int MAX_CLIENT_LENGTH = 23;

	private static final String TIMESTAMP_FORMAT = "HHmmssSSS";
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat(TIMESTAMP_FORMAT);
	
	private static final String CLIENT_TIMESTAMP_DELIMITER = "-";
	
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

		newClientId = newClientId + "-" + SDF.format(new Date());

		return newClientId;
	}
	
	public static String getServerURI(final String brokerAddress)
	{
		String serverURI = brokerAddress;

		if (serverURI.startsWith(MqttManager.TCP_PREFIX)
				|| serverURI.startsWith(MqttManager.SSL_PREFIX))
		{
			return serverURI;
		}

		return MqttManager.TCP_PREFIX + serverURI;
	}
	
	public static String validateConnectionDetails(final ConnectionDetails connectionDetails, final boolean finalCheck)
	{
		if (connectionDetails.getServerURI() == null
				|| connectionDetails.getServerURI().trim().isEmpty())
		{
			return "Server URI cannot be empty";
		}
		
		if (connectionDetails.getClientID() == null
				|| connectionDetails.getClientID().trim().isEmpty() 
				|| connectionDetails.getClientID().length() > MqttUtils.MAX_CLIENT_LENGTH)
		{
			return "Client ID cannot be empty or longer than " + MqttUtils.MAX_CLIENT_LENGTH;
		}
		
		if (connectionDetails.getUserAuthentication() != null)
		{
			if ((finalCheck || !connectionDetails.getUserAuthentication().isAskForUsername()) && (connectionDetails.getUserAuthentication().getUsername() == null
					|| connectionDetails.getUserAuthentication().getUsername().trim().isEmpty()))
			{
				return "With user authentication enabled, user name cannot be empty";
			}
		}
		
		if (connectionDetails.getConnectionTimeout() < 0)
		{
			return "Connection timeout cannot be less than 0";
		}
		
		if (connectionDetails.getKeepAliveInterval() < 0)
		{
			return "Keep alive interval cannot be less than 0";
		}		
		
		return null;
	}
	
	public static String encodePassword(final String value)
	{
		return FormattingUtils.stringToBase64(value);
	}
	
	public static String decodePassword(final String value)
	{
		return FormattingUtils.base64ToString(value);
	}
}
