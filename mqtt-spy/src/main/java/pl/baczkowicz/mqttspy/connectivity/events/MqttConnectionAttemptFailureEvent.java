package pl.baczkowicz.mqttspy.connectivity.events;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionAttemptFailureEvent extends MqttConnectionFailureEvent
{
	public MqttConnectionAttemptFailureEvent(final MqttConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
