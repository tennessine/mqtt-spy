package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.ObservableMessageStoreWithFiltering;

public class BrowseReceivedMessageEvent implements MqttSpyUIEvent
{
	private final MqttContent message;
	
	private final ObservableMessageStoreWithFiltering store;

	public BrowseReceivedMessageEvent(final ObservableMessageStoreWithFiltering store, final MqttContent message)
	{
		this.store = store;
		this.message = message;
	}

	public MqttContent getMessage()
	{
		return message;
	}

	public ObservableMessageStoreWithFiltering getStore()
	{
		return store;
	}
}
