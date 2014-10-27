/**
 * 
 */
package pl.baczkowicz.mqttspy.logger;

import java.io.File;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.exceptions.XMLException;
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
			
			// Creating MQTT client instance
			final MqttClient client = new MqttClient(
					loader.getConfiguration().getConnection().getServerURI(), 
					loader.getConfiguration().getConnection().getClientID(),
					null);
			
			// Set MQTT callback
			client.setCallback(new MqttCallbackHandler());			
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
