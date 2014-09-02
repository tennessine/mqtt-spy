package pl.baczkowicz.mqttspy.events.observers;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public interface ConnectionStatusChangeObserver
{
	void onConnectionStatusChanged(final MqttConnection changedConnection);
}
