package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.ObservableMessageStoreWithFiltering;

public class UpdateReceivedMessageSummaryEvent implements MqttSpyUIEvent
{
	private final MqttContent added;
	
	private final boolean showTopic;

	private final ObservableMessageStoreWithFiltering store;

	public UpdateReceivedMessageSummaryEvent(final ObservableMessageStoreWithFiltering store, final MqttContent added, final boolean showTopic)
	{
		this.store = store;
		this.added = added;
		this.showTopic = showTopic;
	}
	
	public MqttContent getAdded()
	{
		return added;
	}

	public boolean isShowTopic()
	{
		return showTopic;
	}

	public ObservableMessageStoreWithFiltering getStore()
	{
		return store;
	}
}
