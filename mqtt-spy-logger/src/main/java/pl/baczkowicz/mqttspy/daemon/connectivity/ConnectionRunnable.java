package pl.baczkowicz.mqttspy.daemon.connectivity;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.common.generated.SubscriptionDetails;
import pl.baczkowicz.mqttspy.connectivity.BaseMqttConnection;
import pl.baczkowicz.mqttspy.connectivity.SimpleMqttAsyncConnection;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.scripts.ScriptManager;

public class ConnectionRunnable implements Runnable
{
	private final SimpleMqttAsyncConnection connection;
	
	private final DaemonMqttConnectionDetails connectionSettings;

	private final ScriptManager scriptManager;

	public ConnectionRunnable(final ScriptManager scriptManager, final SimpleMqttAsyncConnection connection, final DaemonMqttConnectionDetails connectionSettings)
	{
		this.connection = connection;
		this.connectionSettings = connectionSettings;
		this.scriptManager = scriptManager;
	}
	
	public void run()
	{
		final ReconnectionSettings reconnectionSettings = connection.getMqttConnectionDetails().getReconnectionSettings();
		
		final boolean neverStarted = connection.getLastConnectionAttemptTimestamp() == BaseMqttConnection.NEVER_STARTED;
		
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
	}				
}
