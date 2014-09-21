package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttDisconnectionAttemptSuccessEvent extends MqttConnectionSuccessEvent
{
	public MqttDisconnectionAttemptSuccessEvent(final MqttConnection connection)
	{
		super(connection);
	}
}
