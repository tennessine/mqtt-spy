/**
 * 
 */
package pl.baczkowicz.mqttspy.logger;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.exceptions.XMLException;
import pl.baczkowicz.mqttspy.common.generated.BaseConnectionDetailsWithSubscriptions;
import pl.baczkowicz.mqttspy.common.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.logger.configuration.ConfigurationLoader;
import pl.baczkowicz.mqttspy.logger.connectivity.MqttCallbackHandler;

/**
 * @author kamil
 *
 */
public class Main
{
	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			logger.error("Expecting only 1 parameter with the configuration file location");
			return;
		}
		
		try
		{
			final ConfigurationLoader loader = new ConfigurationLoader();
			
			loader.loadConfiguration(new File(args[0]));
			
			final BaseConnectionDetailsWithSubscriptions connection = loader.getConfiguration().getConnection();
			// Creating MQTT client instance
			final MqttClient client = new MqttClient(
					connection.getServerURI(), 
					connection.getClientID(),
					null);
			
			// Set MQTT callback
			client.setCallback(new MqttCallbackHandler());			
			
			final MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(connection.isCleanSession());
			options.setConnectionTimeout(connection.getConnectionTimeout());
			options.setKeepAliveInterval(connection.getKeepAliveInterval());
			
			if (connection.getUserCredentials() != null)
			{
				options.setUserName(connection.getUserCredentials().getUsername());
				options.setPassword(connection.getUserCredentials().getPassword().toCharArray());
			}
			
			if (connection.getLastWillAndTestament() != null)
			{
				options.setWill(connection.getLastWillAndTestament().getTopic(), 
						Base64.decodeBase64(connection.getLastWillAndTestament().getPayload()),
						connection.getLastWillAndTestament().getQos(),
						connection.getLastWillAndTestament().isRetained());
			}
			
			client.connect(options);
			logger.info("Successfully connected to " + connection.getServerURI());
			
			for (final SubscriptionDetails subscription : connection.getSubscription())
			{
				client.subscribe(subscription.getTopic(), subscription.getQos());
				logger.info("Successfully subscribed to " + subscription.getTopic());
			}
		}
		catch (XMLException e)
		{
			logger.error("Cannot instantiate the configuration loader", e);
		}
		catch (MqttException e)
		{
			logger.error("Error occurred while connecting to broker", e);
		}
	}

}
