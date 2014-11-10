package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public class MqttDisconnectionAttemptSuccessEvent extends MqttConnectionSuccessEvent
{
	public MqttDisconnectionAttemptSuccessEvent(final MqttAsyncConnection connection)
	{
		super(connection);
	}
}
