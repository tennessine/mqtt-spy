package pl.baczkowicz.mqttspy.storage;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.events.ui.RemoveMessageEvent;

public class MessageStoreGarbageCollector implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MessageStoreGarbageCollector.class);
	
	/** Stores events for the UI to be updated. */
	protected final Queue<MqttSpyUIEvent> uiEventQueue;
	
	private final MessageListWithObservableTopicSummary store;
	
	private int minMessagesPerTopic;

	private boolean createRemoveEvents;

	private boolean createIndexUpdateEvents;
	
	public MessageStoreGarbageCollector(final MessageListWithObservableTopicSummary store, final Queue<MqttSpyUIEvent> uiEventQueue, 
			final int minMessages, final boolean createRemoveEvents, final boolean createIndexUpdateEvents)
	{
		this.store = store;
		this.uiEventQueue = uiEventQueue;
		this.minMessagesPerTopic = minMessages;
		this.createRemoveEvents = createRemoveEvents;
		this.createIndexUpdateEvents = createIndexUpdateEvents;
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName("Garbage Message Collector for " + store.getName());
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
			
			synchronized (store.getMessages())
			{
				//logger.info("Checkign if can delete messages... {} {}", i.hasNext(), store.shouldRemove());
				boolean shouldRemove =  store.exceedingPreferredSize();
					
				if (!shouldRemove)
				{
					continue;
				}
				
				logger.info("[{}] Checking if can delete messages...", store.getName());
				for (int i = store.getMessages().size() - 1; i >=0; i--)				
				{
					final MqttContent element = store.getMessages().get(i);
										
					final int count = store.getTopicSummary().getCountForTopic(element.getTopic());
					if (count > minMessagesPerTopic)
					{
						logger.info("[{} {} {}/{}/{}] Deleting message on " + element.getTopic() + ", content " + element.getFormattedPayload(), 
								store.getName(), shouldRemove, count, store.getMessages().size(), store.getPreferredSize());
						
						// Remove from the store
						store.remove(i);
						shouldRemove = store.exceedingPreferredSize();
						
						logger.info("[{} {} {}/{}/{}] Deleted message on " + element.getTopic() + ", content " + element.getFormattedPayload(), 
								store.getName(), shouldRemove, count, store.getMessages().size(), store.getPreferredSize());
												
						// Update topic summary and UI

						// Remove events are for the normal store
						if (createRemoveEvents)
						{
							uiEventQueue.add(new RemoveMessageEvent(store, element));
						}
						
						// Index update are for the filtered store
						if (createIndexUpdateEvents)
						{
							// TODO
						}
						
						if (!shouldRemove)
						{
							break;
						}
					}				
					else
					{
						logger.info("[{}] Message count for topic {} = {}", store.getName(), element.getTopic(), count);
					}
				}
			}
		}
	}
}
