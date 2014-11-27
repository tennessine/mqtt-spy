/**
 * 
 */
package pl.baczkowicz.mqttspy.daemon;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import pl.baczkowicz.mqttspy.configuration.PropertyFileLoader;
import pl.baczkowicz.mqttspy.connectivity.BaseMqttConnection;
import pl.baczkowicz.mqttspy.connectivity.SimpleMqttAsyncConnection;
import pl.baczkowicz.mqttspy.connectivity.reconnection.ReconnectionManager;
import pl.baczkowicz.mqttspy.daemon.configuration.ConfigurationLoader;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.RunningMode;
import pl.baczkowicz.mqttspy.daemon.connectivity.ConnectionRunnable;
import pl.baczkowicz.mqttspy.daemon.connectivity.MqttCallbackHandler;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;
import pl.baczkowicz.mqttspy.utils.Utils;

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
			final MqttCallbackHandler callback = new MqttCallbackHandler(connection, connectionSettings, scriptManager); 
			connection.createClient(callback);
					
			final ReconnectionSettings reconnectionSettings = connection.getMqttConnectionDetails().getReconnectionSettings();			
			final Runnable connectionRunnable = new ConnectionRunnable(scriptManager, connection, connectionSettings);
			ReconnectionManager reconnectionManager = null;
			
			if (reconnectionSettings == null)
			{
				new Thread(connectionRunnable).start();
			}
			else
			{
				reconnectionManager = new ReconnectionManager();
				reconnectionManager.addConnection(connection, connectionRunnable);
				new Thread(reconnectionManager).start();
			}
			
			// Run all configured scripts
			scriptManager.addScripts(connectionSettings.getBackgroundScript());
			for (final ScriptDetails script : connectionSettings.getBackgroundScript())
			{
				scriptManager.runScriptFile(new File(script.getFile()));
			}
			
			if (RunningMode.SCRIPTS_ONLY.equals(connectionSettings.getRunningMode()))
			{
				stop(scriptManager, reconnectionManager, connection, reconnectionManager);
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
	
	private static void stop(final ScriptManager scriptManager, final ReconnectionManager reconnectionManager, 
			final BaseMqttConnection connection, final ReconnectionManager callback)
	{
		Utils.sleep(1000);
		
		// Wait until all scripts have completed or got frozen
		while (scriptManager.areScriptsRunning())
		{
			Utils.sleep(1000);
		}
		
		// Stop reconnection manager
		if (reconnectionManager != null)
		{
			reconnectionManager.stop();
		}
						
		// Disconnect
		connection.disconnect();
		
		// Stop message logger
		callback.stop();
		
		Utils.sleep(1000);
		logger.info("All tasks completed - bye bye...");
	}
}
