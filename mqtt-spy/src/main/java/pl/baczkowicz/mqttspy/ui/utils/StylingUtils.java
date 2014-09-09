package pl.baczkowicz.mqttspy.ui.utils;

import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;

public class StylingUtils
{
	public static String getStyleForMqttConnectionStatus(final MqttConnectionStatus update)
	{
		String style = "";
		
		switch ((MqttConnectionStatus) update)
		{
			case NOT_CONNECTED:
				style = "tab-title-not-connected";
				break;
			case CONNECTED:					
				style = "tab-title-connected";
				break;
			case CONNECTING:
				style = "tab-title-connecting";
				break;
			case DISCONNECTED:
				style = "tab-title-disconnected";
				break;
			case DISCONNECTING:					
				style = "tab-title-disconnected";
				break;
			default:
				style = "tab-title-default";
				break;
		}
		
		return style;
	}
}
