package pl.baczkowicz.mqttspy.ui.stats;

import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import javafx.application.Platform;

public class ConnectionStatsUpdater implements Runnable
{
	private static final int REFRESH_INTERVAL = 1000;
	
	private ConnectionManager connectionManager;

	public ConnectionStatsUpdater(final ConnectionManager connectionManager)
	{
		this.connectionManager = connectionManager;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(REFRESH_INTERVAL);
			}
			catch (InterruptedException e)
			{
				break;
			}
		
			Platform.runLater(new Runnable()
			{					
				@Override
				public void run()
				{
					updateConnectionStats();					
				}
			});						
		}
	}

	private void updateConnectionStats()
	{		
		for (final ConnectionController connectionController : connectionManager.getConnectionControllers().values())
		{
			connectionController.updateConnectionStats();
		}	
	}
}
