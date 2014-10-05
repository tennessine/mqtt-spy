package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.MessageListWithObservableTopicSummary;

public class TopicSummaryNewMessageEvent implements MqttSpyUIEvent
{
	private final MqttContent added;
	
	private final boolean showTopic;

	private final MessageListWithObservableTopicSummary list;

	public TopicSummaryNewMessageEvent(final MessageListWithObservableTopicSummary list, final MqttContent added, final boolean showTopic)
	{
		this.list = list;
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

	public MessageListWithObservableTopicSummary getList()
	{
		return list;
	}
}
