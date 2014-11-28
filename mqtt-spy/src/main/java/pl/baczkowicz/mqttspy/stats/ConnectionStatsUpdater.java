package pl.baczkowicz.mqttspy.stats;

import pl.baczkowicz.mqttspy.ui.ConnectionController;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;
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
			if (ThreadingUtils.sleep(REFRESH_INTERVAL))			
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
