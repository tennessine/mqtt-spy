package pl.baczkowicz.mqttspy.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.messagestore.MqttMessageStore;

public class ConfigurationUtils
{
	public static void populateConnectionDefaults(final ConnectionDetails connection)
	{
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
		
		if (connection.getMaxMessagesStored() == null)
		{
			connection.setMaxMessagesStored(MqttMessageStore.DEFAULT_MAX_SIZE);
		}
		
		if (connection.isAutoOpen() == null)
		{
			connection.setAutoOpen(false);
		}
		
		if (connection.isAutoConnect() == null)
		{
			connection.setAutoConnect(true);
		}
	}
}
