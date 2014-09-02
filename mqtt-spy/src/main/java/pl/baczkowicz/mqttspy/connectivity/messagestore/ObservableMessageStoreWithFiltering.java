package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class ObservableMessageStoreWithFiltering extends ObservableMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(ObservableMessageStoreWithFiltering.class);
	
	private final Set<String> filters = new HashSet<String>();
	
	private final MqttMessageStore filteredStore;
	
	private final MessagesPerTopic messagesPerTopic;	
	
	public ObservableMessageStoreWithFiltering(final String name, final int maxSize)
	{
		super(maxSize);
		this.messagesPerTopic = new MessagesPerTopic(name);
		this.filteredStore = new MqttMessageStore(maxSize);
	}
	
	public void messageReceived(final MqttContent message)
	{						
		if (message != null)
		{
			final boolean noFilters = !filtersEnabled();
			final boolean topicAlreadyExists = messagesPerTopic.topicExists(message.getTopic());			
			final boolean remove = store.isMaxSize();
			
			super.storeMessage(message);
			messagesPerTopic.addAndRemove(message, remove, messageFormat);
			
			if (noFilters)
			{			
				if (!topicAlreadyExists)
				{
					setShowValue(message.getTopic(), true);
					applyFilter(message.getTopic(), false);
				}							
			}
			
			if (message != null && filters.contains(message.getTopic()))
			{
				filteredStore.add(message);				
			}
		}		
		
		super.notify(message);
	}	
	
	@Override
	public Queue<MqttContent> getMessages()
	{		
		return filteredStore.getMessages();
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
	
	public void applyFilter(final String topic)
	{
		applyFilter(topic, true);
	}
	
	private void applyFilter(final String topic, final boolean recreateStore)
	{
		logger.info("Adding " + topic + " to active filters; recreate = " + recreateStore);
		filters.add(topic);
		
		if (recreateStore)
		{
			initialiseFilteredStore();
		}
	}
	
	public Set<String> getFilters()
	{
		return Collections.unmodifiableSet(filters);
	}
	
	public void removeFilter(final String topic)
	{
		logger.info("Removing " + topic + " from active filters");
		filters.remove(topic);
		
		initialiseFilteredStore();
	}
	
	public void removeAllFilters()
	{
		filters.clear();
		filteredStore.clear();
	}
	
	@Override
	public boolean filtersEnabled()
	{
		return filters.size() != getObservableMessagesPerTopic().size();
	}
	
	public ObservableList<SubscriptionTopicSummary> getObservableMessagesPerTopic()
	{
		return messagesPerTopic.getObservableMessagesPerTopic();
	}
	
	public void toggleAllShowValues()
	{
		for (final SubscriptionTopicSummary item : messagesPerTopic.getObservableMessagesPerTopic())
		{
			item.showProperty().set(!item.showProperty().get());
		}
	}
	
	public void setShowValue(final String topic, final boolean value)
	{
		for (final SubscriptionTopicSummary item : messagesPerTopic.getObservableMessagesPerTopic())
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
		for (final SubscriptionTopicSummary item : messagesPerTopic.getObservableMessagesPerTopic())
		{
			item.showProperty().set(value);
		}
	}
	
	@Override
	public void clear()
	{
		super.clear();
		messagesPerTopic.clear();
		removeAllFilters();
	}
}
