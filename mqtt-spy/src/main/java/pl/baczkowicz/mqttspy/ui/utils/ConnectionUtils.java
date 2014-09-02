package pl.baczkowicz.mqttspy.ui.utils;

import pl.baczkowicz.mqttspy.connectivity.MqttManager;
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
}
