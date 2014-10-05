package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.MessageListWithObservableTopicSummary;

public class TopicSummaryRemovedMessageEvent implements MqttSpyUIEvent
{
	private final MqttContent removed;

	private final MessageListWithObservableTopicSummary list;

	public TopicSummaryRemovedMessageEvent(final MessageListWithObservableTopicSummary list, final MqttContent removed)
	{
		this.list = list;
		this.removed = removed;
	}

	public MqttContent getRemoved()
	{
		return removed;
	}

	public MessageListWithObservableTopicSummary getList()
	{
		return list;
	}
}
