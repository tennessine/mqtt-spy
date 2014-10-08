package pl.baczkowicz.mqttspy.storage;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javafx.collections.transformation.FilteredList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.ui.BrowseReceivedMessageEvent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.events.ui.TopicSummaryNewMessageEvent;
import pl.baczkowicz.mqttspy.events.ui.TopicSummaryRemovedMessageEvent;
import pl.baczkowicz.mqttspy.ui.properties.SubscriptionTopicSummaryProperties;

public class ManagedMessageStoreWithFiltering extends BasicMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(ManagedMessageStoreWithFiltering.class);
	
	/** All topics this store knows about. */
	private final Set<String> allTopics = new HashSet<String>();
	
	private FilteredMessageStore filteredStore;
	
	public ManagedMessageStoreWithFiltering(final String name, final int minMessagesPerTopic, final int preferredSize, final int maxSize, 
			final Queue<MqttSpyUIEvent> uiEventQueue, final EventManager eventManager)
	{
		super(name, preferredSize, maxSize, uiEventQueue, eventManager);
		
		filteredStore = new FilteredMessageStore(messages, preferredSize, maxSize, name, messageFormat);		
		
		new Thread(new MessageStoreGarbageCollector(this, messages, uiEventQueue, minMessagesPerTopic, true, false)).start();
		new Thread(new MessageStoreGarbageCollector(this, filteredStore.getFilteredMessages(), uiEventQueue, minMessagesPerTopic, false, true)).start();
	}
	
	/**
	 * Stores the received message and triggers UI updates. The following
	 * updates are queued as UI events so that the JavaFX thread is not swamped
	 * with hundreds or thousands of requests to do Platform.runLater().
	 * 
	 * @param message Received message
	 */
	public void messageReceived(final MqttContent message)
	{	
		// Record the current state of topics
		final boolean allTopicsShown = !filtersEnabled();		
		final boolean topicAlreadyExists = allTopics.contains(message.getTopic());
		
		// Start processing the received message...
		
		// 1. Store the topic for the received message
		allTopics.add(message.getTopic());
		
		// 2. Add the message to 'all messages' store - oldest could be removed if the store has reached its max size 
		final MqttContent removed = storeMessage(message);
		
		// 3. Add it to the filtered store if all messages are shown or the topic is already on the list
		if (allTopicsShown || filteredStore.getShownTopics().contains(message.getTopic()))
		{
			filteredStore.getFilteredMessages().add(message);
			
			// Message browsing update
			uiEventQueue.add(new BrowseReceivedMessageEvent(filteredStore.getFilteredMessages(), message));
		}

		// 4. If the topic doesn't exist yet, add it (e.g. all shown but this is the first message for this topic)
		if (allTopicsShown && !topicAlreadyExists)
		{
			// This doesn't need to trigger 'show first' or sth because the following two UI events should refresh the screen
			filteredStore.applyFilter(message.getTopic(), false);	 
		}

		// 5. Formats the message with the currently selected formatter
		message.format(getFormatter());			
		
		// 6. Summary table update - required are: removed message, new message, and whether to show the topic
		if (removed != null)
		{
			uiEventQueue.add(new TopicSummaryRemovedMessageEvent(messages, removed));
		}
		uiEventQueue.add(new TopicSummaryNewMessageEvent(messages, message, allTopicsShown && !topicAlreadyExists));
	}	
	
	@Override
	public List<MqttContent> getMessages()
	{		
		return filteredStore.getFilteredMessages().getMessages();
	}
	
	@Override
	public MessageListWithObservableTopicSummary getMessageList()
	{
		return filteredStore.getFilteredMessages();
	}
	
	public MessageListWithObservableTopicSummary getNonFilteredMessageList()
	{
		return messages;
	}
	
	public FilteredMessageStore getFilteredMessageStore()
	{
		return filteredStore;
	}	
	
	@Override
	public boolean filtersEnabled()
	{
		return filteredStore.getShownTopics().size() != allTopics.size();
	}

	@Override
	public void clear()
	{
		super.clear();
		allTopics.clear();
		filteredStore.removeAllFilters();
	}

	public void setAllShowValues(final boolean show, final FilteredList<SubscriptionTopicSummaryProperties> filteredData)
	{
		// TODO: process filtered data
		
		if (show)
		{
			filteredStore.addAllFilters();
		}
		else
		{
			filteredStore.removeAllFilters();
		}
		
		// filteredStore.updateFilter(allTopics, show);
		messages.getTopicSummary().setAllShowValues(show);
	}

	public void toggleAllShowValues(final FilteredList<SubscriptionTopicSummaryProperties> filteredData)
	{
		// TODO: process filtered data
		final Set<String> topicsToShow = new HashSet<>();
		for (final String topic : allTopics)
		{
			//filteredStore.updateFilter(topic, !filteredStore.getShownTopics().contains(topic));			
			if (!filteredStore.getShownTopics().contains(topic))				
			{
				topicsToShow.add(topic);
			}
		}
		filteredStore.removeAllFilters();
		filteredStore.applyFilter(topicsToShow, true);
		// filteredStore.updateFilter(topicsToShow, true);
		
		messages.getTopicSummary().toggleAllShowValues();
	}

	public void setShowValue(final String topic, final boolean show)
	{
		filteredStore.updateFilter(topic, show);
		messages.getTopicSummary().setShowValue(topic, show);
	}
}
