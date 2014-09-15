package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Deque;
import java.util.Observable;

import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;

public class ObservableMessageStore extends Observable implements MessageStore
{
	protected final MqttMessageStore store;
	
	/** The message format used for this message store. */
	protected FormatterDetails messageFormat = FormattingUtils.createBasicFormatter("default", "Plain", ConversionMethod.PLAIN);

	public ObservableMessageStore(final int maxSize)
	{
		this.store = new MqttMessageStore(maxSize);
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
}
