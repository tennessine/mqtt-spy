package pl.baczkowicz.mqttspy.storage;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.storage.summary.ObservableTopicSummary;

public class MessageListWithObservableTopicSummary extends MessageList
{
	private final ObservableTopicSummary topicSummary;
	
	public MessageListWithObservableTopicSummary(int preferredSize, int maxSize, String name, FormatterDetails messageFormat)
	{
		super(preferredSize, maxSize, name);
				
		this.topicSummary = new ObservableTopicSummary(name);
		this.topicSummary.setFormatter(messageFormat);
	}

	public ObservableTopicSummary getTopicSummary()
	{
		return topicSummary;
	}
	
	public MqttContent add(final MqttContent message)
	{
		final MqttContent removed = super.add(message);
		
		if (removed != null)
		{
			topicSummary.decreaseCount(removed);
		}
		topicSummary.increaseCount(message);
		
		return removed;
	}
	
	public MqttContent remove(final int index)
	{
		final MqttContent removed = super.remove(index);
		
		topicSummary.decreaseCount(removed);
		
		return removed;
	}
}
