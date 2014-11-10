package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public interface ConnectionStatusChangeObserver
{
	void onConnectionStatusChanged(final MqttAsyncConnection changedConnection);
}
