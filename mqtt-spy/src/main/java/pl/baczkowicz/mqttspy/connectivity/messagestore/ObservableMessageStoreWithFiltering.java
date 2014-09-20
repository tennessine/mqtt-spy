package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class ObservableMessageStoreWithFiltering extends ObservableMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(ObservableMessageStoreWithFiltering.class);
	
	/** This is the same as 'show' flag on messages per topic. */
	private final Set<String> filters = new HashSet<String>();
	
	private final MqttMessageStore filteredStore;
	
	private final MessagesPerTopic messagesPerTopic;

	private String name;	
	
	public ObservableMessageStoreWithFiltering(final String name, final int maxSize)
	{
		super(maxSize);
		this.name = name;
		this.messagesPerTopic = new MessagesPerTopic(name);
		this.messagesPerTopic.setFormatter(messageFormat);
		this.filteredStore = new MqttMessageStore(maxSize);
	}
	
	public void messageReceived(final MqttContent message)
	{						
		// Note: this is not FX thread yet
		final boolean allMessagesShown = !filtersEnabled();
		final boolean topicAlreadyExists = messagesPerTopic.topicExists(message.getTopic());			
		
		// Add the message to 'all messages' store - oldest could be removed if the store has reached its max size 
		final MqttContent removed = storeMessage(message);
		
		// Add it to the filtered store if all messages are shown or the topic is already on the list
		if (allMessagesShown || filters.contains(message.getTopic()))
		{
			filteredStore.add(message);
		}

		// If the topic doesn't exist yet, add it (e.g. all shown but this is the first message for this topic)
		if (allMessagesShown && !topicAlreadyExists)
		{
			applyFilter(message.getTopic(), false);	
		}

		// Formats the message with the currently selected formatter
		message.format(getFormatter());
		
		final ObservableMessageStore thisStore = this;
		
		// Note: following require the FX thread
		// TODO: still needs to be optimised
		final boolean selectNewTopic = allMessagesShown && !topicAlreadyExists;
		
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{				
				// Stage 1 - table update - required are: removed message, new message, and whether to show the topic
				
				// Remove old message from stats
				if (removed != null)
				{
					messagesPerTopic.removeOldest(removed);
				}
				
				// Calculate the overall message count per topic
				messagesPerTopic.addAndRemove(message);
				
				// Update the 'show' property if required
				if (selectNewTopic)
				{			
					messagesPerTopic.setShowValue(message.getTopic(), true);											
				}
				
				// Stage 2 - message browsing update
				
				// Notify any observers there is a new message				
				thisStore.notify(message);				
			}		
		});
	}	
	
	@Override
	public Deque<MqttContent> getMessages()
	{		
		return filteredStore.getMessages();
	}
	
	@Override
	public void setFormatter(final FormatterDetails messageFormat)
	{
		super.setFormatter(messageFormat);
		messagesPerTopic.setFormatter(messageFormat);
	}
	
	private void initialiseFilteredStore()
	{
		filteredStore.clear();
		
		for (MqttContent message : super.getMessages())
		{
			if (filters.contains(message.getTopic()))
			{
				filteredStore.add(message);
			}
		}
	}
	
	public boolean applyFilter(final String topic)
	{
		return applyFilter(topic, true);
	}
	
	private boolean applyFilter(final String topic, final boolean recreateStore)
	{
		synchronized (filters)
		{
			if (!filters.contains(topic))
			{
				logger.info("Adding {} to active filters for {}; recreate = {}", topic, name, recreateStore);
				filters.add(topic);
				
				// TODO: optimise
				if (recreateStore)
				{
					logger.warn("Recreating store for {} because of {}", name, topic);
					initialiseFilteredStore();
				}
				
				return true;
			}
			
			return false;
		}
	}
	
	public Set<String> getFilters()
	{
		return Collections.unmodifiableSet(filters);
	}
	
	public boolean removeFilter(final String topic)
	{
		synchronized (filters)
		{
			if (filters.contains(topic))
			{
				logger.info("Removing {} from active filters for {}", topic, name);
				filters.remove(topic);
		
				initialiseFilteredStore();
				
				return true;
			}
			
			return false;
		}
	}
	
	public void removeAllFilters()
	{
		synchronized (filters)
		{
			filters.clear();
			filteredStore.clear();
		}
	}
	
	@Override
	public boolean filtersEnabled()
	{
		return filters.size() != getObservableMessagesPerTopic().size();
	}
	
	public ObservableList<ObservableSubscriptionTopicSummaryProperties> getObservableMessagesPerTopic()
	{
		return messagesPerTopic.getObservableMessagesPerTopic();
	}
	
	@Override
	public void clear()
	{
		super.clear();
		messagesPerTopic.clear();
		removeAllFilters();
	}

	public void setAllShowValues(boolean value)
	{
		messagesPerTopic.setAllShowValues(value);		
	}

	public void toggleAllShowValues()
	{
		messagesPerTopic.toggleAllShowValues();		
	}

	public void setShowValue(final String topic, final boolean value)
	{
		messagesPerTopic.setShowValue(topic, value);		
	}
	
	public String getName()
	{
		return name;
	}
}
