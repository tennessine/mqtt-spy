package pl.baczkowicz.mqttspy.events;

import java.util.HashMap;
import java.util.Map;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.events.observers.ConnectionStatusChangeObserver;

public class EventManager
{
	final Map<ConnectionStatusChangeObserver, MqttConnection> connectionStatusChangeObserver 
		= new HashMap<ConnectionStatusChangeObserver, MqttConnection>();
	
	/**
	 * 
	 * Registers an observer for connection status changes.
	 * 
	 * @param observer The observer to register
	 * @param filter Null for all, or value to match
	 */
	public void registerConnectionStatusObserver(final ConnectionStatusChangeObserver observer, final MqttConnection filter)
	{
		connectionStatusChangeObserver.put(observer, filter);
	}
	
	public void notifyConnectionStatusChanged(final MqttConnection changedConnection)
	{
		for (final ConnectionStatusChangeObserver observer : connectionStatusChangeObserver.keySet())
		{
			final MqttConnection filter = connectionStatusChangeObserver.get(observer);
			
			if (filter == null || filter.equals(changedConnection))
			{
				observer.onConnectionStatusChanged(changedConnection);
			}
		}
	}
}
