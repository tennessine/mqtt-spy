package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Deque;
import java.util.Observable;
import java.util.Queue;

import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;

public class ObservableMessageStore extends Observable implements MessageStore
{
	protected final MqttMessageStore store;
	
	/** The message format used for this message store. */
	protected FormatterDetails messageFormat = FormattingUtils.createBasicFormatter("default", "Plain", ConversionMethod.PLAIN);

	/** Stores events for the UI to be updated. */
	protected Queue<MqttSpyUIEvent> uiEventQueue;

	private final String name;	
	
	public ObservableMessageStore(final String name, final int maxSize, final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		this.store = new MqttMessageStore(maxSize);
		this.uiEventQueue = uiEventQueue;
		this.name = name;
	}
	
	public MqttContent storeMessage(final MqttContent message)
	{
		if (message != null)
		{
			return store.add(message);
		}	
		
		return null;
	}
	
	public void notify(final MqttContent message)
	{
		// Notifies the observers
		this.setChanged();
		this.notifyObservers(message);
	}

	public Deque<MqttContent> getMessages()
	{
		return store.getMessages();
	}

	public void clear()
	{
		store.clear();
	}	

	public void setFormatter(final FormatterDetails messageFormat)
	{
		this.messageFormat = messageFormat;		
	}
	
	public FormatterDetails getFormatter()
	{
		return this.messageFormat;
	}
	
	public boolean filtersEnabled()
	{
		return false;
	}

	public String getName()
	{
		return name;
	}
}
