package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class MessagesPerTopic
{
	final static Logger logger = LoggerFactory.getLogger(MessagesPerTopic.class);
	
	private Map<String, SubscriptionTopicSummary> messagesPerTopic = new HashMap<String, SubscriptionTopicSummary>();

	private final ObservableList<SubscriptionTopicSummary> observableMessagesPerTopic = FXCollections
			.observableArrayList();

	private final String name;

	public MessagesPerTopic(final String name)
	{
		this.name = name;
	}
	
	public void clear()
	{
		messagesPerTopic.clear();
		observableMessagesPerTopic.clear();
	}
	
	public boolean topicExists(final String topic)
	{
		return messagesPerTopic.containsKey(topic);
	}
	
	private void remove(final MqttContent message)
	{
		final SubscriptionTopicSummary value = messagesPerTopic.get(message.getTopic());

		// There should be something in
		if (value != null)
		{
			value.setCount(value.countProperty().intValue() - 1);
		}
		else
		{
			logger.error("Found empty value");
		}		
	}
	
	public void addAndRemove(final MqttContent message, final boolean removeFirst, final FormatterDetails messageFormat)
	{
		if (removeFirst)
		{
			remove(message);
		}

		SubscriptionTopicSummary value = messagesPerTopic.get(message.getTopic());

		if (value == null)
		{
			value = new SubscriptionTopicSummary(false, 1, message, messageFormat);
			messagesPerTopic.put(message.getTopic(), value);
			observableMessagesPerTopic.add(value);
		}
		else
		{
			value.setCount(value.countProperty().intValue() + 1);	
			value.setMessage(message, messageFormat);
		}
		
		logger.trace(name + " has " + (value.countProperty().intValue()) + " messages");
	}

	public ObservableList<SubscriptionTopicSummary> getObservableMessagesPerTopic()
	{
		return observableMessagesPerTopic;
	}
}
