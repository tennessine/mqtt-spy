package pl.baczkowicz.mqttspy.storage.summary;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class TopicSummary extends TopicMessageCount
{
	final static Logger logger = LoggerFactory.getLogger(TopicSummary.class);
	
	protected Map<String, SubscriptionTopicSummaryProperties> topicToSummaryMapping = new HashMap<>();
	
	protected FormatterDetails messageFormat;

	public TopicSummary(final String name)
	{
		super(name);
	}
	
	public void clear()
	{
		synchronized (topicToSummaryMapping)
		{
			super.clear();
			topicToSummaryMapping.clear();
		}
	}
	
	public void remove(final MqttContent message)
	{
		synchronized (topicToSummaryMapping)
		{
			final SubscriptionTopicSummaryProperties value = topicToSummaryMapping.get(message.getTopic());
	
			// There should be something in
			if (value != null)
			{
				value.setCount(value.countProperty().intValue() - 1);
			}
			else
			{
				logger.error("[{}] Found empty value for topic {}", name, message.getTopic());
			}
		}
	}
	
	public SubscriptionTopicSummaryProperties add(final MqttContent message)
	{
		SubscriptionTopicSummaryProperties newElement = null;
		
		synchronized (topicToSummaryMapping)
		{
			SubscriptionTopicSummaryProperties value = topicToSummaryMapping.get(message.getTopic());
	
			if (value == null)
			{
				value = new SubscriptionTopicSummaryProperties(false, 1, message, messageFormat);
				topicToSummaryMapping.put(message.getTopic(), value);
				newElement = value;
			}
			else
			{
				value.setCount(value.countProperty().intValue() + 1);	
				value.setMessage(message, messageFormat);				
			}
			
			logger.trace("[{}] has {} messages", name, value.countProperty().intValue());
		}		
		
		return newElement;
	}

	public void setFormatter(final FormatterDetails messageFormat)
	{
		this.messageFormat = messageFormat;		
	}
}
