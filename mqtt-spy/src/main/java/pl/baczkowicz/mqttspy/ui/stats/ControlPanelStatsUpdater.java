package pl.baczkowicz.mqttspy.ui.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ControlPanelItemController;
import pl.baczkowicz.mqttspy.ui.controlpanel.ItemStatus;

public class ControlPanelStatsUpdater implements Runnable
{
	private boolean statsPlaying;
	
	private List<String> unicefDetails = new ArrayList<String>(Arrays.asList(
			"Finding mqtt-spy useful? Donate to UNICEF this month at", 
			"Like your mqtt-spy? Why not to donate to UNICEF this month at", 
			"If you use mqtt-spy on a regular basis, please donate to UNICEF every month at"));

	private int statTitleIndex;

	private final ControlPanelItemController controlPanelItemController;

	private Application application;
	
	public ControlPanelStatsUpdater(final ControlPanelItemController controlPanelItemController, final Application application)
	{
		this.controlPanelItemController = controlPanelItemController;				
		this.application = application;
	}
	
	public void show()
	{
		// UNICEF details
		final Random r = new Random();
		controlPanelItemController.setDetails("");
		// TODO: add "(click here)"

		controlPanelItemController.setStatus(ItemStatus.STATS);

		final List<Node> items  = new ArrayList<Node>();

		items.add(new Label(unicefDetails.get(r.nextInt(unicefDetails.size()))));
		// items.add(new Label(" at"));

//		final Hyperlink jg = new Hyperlink();
//		jg.setText("justgiving.com");
//		jg.setOnAction(new EventHandler<ActionEvent>()
//		{
//			@Override
//			public void handle(ActionEvent event)
//			{
//				application.getHostServices().showDocument("https://www.justgiving.com/mqtt-spy/");
//			}
//		});
//		donate.getChildren().add(jg);
		//
		// donate.getChildren().add(new Label(" or "));

		final Hyperlink unicef = new Hyperlink();
		unicef.setText("unicef.org.uk");
		unicef.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				application.getHostServices().showDocument("http://fundraise.unicef.org.uk/MyPage/mqtt-spy");
			}
		});
		items.add(unicef);
		
		items.add(new Label("!"));
		// controlPanelItemController.getCustomItems().getChildren().add(donate);
		controlPanelItemController.getDetails().getChildren().addAll(items);
		
		statsPlaying = true;
		ControlPanelItemController.setButtonProperties(controlPanelItemController.getButton1(), "/images/pause.png", true, new EventHandler<ActionEvent>()
		{			
			@Override
			public void handle(ActionEvent event)
			{
				if (statsPlaying)
				{
					ControlPanelItemController.setButtonProperties(controlPanelItemController.getButton1(), "/images/play.png", true);
					statsPlaying = false;
				}
				else
				{
					ControlPanelItemController.setButtonProperties(controlPanelItemController.getButton1(), "/images/pause.png", true);
					statsPlaying = true;
				}
			}
		});
		
		ControlPanelItemController.setButtonProperties(controlPanelItemController.getButton2(), "/images/next.png", true, new EventHandler<ActionEvent>()
		{			
			@Override
			public void handle(ActionEvent event)
			{
				moveToNextStatTitle();
			}
		});
		
		controlPanelItemController.refresh();
		
		new Thread(this).start();
	}
	
	private void moveToNextStatTitle()
	{								
		statTitleIndex++;
		if (statTitleIndex == 6)
		{
			statTitleIndex = 0;
		}
		refreshStatsTitle();
	}
	
	private void refreshStatsTitle()
	{
		final long milliseconds = new Date().getTime() - StatisticsManager.stats.getStartDate().toGregorianCalendar().getTime().getTime();
		final long days = milliseconds / (1000 * 60 * 60 * 24);
		final String inDays = days > 1 ? (" in " + days + " days") : "";
		
		if (statTitleIndex == 0)
		{
			if (StatisticsManager.stats.getConnections() > 1)
			{
				controlPanelItemController.setTitle(String.format(						
						"Your mqtt-spy made %d connections to MQTT brokers%s.", 
						StatisticsManager.stats.getConnections(),
						inDays));
			}
			else
			{
				moveToNextStatTitle();
			}
		}		
		else if (statTitleIndex == 1)
		{
			if (StatisticsManager.stats.getMessagesPublished() > 1)
			{
				controlPanelItemController.setTitle(String.format(
						"Your mqtt-spy published %d messages to MQTT brokers%s.", 
						StatisticsManager.stats.getMessagesPublished(),
						inDays));
			}
			else
			{
				moveToNextStatTitle();
			}
		}		
		else if (statTitleIndex == 2)
		{
			if (StatisticsManager.stats.getSubscriptions() > 1)
			{
				controlPanelItemController.setTitle(String.format(
						"Your mqtt-spy made %d subscriptions to MQTT brokers%s.", 
						StatisticsManager.stats.getSubscriptions(),
						inDays));
			}
			else
			{
				moveToNextStatTitle();
			}
		}
		else if (statTitleIndex == 3)
		{
			if (StatisticsManager.stats.getMessagesReceived() > 1)
			{
				controlPanelItemController.setTitle(String.format(
						"Your mqtt-spy received %d messages%s.", 
						StatisticsManager.stats.getMessagesReceived(),
						inDays));
			}
			else
			{
				moveToNextStatTitle();
			}
		}
		else if (statTitleIndex == 4)
		{
			if (StatisticsManager.getMessagesPublished() > 1)
			{
				controlPanelItemController.setTitle(String.format(
						"Right now your mqtt-spy is publishing %d msgs/s.", 
						StatisticsManager.getMessagesPublished()));
			}
			else
			{
				moveToNextStatTitle();
			}
		}
		else if (statTitleIndex == 5)
		{
			if (StatisticsManager.getMessagesReceived() > 1)
			{
				controlPanelItemController.setTitle(String.format(
						"Right now your mqtt-spy is munching through %d msgs/s.", 
						StatisticsManager.getMessagesReceived()));
			}
			else
			{
				moveToNextStatTitle();
			}
		}		
	}

	@Override
	public void run()
	{
		int count = 0;
		while (true)
		{
			count++;
			Platform.runLater(new Runnable()
			{				
				@Override
				public void run()
				{
					refreshStatsTitle();	
				}
			});
						
			if (statsPlaying)
			{
				if (count == 10)
				{
					count = 0;
					Platform.runLater(new Runnable()
					{						
						@Override
						public void run()
						{
							moveToNextStatTitle();							
						}
					});
				}				
			}
			else
			{
				count = 0;
			}			
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
	}
}
