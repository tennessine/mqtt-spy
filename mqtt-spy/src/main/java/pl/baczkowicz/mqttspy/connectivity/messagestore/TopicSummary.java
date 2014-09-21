package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class TopicSummary
{
	final static Logger logger = LoggerFactory.getLogger(TopicSummary.class);
	
	private Map<String, SubscriptionTopicSummaryProperties> topicToSummaryMapping = new HashMap<String, SubscriptionTopicSummaryProperties>();

	private final ObservableList<SubscriptionTopicSummaryProperties> observableTopicSummaryList = FXCollections.observableArrayList();

	private final String name;

	private FormatterDetails messageFormat;

	public TopicSummary(final String name)
	{
		this.name = name;
	}
	
	public void clear()
	{
		topicToSummaryMapping.clear();
		observableTopicSummaryList.clear();
	}
	
	public void removeOldest(final MqttContent message)
	{
		final SubscriptionTopicSummaryProperties value = topicToSummaryMapping.get(message.getTopic());

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
	
	public void addAndRemove(final MqttContent message)
	{
		SubscriptionTopicSummaryProperties value = topicToSummaryMapping.get(message.getTopic());

		if (value == null)
		{
			value = new SubscriptionTopicSummaryProperties(false, 1, message, messageFormat);
			topicToSummaryMapping.put(message.getTopic(), value);
			observableTopicSummaryList.add(value);
		}
		else
		{
			value.setCount(value.countProperty().intValue() + 1);	
			value.setMessage(message, messageFormat);
		}
		
		logger.trace(name + " has " + (value.countProperty().intValue()) + " messages");
	}

	public void toggleAllShowValues()
	{
		for (final SubscriptionTopicSummaryProperties item : observableTopicSummaryList)
		{
			item.showProperty().set(!item.showProperty().get());
		}
	}
	
	public void setShowValue(final String topic, final boolean value)
	{
		for (final SubscriptionTopicSummaryProperties item : observableTopicSummaryList)
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
		for (final SubscriptionTopicSummaryProperties item : observableTopicSummaryList)
		{
			item.showProperty().set(value);
		}
	}

	public ObservableList<SubscriptionTopicSummaryProperties> getObservableMessagesPerTopic()
	{
		return observableTopicSummaryList;
	}

	public void setFormatter(final FormatterDetails messageFormat)
	{
		this.messageFormat = messageFormat;		
	}
}
