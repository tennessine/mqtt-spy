package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;

public class UpdateReceivedMessageSummaryEvent implements MqttSpyUIEvent
{
	private final MqttContent removed;
	
	private final MqttContent added;
	
	private final boolean showTopic;

	private final ObservableMessageStoreWithFiltering store;

	public UpdateReceivedMessageSummaryEvent(final ObservableMessageStoreWithFiltering store, final MqttContent removed, final MqttContent added, final boolean showTopic)
	{
		this.store = store;
		this.removed = removed;
		this.added = added;
		this.showTopic = showTopic;
	}

	public MqttContent getRemoved()
	{
		return removed;
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
