package pl.baczkowicz.mqttspy.daemon.connectivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.common.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.SimpleMqttConnection;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;
import pl.baczkowicz.mqttspy.utils.ConnectionUtils;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

public class ConnectionRunnable implements Runnable
{
	final static Logger logger = LoggerFactory.getLogger(ConnectionRunnable.class);
	
	private final SimpleMqttConnection connection;
	
	private final DaemonMqttConnectionDetails connectionSettings;

	private final ScriptManager scriptManager;

	public ConnectionRunnable(final ScriptManager scriptManager, final SimpleMqttConnection connection, final DaemonMqttConnectionDetails connectionSettings)
	{
		this.connection = connection;
		this.connectionSettings = connectionSettings;
		this.scriptManager = scriptManager;
	}
	
	public void run()
	{
		Thread.currentThread().setName("Connection " + connection.getMqttConnectionDetails().getName());
		ThreadingUtils.logStarting();
		
		final ReconnectionSettings reconnectionSettings = connection.getMqttConnectionDetails().getReconnectionSettings();
		
		final boolean neverStarted = connection.getLastConnectionAttemptTimestamp() == ConnectionUtils.NEVER_STARTED;
		
		// If successfully connected, and re-subscription is configured
		if (connection.connect() 
				&& (neverStarted || (reconnectionSettings != null && reconnectionSettings.isResubscribe())))
		{
			// Subscribe to all configured subscriptions
			for (final SubscriptionDetails subscription : connectionSettings.getSubscription())
			{	
				if (neverStarted && subscription.getScriptFile() != null)
				{
					scriptManager.addScript(subscription.getScriptFile());
				}
					
				connection.subscribe(subscription.getTopic(), subscription.getQos());							
			}
		}
		
		ThreadingUtils.logEnding();
	}				
}
