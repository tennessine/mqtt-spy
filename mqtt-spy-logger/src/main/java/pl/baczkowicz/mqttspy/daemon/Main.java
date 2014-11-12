/**
 * 
 */
package pl.baczkowicz.mqttspy.daemon;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.Script;
import pl.baczkowicz.mqttspy.common.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.configuration.PropertyFileLoader;
import pl.baczkowicz.mqttspy.connectivity.SimpleMqttAsyncConnection;
import pl.baczkowicz.mqttspy.daemon.configuration.ConfigurationLoader;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.daemon.connectivity.MqttCallbackHandler;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;

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
		try
		{
			final ConfigurationLoader loader = new ConfigurationLoader();
			
			logger.info("#######################################################");
			logger.info("### Starting mqtt-spy-daemon v{}", loader.getFullVersionName());
			logger.info("### If you find it useful, please donate at http://fundraise.unicef.org.uk/MyPage/mqtt-spy :)");
			logger.info("### Visit {} for more information on the project", loader.getProperty(PropertyFileLoader.DOWNLOAD_URL));
			logger.info("### To get release updates follow @mqtt_spy on Twitter");
			logger.info("#######################################################");
			
			if (args.length != 1)
			{
				logger.error("Expecting only 1 parameter with the configuration file location");
				return;
			}		
		
						
			
			loader.loadConfiguration(new File(args[0]));
			
			final DaemonMqttConnectionDetails connectionSettings = loader.getConfiguration().getConnection();

			final SimpleMqttAsyncConnection connection = new SimpleMqttAsyncConnection(connectionSettings);
			final ScriptManager scriptManager = new ScriptManager(null, null, connection);
			connection.createClient(new MqttCallbackHandler(connection, connectionSettings, scriptManager));
			connection.connect();
			
			logger.info("Successfully connected to " + connectionSettings.getServerURI());
			
			
			// Subscribe to all configured subscriptions
			for (final SubscriptionDetails subscription : connectionSettings.getSubscription())
			{	
				if (subscription.getScriptFile() != null)
				{
					scriptManager.addScript(subscription.getScriptFile());
				}
					
				connection.subscribe(subscription.getTopic(), subscription.getQos());
				logger.info("Successfully subscribed to " + subscription.getTopic());
			}
			
			// Run all configured scripts
			scriptManager.addScripts(connectionSettings.getBackgroundScript());
			for (final Script script : connectionSettings.getBackgroundScript())
			{
				scriptManager.evaluateScriptFile(new File(script.getFile()));
			}
		}
		catch (XMLException e)
		{
			logger.error("Cannot load the mqtt-spy-daemon's configuration", e);
		}
		catch (MqttSpyException e)
		{
			logger.error("Error occurred while connecting to broker", e);
		}
	}
}
