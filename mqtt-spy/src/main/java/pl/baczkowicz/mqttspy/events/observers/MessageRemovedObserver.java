package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;

public interface MessageRemovedObserver
{
	void onMessageRemoved(final MqttContent message, final int messageIndex);
}
