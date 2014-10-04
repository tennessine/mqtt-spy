package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.MessageListWithObservableTopicSummary;

public class RemoveMessageEvent implements MqttSpyUIEvent
{
	private final MqttContent removed;

	private final MessageListWithObservableTopicSummary store;

	public RemoveMessageEvent(final MessageListWithObservableTopicSummary store, final MqttContent removed)
	{
		this.store = store;
		this.removed = removed;
	}

	public MqttContent getRemoved()
	{
		return removed;
	}

	public MessageListWithObservableTopicSummary getStore()
	{
		return store;
	}
}
