package pl.baczkowicz.mqttspy.storage;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.ui.BrowseRemovedMessageEvent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.events.ui.TopicSummaryRemovedMessageEvent;

public class MessageStoreGarbageCollector implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MessageStoreGarbageCollector.class);
	
	/** Stores events for the UI to be updated. */
	protected final Queue<MqttSpyUIEvent> uiEventQueue;
	
	private MessageListWithObservableTopicSummary messages;
	
	private int minMessagesPerTopic;

	private boolean createTopicSummaryEvents;

	private boolean createBrowseEvents;
	
	public MessageStoreGarbageCollector(final ManagedMessageStoreWithFiltering store, final MessageListWithObservableTopicSummary messages, 
			final Queue<MqttSpyUIEvent> uiEventQueue, 
			final int minMessages, final boolean createTopicSummaryEvents, final boolean createBrowseEvents)
	{
		this.messages = messages;
		this.uiEventQueue = uiEventQueue;
		this.minMessagesPerTopic = minMessages;
		this.createTopicSummaryEvents = createTopicSummaryEvents;
		this.createBrowseEvents = createBrowseEvents;
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName("Garbage Message Collector for " + messages.getName());
		logger.info("Starting thread " + Thread.currentThread().getName());
				
		while (true)		
		{			
			try
			{				
				Thread.sleep(1000);				
			}
			catch (InterruptedException e)
			{
				break;
			}
			
			synchronized (messages.getMessages())
			{
				//logger.info("Checkign if can delete messages... {} {}", i.hasNext(), store.shouldRemove());
				boolean shouldRemove =  messages.exceedingPreferredSize();
					
				if (!shouldRemove)
				{
					continue;
				}
				
				logger.info("[{}] Checking if can delete messages...", messages.getName());
				for (int i = messages.getMessages().size() - 1; i >=0; i--)				
				{
					final MqttContent element = messages.getMessages().get(i);
										
					final int count = messages.getTopicSummary().getCountForTopic(element.getTopic());
					if (count > minMessagesPerTopic)
					{
						logger.info("[{} {} {}/{}/{}] Deleting message on " + element.getTopic() + ", content " + element.getFormattedPayload(), 
								messages.getName(), shouldRemove, count, messages.getMessages().size(), messages.getPreferredSize());
						
						// Remove from the store
						messages.remove(i);
						shouldRemove = messages.exceedingPreferredSize();
						
						logger.info("[{} {} {}/{}/{}] Deleted message on " + element.getTopic() + ", content " + element.getFormattedPayload(), 
								messages.getName(), shouldRemove, count, messages.getMessages().size(), messages.getPreferredSize());
												
						// Update topic summary and UI

						// Remove events are for the normal store
						if (createTopicSummaryEvents)
						{
							uiEventQueue.add(new TopicSummaryRemovedMessageEvent(messages, element));
						}
						
						// Index update are for the filtered store
						if (createBrowseEvents)
						{
							uiEventQueue.add(new BrowseRemovedMessageEvent(messages, element, i + 1));
						}
						
						if (!shouldRemove)
						{
							break;
						}
					}				
					else
					{
						logger.info("[{}] Message count for topic {} = {}", messages.getName(), element.getTopic(), count);
					}
				}
			}
		}
	}
}
