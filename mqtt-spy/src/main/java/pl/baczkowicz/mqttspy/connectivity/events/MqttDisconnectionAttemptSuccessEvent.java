package pl.baczkowicz.mqttspy.connectivity.events;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttDisconnectionAttemptSuccessEvent extends MqttConnectionSuccessEvent
{
	public MqttDisconnectionAttemptSuccessEvent(final MqttConnection connection)
	{
		super(connection);
	}
}
