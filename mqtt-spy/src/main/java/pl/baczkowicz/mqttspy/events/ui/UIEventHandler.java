package pl.baczkowicz.mqttspy.events.ui;

import java.util.Queue;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UIEventHandler implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(UIEventHandler.class);
	
	private final Queue<MqttSpyUIEvent> uiEventQueue;

	public UIEventHandler(final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		this.uiEventQueue = uiEventQueue;
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
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				logger.error(UIEventHandler.class + " thread interrupted", e);
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
				final BrowseReceivedMessageEvent browseEvent = (BrowseReceivedMessageEvent) event;
				
				// Notify any observers there is a new message
				browseEvent.getStore().notify(browseEvent.getMessage());
			}
			else if (event instanceof UpdateReceivedMessageSummaryEvent)
			{
				final UpdateReceivedMessageSummaryEvent updateEvent = (UpdateReceivedMessageSummaryEvent) event;
				
				// Calculate the overall message count per topic
				updateEvent.getStore().getMessageStore().getTopicSummary().add(updateEvent.getAdded());
				
				// Update the 'show' property if required
				if (updateEvent.isShowTopic())
				{			
					updateEvent.getStore().getMessageStore().getTopicSummary().setShowValue(updateEvent.getAdded().getTopic(), true);											
				}
			}
			else if (event instanceof RemoveMessageEvent)
			{
				final RemoveMessageEvent removeEvent = (RemoveMessageEvent) event;
				
				// Remove old message from stats
				if (removeEvent.getRemoved() != null)
				{
					removeEvent.getStore().getTopicSummary().remove(removeEvent.getRemoved());
				}
			}
		}		
	}
}
