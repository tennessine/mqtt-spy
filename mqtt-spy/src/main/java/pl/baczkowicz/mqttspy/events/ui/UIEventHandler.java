package pl.baczkowicz.mqttspy.events.ui;

import java.util.Queue;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

public class UIEventHandler implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(UIEventHandler.class);
	
	private final Queue<MqttSpyUIEvent> uiEventQueue;
	
	private final EventManager eventManager;

	public UIEventHandler(final Queue<MqttSpyUIEvent> uiEventQueue, final EventManager eventManager)
	{
		this.uiEventQueue = uiEventQueue;
		this.eventManager = eventManager;
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (uiEventQueue.size() > 0)
			{
				Platform.runLater(new Runnable()
				{				
					@Override
					public void run()
					{					
						showUpdates();					
					}
				});	
			}
			
			// Sleep so that we don't run all the time - updating the UI 20 times a second should be more than enough
			if (ThreadingUtils.sleep(50))			
			{
				break;
			}
		}		
	}

	private void showUpdates()
	{
		while (uiEventQueue.size() > 0)
		{
			final MqttSpyUIEvent event = uiEventQueue.remove();
			
			if (event instanceof BrowseReceivedMessageEvent)
			{
				eventManager.notifyMessageAdded((BrowseReceivedMessageEvent) event);				
			}
			else if (event instanceof BrowseRemovedMessageEvent)
			{
				eventManager.notifyMessageRemoved((BrowseRemovedMessageEvent) event);
			}			
			else if (event instanceof TopicSummaryNewMessageEvent)
			{
				final TopicSummaryNewMessageEvent updateEvent = (TopicSummaryNewMessageEvent) event;
				
				// Calculate the overall message count per topic
				updateEvent.getList().getTopicSummary().addMessage(updateEvent.getAdded());
				
				// Update the 'show' property if required
				if (updateEvent.isShowTopic())
				{			
					updateEvent.getList().getTopicSummary().setShowValue(updateEvent.getAdded().getTopic(), true);											
				}
			}
			else if (event instanceof TopicSummaryRemovedMessageEvent)
			{
				final TopicSummaryRemovedMessageEvent removeEvent = (TopicSummaryRemovedMessageEvent) event;
				
				// Remove old message from stats
				if (removeEvent.getRemoved() != null)
				{
					removeEvent.getList().getTopicSummary().removeMessage(removeEvent.getRemoved());
				}
			}
		}		
	}
}
