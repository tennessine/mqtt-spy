package pl.baczkowicz.mqttspy.ui.utils;

import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;

public class ConnectionUtils
{
	public static void disconnectAndClose(final MqttManager mqttManager, final int connectionId, final Tab tab)
	{
		disconnect(mqttManager, connectionId);
		mqttManager.close(connectionId);
		TabUtils.requestClose(tab);
	}
	
	public static void disconnect(final MqttManager mqttManager, final int connectionId)
	{
		mqttManager.disconnectFromBroker(connectionId);
	}
	
	public static EventHandler<ActionEvent> createDisconnectAction(final MqttManager mqttManager, final int connectionId)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				ConnectionUtils.disconnect(mqttManager, connectionId);
				event.consume();
			}
		};
	}
	
	public static EventHandler<ActionEvent> createDisconnectAndCloseAction(final MqttManager mqttManager, final int connectionId, final Tab tab)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				ConnectionUtils.disconnectAndClose(mqttManager, connectionId, tab);
				event.consume();
			}
		};
	}
	
	public static EventHandler<ActionEvent> createConnectAction(final MqttManager mqttManager, final int connectionId)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				mqttManager.connectToBroker(connectionId);
				event.consume();
			}
		};
	}
}
