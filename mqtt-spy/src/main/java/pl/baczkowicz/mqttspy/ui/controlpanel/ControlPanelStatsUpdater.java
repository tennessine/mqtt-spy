package pl.baczkowicz.mqttspy.ui.controlpanel;

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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import pl.baczkowicz.mqttspy.stats.StatisticsManager;
import pl.baczkowicz.mqttspy.ui.ControlPanelItemController;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;
import pl.baczkowicz.mqttspy.utils.TimeUtils;

public class ControlPanelStatsUpdater implements Runnable
{
	private final static long milliseconds = new Date().getTime() - StatisticsManager.stats.getStartDate().toGregorianCalendar().getTime().getTime();
	
	private final static long days = milliseconds / (1000 * 60 * 60 * 24);
	
	private final static String inDays = days > 1 ? (" in " + days + " days") : "";
	
	private final static String since = " since " + TimeUtils.DATE_SDF.format(StatisticsManager.stats.getStartDate().toGregorianCalendar().getTime());
	
	private final static int STATS_MESSAGES = 6;
	
	private final static int GO_NEXT_AFTER_INTERVALS = 10;
	
	private final static int REFRESH_INTERVAL =  1000;
		
	private boolean statsPlaying;
	
	private List<String> unicefDetails = new ArrayList<String>(Arrays.asList(
			"Finding mqtt-spy useful? Donate to UNICEF each month at the", 
			"Like your mqtt-spy? Why not to donate to UNICEF this month at the", 
			"Using mqtt-spy on a regular basis? Please donate to UNICEF at the"));

	private int statTitleIndex;

	private final ControlPanelItemController controlPanelItemController;

	private Application application;

	private int secondCounter;
	
	private final Button bigButton;
	
	public ControlPanelStatsUpdater(final ControlPanelItemController controlPanelItemController, final Button bigButton, final Application application)
	{
		this.controlPanelItemController = controlPanelItemController;
		this.bigButton = bigButton;
		this.application = application;
	}
	
	private static String formatNumber(final long number)
	{
		long divided = number;
		final StringBuffer sb = new StringBuffer();
		
		while (divided > 1000)
		{
			long rest = divided % 1000;
			sb.insert(0, " " + String.format("%03d", rest));
			
			divided = divided / 1000;
		}
		
		long rest = divided % 1000;
		sb.insert(0, rest);
		
		return sb.toString();
	}
	
	public void show()
	{
		// Default values
		controlPanelItemController.setTitle("Connect to an MQTT broker to start seeing processing statistics...");		
		controlPanelItemController.setDetails("");
		controlPanelItemController.setStatus(ItemStatus.STATS);
		
		bigButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				moveToNextStatTitle();
			}
		});

		final List<Node> items  = new ArrayList<Node>();

		// UNICEF details
		final Random r = new Random();
		items.add(new Label(unicefDetails.get(r.nextInt(unicefDetails.size()))));

		final Hyperlink unicef = new Hyperlink();
		unicef.setText("unicef.org.uk mqtt-spy page");
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
				event.consume();
			}
		});
		
		controlPanelItemController.refresh();
		
		new Thread(this).start();
	}
	
	private void moveToNextStatTitle()
	{	
		secondCounter = 0;
		
		statTitleIndex++;
		
		if (statTitleIndex == STATS_MESSAGES)
		{
			statTitleIndex = 0;
		}
		
		// Try to update the stats - if all unavailable, ignore
		int retries = 0;		
		while (!refreshStatsTitle(false) && retries < STATS_MESSAGES)
		{				
			statTitleIndex++;
			
			if (statTitleIndex == STATS_MESSAGES)
			{
				statTitleIndex = 0;
			}
			
			retries++;
		}
	}
	
	private boolean refreshStatsTitle(final boolean updateOnly)
	{
		if ((statTitleIndex == 0) && (StatisticsManager.stats.getConnections() > 0))
		{
			controlPanelItemController.setTitle(String.format(
					"Your mqtt-spy made %s connection" + (StatisticsManager.stats.getConnections() > 1 ? "s" : "") + " to MQTT brokers%s.",
					formatNumber(StatisticsManager.stats.getConnections()), inDays));
			return true;
		}

		else if ((statTitleIndex == 1) && (StatisticsManager.stats.getMessagesPublished() > 1))
		{
			controlPanelItemController.setTitle(String.format(
					"Your mqtt-spy published %s messages to MQTT brokers.",
					formatNumber(StatisticsManager.stats.getMessagesPublished()), inDays));
			return true;
		}

		else if ((statTitleIndex == 2) && (StatisticsManager.stats.getSubscriptions() > 1))
		{
			controlPanelItemController.setTitle(String.format(
					"Your mqtt-spy made %s subscriptions to MQTT brokers%s.",
					formatNumber(StatisticsManager.stats.getSubscriptions()), inDays));
			return true;
		}

		else if ((statTitleIndex == 3) && (StatisticsManager.stats.getMessagesReceived() > 1))
		{
			controlPanelItemController.setTitle(String.format(
					"Your mqtt-spy received %s messages%s.",
					formatNumber(StatisticsManager.stats.getMessagesReceived()), since));
			return true;
		}

		else if ((statTitleIndex == 4) && (updateOnly || StatisticsManager.getMessagesPublished() > 1))
		{
			controlPanelItemController.setTitle(String.format(
					"Right now your mqtt-spy is publishing %s msgs/s.",
					StatisticsManager.getMessagesPublished()));
			return true;
		}

		else if ((statTitleIndex == 5) && (updateOnly || StatisticsManager.getMessagesReceived() > 1))
		{
			controlPanelItemController.setTitle(String.format(
					"Right now your mqtt-spy is munching through %d msgs/s.",
					StatisticsManager.getMessagesReceived()));
			return true;
		}				
		
		return false;
	}

	@Override
	public void run()
	{
		secondCounter = 0;
		while (true)
		{
			secondCounter++;
			Platform.runLater(new Runnable()
			{				
				@Override
				public void run()
				{
					refreshStatsTitle(true);	
				}
			});
						
			if (statsPlaying)
			{
				if (secondCounter == GO_NEXT_AFTER_INTERVALS)
				{
					secondCounter = 0;
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
				secondCounter = 0;
			}			
			
			if (ThreadingUtils.sleep(REFRESH_INTERVAL))
			{
				break;
			}			
		}
	}
}
