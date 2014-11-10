package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public class MqttDisconnectionAttemptFailureEvent extends MqttConnectionFailureEvent
{
	public MqttDisconnectionAttemptFailureEvent(final MqttAsyncConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
