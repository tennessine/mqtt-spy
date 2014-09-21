package pl.baczkowicz.mqttspy.events;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.observers.ClearTabObserver;
import pl.baczkowicz.mqttspy.events.observers.ConnectionStatusChangeObserver;

public class EventManager
{
	final Map<ConnectionStatusChangeObserver, MqttConnection> connectionStatusChangeObserver 
		= new HashMap<ConnectionStatusChangeObserver, MqttConnection>();
	
	private Map<ClearTabObserver, ObservableMessageStoreWithFiltering> clearTabObserver
		= new HashMap<ClearTabObserver, ObservableMessageStoreWithFiltering>();
	
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
	
	public void deregisterConnectionStatusObserver(final ConnectionStatusChangeObserver observer)
	{
		connectionStatusChangeObserver.remove(observer);
	}
	
	public void registerClearTabObserver(final ClearTabObserver observer, final ObservableMessageStoreWithFiltering filter)
	{
		clearTabObserver.put(observer, filter);
	}
	
	public void notifyConnectionStatusChanged(final MqttConnection changedConnection)
	{
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
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
		});
		
	}

	public void notifyConfigurationFileWriteFailure()
	{
		// TODO Auto-generated method stub
	}

	public void notifyConfigurationFileCopyFailure()
	{
		// TODO Auto-generated method stub		
	}

	public void notifyConfigurationFileReadFailure()
	{
		// TODO Auto-generated method stub
	}

	public void notifyClearHistory(ObservableMessageStoreWithFiltering store)
	{
		for (final ClearTabObserver observer : clearTabObserver.keySet())
		{
			final ObservableMessageStoreWithFiltering filter = clearTabObserver.get(observer);
			
			if (filter == null || filter.equals(store))
			{
				observer.onClearTab(store);
			}
		}
	}
}
