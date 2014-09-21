package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionAttemptFailureEvent extends MqttConnectionFailureEvent
{
	public MqttConnectionAttemptFailureEvent(final MqttConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
