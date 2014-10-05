package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;

public interface MessageAddedObserver
{
	void onMessageAdded(final MqttContent message);
}
