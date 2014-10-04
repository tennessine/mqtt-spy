package pl.baczkowicz.mqttspy.events;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.events.observers.ClearTabObserver;
import pl.baczkowicz.mqttspy.events.observers.ConnectionStatusChangeObserver;
import pl.baczkowicz.mqttspy.events.observers.MessageFormatChangeObserver;
import pl.baczkowicz.mqttspy.events.observers.MessageIndexChangeObserver;
import pl.baczkowicz.mqttspy.events.observers.MessageIndexIncrementObserver;
import pl.baczkowicz.mqttspy.events.observers.MessageIndexToFirstObserver;
import pl.baczkowicz.mqttspy.events.observers.MqttContentObserver;
import pl.baczkowicz.mqttspy.events.observers.NewMessageObserver;
import pl.baczkowicz.mqttspy.events.observers.ScriptStateChangeObserver;
import pl.baczkowicz.mqttspy.events.observers.SubscriptionStatusChangeObserver;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.storage.MessageStore;
import pl.baczkowicz.mqttspy.storage.ObservableMessageStore;
import pl.baczkowicz.mqttspy.storage.ObservableMessageStoreWithFiltering;

public class EventManager
{
	// final static Logger logger = LoggerFactory.getLogger(EventManager.class);
	
	private final Map<MqttContentObserver, ObservableMessageStore> mqttContentObservers = new HashMap<>();
	
	private final Map<ConnectionStatusChangeObserver, MqttConnection> connectionStatusChangeObservers = new HashMap<>();
	
	private final Map<SubscriptionStatusChangeObserver, MqttSubscription> subscriptionStatusChangeObservers = new HashMap<>();
	
	private final Map<ClearTabObserver, ObservableMessageStoreWithFiltering> clearTabObservers = new HashMap<>();

	private final Map<NewMessageObserver, MessageStore> newMessageObservers = new HashMap<>();
	
	private final Map<MessageIndexChangeObserver, MessageStore> changeMessageIndexObservers = new HashMap<>();
	
	private final Map<MessageIndexToFirstObserver, MessageStore> displayFirstMessageObservers = new HashMap<>();
	
	private final Map<MessageIndexIncrementObserver, MessageStore> incrementMessageIndexObservers = new HashMap<>();
	
	private final Map<MessageFormatChangeObserver, MessageStore> formatChangeObservers = new HashMap<>();
	
	private final Map<ScriptStateChangeObserver, String> scriptStateChangeObservers = new HashMap<>();
	
	/**
	 * 
	 * Registers an observer for connection status changes.
	 * 
	 * @param observer The observer to register
	 * @param filter Null for all, or value to match
	 */
	public void registerConnectionStatusObserver(final ConnectionStatusChangeObserver observer, final MqttConnection filter)
	{
		connectionStatusChangeObservers.put(observer, filter);
	}
	
	public void registerMqttContentObserver(final MqttContentObserver observer, final ObservableMessageStore filter)
	{
		mqttContentObservers.put(observer, filter);
	}
	
	public void registerSubscriptionStatusObserver(final SubscriptionStatusChangeObserver observer, final MqttSubscription filter)
	{
		subscriptionStatusChangeObservers.put(observer, filter);
	}
	
	public void deregisterConnectionStatusObserver(final ConnectionStatusChangeObserver observer)
	{
		connectionStatusChangeObservers.remove(observer);
	}
	
	public void registerClearTabObserver(final ClearTabObserver observer, final ObservableMessageStoreWithFiltering filter)
	{
		clearTabObservers.put(observer, filter);
	}
	
	public void registerNewMessageObserver(final NewMessageObserver observer, final MessageStore filter)
	{
		newMessageObservers.put(observer, filter);
	}
	
	public void registerChangeMessageIndexObserver(final MessageIndexChangeObserver observer, final MessageStore filter)
	{
		changeMessageIndexObservers.put(observer, filter);
	}
	
	public void registerChangeMessageIndexFirstObserver(final MessageIndexToFirstObserver observer, final MessageStore filter)
	{
		displayFirstMessageObservers.put(observer, filter);
	}
	
	public void registerIncrementMessageIndexObserver(final MessageIndexIncrementObserver observer, final MessageStore filter)
	{
		incrementMessageIndexObservers.put(observer, filter);
	}
	
	public void registerFormatChangeObserver(final MessageFormatChangeObserver observer, final MessageStore filter)
	{
		formatChangeObservers.put(observer, filter);
	}
	
	public void deregisterFormatChangeObserver(MessageFormatChangeObserver observer)
	{
		formatChangeObservers.remove(observer);		
	}
	
	public void registerScriptStateChangeObserver(final ScriptStateChangeObserver observer, final String filter)
	{
		scriptStateChangeObservers.put(observer, filter);
	}
	
	public void notifyMqttContentReceived(final ObservableMessageStore store, final MqttContent message)
	{
		for (final MqttContentObserver observer : mqttContentObservers.keySet())
		{
			final ObservableMessageStore filter = mqttContentObservers.get(observer);
			
			if (filter == null || filter.equals(store))
			{				
				observer.onMqttContentReceived(message);
			}			
		}				
	}
	
	public void notifyConnectionStatusChanged(final MqttConnection changedConnection)
	{
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				for (final ConnectionStatusChangeObserver observer : connectionStatusChangeObservers.keySet())
				{
					final MqttConnection filter = connectionStatusChangeObservers.get(observer);
					
					if (filter == null || filter.equals(changedConnection))
					{				
						observer.onConnectionStatusChanged(changedConnection);
					}
				}				
			}
		});		
	}
	
	public void notifySubscriptionStatusChanged(final MqttSubscription changedSubscription)
	{
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				for (final SubscriptionStatusChangeObserver observer : subscriptionStatusChangeObservers.keySet())
				{
					final MqttSubscription filter = subscriptionStatusChangeObservers.get(observer);
					
					if (filter == null || filter.equals(changedSubscription))
					{				
						observer.onSubscriptionStatusChanged(changedSubscription);
					}
				}				
			}
		});		
		
	}
		
	public void notifyFormatChanged(final MessageStore store)
	{
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				for (final MessageFormatChangeObserver observer : formatChangeObservers.keySet())
				{
					final MessageStore filter = formatChangeObservers.get(observer);
					
					if (filter == null || filter.equals(store))
					{				
						observer.onFormatChange();
					}
				}				
			}
		});		
	}
	
	public void notifyNewMessageAvailable(final MessageStore store)
	{
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				for (final NewMessageObserver observer : newMessageObservers.keySet())
				{
					final MessageStore filter = newMessageObservers.get(observer);
					
					if (filter == null || filter.equals(store))
					{				
						observer.onNewMessageReceived();
					}
				}				
			}
		});		
	}
	
	public void changeMessageIndexToFirst(final MessageStore store)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (final MessageIndexToFirstObserver observer : displayFirstMessageObservers.keySet())
				{
					final MessageStore filter = displayFirstMessageObservers.get(observer);

					if (filter == null || filter.equals(store))
					{
						observer.onMessageIndexToFirstChange();
					}
				}
			}
		});
	}
	
	public void changeMessageIndex(final MessageStore store, final Object dispatcher, final int index)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (final MessageIndexChangeObserver observer : changeMessageIndexObservers.keySet())
				{
					final MessageStore filter = changeMessageIndexObservers.get(observer);

					if ((filter == null || filter.equals(store)) && (dispatcher != observer))
					{
						observer.onMessageIndexChange(index);
					}
				}
			}
		});
	}
	
	public void incrementMessageIndex(final MessageStore store)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (final MessageIndexIncrementObserver observer : incrementMessageIndexObservers.keySet())
				{
					final MessageStore filter = incrementMessageIndexObservers.get(observer);

					if (filter == null || filter.equals(store))
					{
						observer.onMessageIndexIncrement();
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

	public void notifyClearHistory(final ObservableMessageStoreWithFiltering store)
	{
		for (final ClearTabObserver observer : clearTabObservers.keySet())
		{
			final ObservableMessageStoreWithFiltering filter = clearTabObservers.get(observer);
			
			if (filter == null || filter.equals(store))
			{
				observer.onClearTab(store);
			}
		}
	}

	public void notifyScriptStateChange(final String scriptName, final ScriptRunningState state)
	{
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{
				for (final ScriptStateChangeObserver observer : scriptStateChangeObservers.keySet())
				{
					final String filter = scriptStateChangeObservers.get(observer);

					if (filter == null || filter.equals(scriptName))
					{
						observer.onScriptStateChange(scriptName, state);
					}
				}
			}
		});
		
	}
}
