package pl.baczkowicz.mqttspy.utils;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttUtils;

public class ConfigurationUtils
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);
	
	public static void populateServerURIs(final MqttConnectionDetails connection)
	{
		for (int i = 0; i < connection.getServerURI().size(); i++)
		{
			final String serverURI = connection.getServerURI().get(i);			
			final String completeServerURI = MqttUtils.getCompleteServerURI(serverURI);
			
			// Replace the existing value if it is not complete
			if (!completeServerURI.equals(serverURI))
			{
				logger.info("Auto-complete for server URI ({} -> {})", serverURI, completeServerURI);
				connection.getServerURI().set(i, completeServerURI);
			}
		}	
	}
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
