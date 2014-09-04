package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import javafx.application.Platform;
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

	private String name;	
	
	public ObservableMessageStoreWithFiltering(final String name, final int maxSize)
	{
		super(maxSize);
		this.name = name;
		this.messagesPerTopic = new MessagesPerTopic(name);
		this.filteredStore = new MqttMessageStore(maxSize);
	}
	
	public void messageReceived(final MqttContent message)
	{						
		// Note: this is not FX thread yet
		final boolean noFilters = !filtersEnabled();
		final boolean topicAlreadyExists = messagesPerTopic.topicExists(message.getTopic());			
		final boolean remove = store.isMaxSize();
		
		final ObservableMessageStore thisStore = this;
		thisStore.storeMessage(message);
		
		// Note: following require the FX thread
		// TODO: still needs to be optimised
		Platform.runLater(new Runnable()
		{
			@Override
			public void run()
			{				
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
								
				thisStore.notify(message);				
			}		
		});
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
		synchronized (filters)
		{
			if (!filters.contains(topic))
			{
				logger.info("Adding {} to active filters for {}; recreate = {}", topic, name, recreateStore);
				filters.add(topic);
				
				if (recreateStore)
				{
					initialiseFilteredStore();
				}
			}
		}
	}
	
	public Set<String> getFilters()
	{
		return Collections.unmodifiableSet(filters);
	}
	
	public void removeFilter(final String topic)
	{
		synchronized (filters)
		{
			logger.info("Removing {} from active filters for {}", topic, name);
			filters.remove(topic);
		
			initialiseFilteredStore();
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
