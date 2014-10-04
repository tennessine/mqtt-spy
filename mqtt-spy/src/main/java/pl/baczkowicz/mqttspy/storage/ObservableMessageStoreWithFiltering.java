package pl.baczkowicz.mqttspy.storage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.ui.BrowseReceivedMessageEvent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.events.ui.RemoveMessageEvent;
import pl.baczkowicz.mqttspy.events.ui.UpdateReceivedMessageSummaryEvent;

public class ObservableMessageStoreWithFiltering extends ObservableMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(ObservableMessageStoreWithFiltering.class);
	
	/** This is the same as 'show' flag on topic summary. */
	private final Set<String> shownTopics = new HashSet<String>();
	
	/** All topics this store knows about. */
	private final Set<String> allTopics = new HashSet<String>();
	
	private final MessageListWithObservableTopicSummary filteredStore;
			
	public ObservableMessageStoreWithFiltering(final String name, final int minMessagesPerTopic, final int preferredSize, final int maxSize, 
			final Queue<MqttSpyUIEvent> uiEventQueue, final EventManager eventManager)
	{
		super(name, preferredSize, maxSize, uiEventQueue, eventManager);		
		
		this.filteredStore = new MessageListWithObservableTopicSummary(preferredSize, maxSize, "filtered-" + name, messageFormat);
		
		new Thread(new MessageStoreGarbageCollector(store, uiEventQueue, minMessagesPerTopic, true, false)).start();
		new Thread(new MessageStoreGarbageCollector(filteredStore, uiEventQueue, minMessagesPerTopic, false, true)).start();
	}
	
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
		if (allTopicsShown || shownTopics.contains(message.getTopic()))
		{
			filteredStore.add(message);
		}

		// 4. If the topic doesn't exist yet, add it (e.g. all shown but this is the first message for this topic)
		if (allTopicsShown && !topicAlreadyExists)
		{
			// This doesn't need to trigger 'show first' or sth because the following two UI events should refresh the screen
			applyFilter(message.getTopic(), false);	 
		}

		// 5. Formats the message with the currently selected formatter
		message.format(getFormatter());
		
		// 6. The following updates are queued so that the JavaFX thread is not swamped with hundreds or thousands of requests to do Platform.runLater()
		// Message browsing update
		uiEventQueue.add(new BrowseReceivedMessageEvent(this, message));		
		
		// Summary table update - required are: removed message, new message, and whether to show the topic
		if (removed != null)
		{
			uiEventQueue.add(new RemoveMessageEvent(store, removed));
		}
		uiEventQueue.add(new UpdateReceivedMessageSummaryEvent(this, message, allTopicsShown && !topicAlreadyExists));
	}	
	
	@Override
	public List<MqttContent> getMessages()
	{		
		return filteredStore.getMessages();
	}

	private void initialiseFilteredStore()
	{
		filteredStore.clear();
		
		for (MqttContent message : super.getMessages())
		{
			if (shownTopics.contains(message.getTopic()))
			{
				filteredStore.add(message);
			}
		}
	}
	
	private boolean applyFilter(final String topic, final boolean recreateStore)
	{
		synchronized (shownTopics)
		{
			if (!shownTopics.contains(topic))
			{
				logger.debug("Adding {} to active filters for {}; recreate = {}", topic, getName(), recreateStore);
				shownTopics.add(topic);
				
				// TODO: optimise
				if (recreateStore)
				{
					logger.warn("Recreating store for {} because of {}", getName(), topic);
					initialiseFilteredStore();
				}
				
				return true;
			}
			
			return false;
		}
	}
	
	public Set<String> getFilters()
	{
		return Collections.unmodifiableSet(shownTopics);
	}
	
	private boolean removeFilter(final String topic)
	{
		synchronized (shownTopics)
		{
			if (shownTopics.contains(topic))
			{
				logger.debug("Removing {} from active filters for {}", topic, getName());
				shownTopics.remove(topic);
		
				initialiseFilteredStore();
				
				return true;
			}
			
			return false;
		}
	}
	
	private void removeAllFilters()
	{
		synchronized (shownTopics)
		{
			shownTopics.clear();
			filteredStore.clear();
		}
	}
	
	@Override
	public boolean filtersEnabled()
	{
		return shownTopics.size() != allTopics.size();
	}

	
	@Override
	public void clear()
	{
		super.clear();
		allTopics.clear();
		removeAllFilters();
	}
	
	public boolean updateFilter(final String topic, final boolean show)
	{
		boolean updated = false;
		if (show)
		{
			updated = applyFilter(topic, true);
		}
		else
		{
			updated = removeFilter(topic);
		}
		
		return updated;
	}

	public void setAllShowValues(boolean show)
	{
		for (final String topic : allTopics)
		{
			updateFilter(topic, show);
		}
		
		store.getTopicSummary().setAllShowValues(show);
	}

	public void toggleAllShowValues()
	{
		for (final String topic : allTopics)
		{
			updateFilter(topic, !shownTopics.contains(topic));
		}
		
		store.getTopicSummary().toggleAllShowValues();
	}

	public void setShowValue(final String topic, final boolean show)
	{
		updateFilter(topic, show);
		store.getTopicSummary().setShowValue(topic, show);
	}
}
