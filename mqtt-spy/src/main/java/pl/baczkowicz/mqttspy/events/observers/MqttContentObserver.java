package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;

public interface MqttContentObserver
{
	void onMqttContentReceived(final MqttContent message);
}
