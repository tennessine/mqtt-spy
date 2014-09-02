package pl.baczkowicz.mqttspy.connectivity.events;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttDisconnectionAttemptFailureEvent extends MqttConnectionFailureEvent
{
	public MqttDisconnectionAttemptFailureEvent(final MqttConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
