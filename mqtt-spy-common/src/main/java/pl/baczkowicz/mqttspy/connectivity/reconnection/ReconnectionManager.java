package pl.baczkowicz.mqttspy.connectivity.reconnection;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.connectivity.BaseMqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.utils.Utils;

public class ReconnectionManager implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(ReconnectionManager.class);
	
	private final Map<BaseMqttConnection, Runnable> connections = new HashMap<BaseMqttConnection, Runnable>();
	
	private final static int SLEEP = 100;
	
	public void addConnection(final BaseMqttConnection connection, final Runnable connectingRunnable)
	{
		synchronized (connections)
		{
			connections.put(connection, connectingRunnable);
		}
	}
	
	public void removeConnection(final BaseMqttConnection connection)
	{
		synchronized (connections)
		{
			connections.remove(connection);
		}
	}
	
	public void oneCycle()
	{
		for (final BaseMqttConnection connection : connections.keySet())
		{
			if (connection.getConnectionStatus().equals(MqttConnectionStatus.CONNECTING))
			{
				// If already connecting, ignore it
				continue;
			}
			
			final ReconnectionSettings reconnectionSettings = connection.getMqttConnectionDetails().getReconnectionSettings();				
			if (connection.getLastConnectionAttempt() + reconnectionSettings.getRetryInterval() > Utils.getMonotonicTimeInMilliseconds())
			{
				// If we're not due to reconnect yet
				continue;
			}
			
			if (connection.getConnectionStatus().equals(MqttConnectionStatus.DISCONNECTED) 
					|| connection.getConnectionStatus().equals(MqttConnectionStatus.NOT_CONNECTED))
			{
				logger.info("Starting connection {}", connection.getMqttConnectionDetails().getName());
				new Thread(connections.get(connection)).start();
			}
		}			
	}

	public void run()
	{
		while (true)
		{
			synchronized (connections)
			{
				oneCycle();
			}
			
			try
			{
				Thread.sleep(SLEEP);
			}
			catch (InterruptedException e)
			{
				logger.error("Thread interrupted - stopping reconnection manager", e);
				break;
			}
		}		
	}
}
