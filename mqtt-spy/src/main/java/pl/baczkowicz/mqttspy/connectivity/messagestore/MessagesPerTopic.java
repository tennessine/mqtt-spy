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
	
	private Map<String, SubscriptionTopicSummary> topicToSummaryMapping = new HashMap<String, SubscriptionTopicSummary>();

	private final ObservableList<SubscriptionTopicSummary> observableTopicSummaryList = FXCollections.observableArrayList();

	private final String name;

	public MessagesPerTopic(final String name)
	{
		this.name = name;
	}
	
	public void clear()
	{
		topicToSummaryMapping.clear();
		observableTopicSummaryList.clear();
	}
	
	public boolean topicExists(final String topic)
	{
		return topicToSummaryMapping.containsKey(topic);
	}
	
	public void removeOldest(final MqttContent message)
	{
		final SubscriptionTopicSummary value = topicToSummaryMapping.get(message.getTopic());

		// There should be something in
		if (value != null)
		{
			value.setCount(value.countProperty().intValue() - 1);
			
			// TODO: repopulate the observable list
		}
		else
		{
			logger.error("Found empty value");
		}		
	}
	
	public void addAndRemove(final MqttContent message, final FormatterDetails messageFormat)
	{
		SubscriptionTopicSummary value = topicToSummaryMapping.get(message.getTopic());

		if (value == null)
		{
			value = new SubscriptionTopicSummary(false, 1, message, messageFormat);
			topicToSummaryMapping.put(message.getTopic(), value);
			observableTopicSummaryList.add(value);
		}
		else
		{
			// TODO: this might not be enough, because the list needs to be updated, not just the value
			value.setCount(value.countProperty().intValue() + 1);	
			value.setMessage(message, messageFormat);
		}
		
		logger.trace(name + " has " + (value.countProperty().intValue()) + " messages");
	}
	

	public void toggleAllShowValues()
	{
		for (final SubscriptionTopicSummary item : observableTopicSummaryList)
		{
			item.showProperty().set(!item.showProperty().get());
		}
	}
	
	public void setShowValue(final String topic, final boolean value)
	{
		for (final SubscriptionTopicSummary item : observableTopicSummaryList)
		{
			if (item.topicProperty().getValue().equals(topic))
			{
				item.showProperty().set(value);
				break;
			}
		}
	}
	
	public void setAllShowValues(final boolean value)
	{
		for (final SubscriptionTopicSummary item : observableTopicSummaryList)
		{
			item.showProperty().set(value);
		}
	}

	public ObservableList<SubscriptionTopicSummary> getObservableMessagesPerTopic()
	{
		return observableTopicSummaryList;
	}
}
