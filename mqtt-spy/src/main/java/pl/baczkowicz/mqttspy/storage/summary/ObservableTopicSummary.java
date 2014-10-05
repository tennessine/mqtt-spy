package pl.baczkowicz.mqttspy.storage.summary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class ObservableTopicSummary extends TopicSummary
{
	final static Logger logger = LoggerFactory.getLogger(ObservableTopicSummary.class);
	
	private final ObservableList<SubscriptionTopicSummaryProperties> observableTopicSummaryList = FXCollections.observableArrayList();

	public ObservableTopicSummary(final String name)
	{
		super(name);
	}
	
	public void clear()
	{
		synchronized (topicToSummaryMapping)
		{
			super.clear();
			observableTopicSummaryList.clear();
		}
	}
	
	public SubscriptionTopicSummaryProperties addMessage(final MqttContent message)
	{
		synchronized (topicToSummaryMapping)
		{
			SubscriptionTopicSummaryProperties newAdded = super.addMessage(message);
			
			if (newAdded != null)
			{				
				observableTopicSummaryList.add(newAdded);
			}
			
			return newAdded;
		}				
	}

	public void toggleAllShowValues()
	{
		synchronized (topicToSummaryMapping)
		{
			for (final SubscriptionTopicSummaryProperties item : observableTopicSummaryList)
			{
				item.showProperty().set(!item.showProperty().get());
			}
		}
	}
	
	public void setShowValue(final String topic, final boolean value)
	{
		synchronized (topicToSummaryMapping)
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
	}
	
	public void setAllShowValues(final boolean value)
	{
		synchronized (topicToSummaryMapping)
		{
			for (final SubscriptionTopicSummaryProperties item : observableTopicSummaryList)
			{
				item.showProperty().set(value);
			}
		}
	}

	public ObservableList<SubscriptionTopicSummaryProperties> getObservableMessagesPerTopic()
	{
		return observableTopicSummaryList;
	}
}
