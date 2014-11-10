package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public class MqttConnectionAttemptFailureEvent extends MqttConnectionFailureEvent
{
	public MqttConnectionAttemptFailureEvent(final MqttAsyncConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
