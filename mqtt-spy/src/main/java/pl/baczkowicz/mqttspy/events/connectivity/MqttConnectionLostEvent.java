package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionLostEvent extends MqttConnectionFailureEvent
{
	public MqttConnectionLostEvent(final MqttConnection connection, final Throwable cause)
	{
		super(connection, cause);
	}
}
