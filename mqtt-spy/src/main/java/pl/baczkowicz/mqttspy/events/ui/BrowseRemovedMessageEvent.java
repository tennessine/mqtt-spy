package pl.baczkowicz.mqttspy.events.ui;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.MessageListWithObservableTopicSummary;

public class BrowseRemovedMessageEvent implements MqttSpyUIEvent
{
	private final MqttContent message;
	
	private final int messageIndex;

	private final MessageListWithObservableTopicSummary store;

	public BrowseRemovedMessageEvent(final MessageListWithObservableTopicSummary store, final MqttContent message, final int messageIndex)
	{
		this.store = store;
		this.message = message;
		this.messageIndex = messageIndex;
	}

	public MqttContent getMessage()
	{
		return message;
	}

	public int getMessageIndex()
	{
		return messageIndex;
	}

	public MessageListWithObservableTopicSummary getList()
	{
		return store;
	}
}
