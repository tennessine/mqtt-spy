package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public class MqttConnectionLostEvent extends MqttConnectionFailureEvent
{
	public MqttConnectionLostEvent(final MqttAsyncConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
