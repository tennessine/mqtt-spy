package pl.baczkowicz.mqttspy.utils;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;

public class ConfigurationUtils
{
	public static void populateConnectionDefaults(final MqttConnectionDetails connection)
	{
		if (connection.getName() == null || connection.getName().isEmpty())
		{
			connection.setName(ConnectionUtils.composeConnectionName(connection.getClientID(), connection.getServerURI()));
		}
		
		if (connection.isCleanSession() == null)
		{
			connection.setCleanSession(MqttConnectOptions.CLEAN_SESSION_DEFAULT);
		}
		
		if (connection.getConnectionTimeout() == null)
		{
			connection.setConnectionTimeout(MqttConnectOptions.CONNECTION_TIMEOUT_DEFAULT);
		}		

		if (connection.getKeepAliveInterval() == null)
		{
			connection.setKeepAliveInterval(MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT);
		}		
	}
}
