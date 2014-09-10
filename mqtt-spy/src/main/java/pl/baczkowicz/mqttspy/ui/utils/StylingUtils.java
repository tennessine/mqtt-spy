package pl.baczkowicz.mqttspy.ui.utils;

import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;

public class StylingUtils
{
	public static String getStyleForMqttConnectionStatus(final MqttConnectionStatus update)
	{
		String style = "connection-default";
		
		if (update != null)
		{
			switch ((MqttConnectionStatus) update)
			{
				case NOT_CONNECTED:
					style = "connection-not-connected";
					break;
				case CONNECTED:					
					style = "connection-connected";
					break;
				case CONNECTING:
					style = "connection-connecting";
					break;
				case DISCONNECTED:
					style = "connection-disconnected";
					break;
				case DISCONNECTING:					
					style = "connection-disconnected";
					break;
				default:
					style = "connection-default";
					break;
			}
		}
		
		return style;
	}
}
