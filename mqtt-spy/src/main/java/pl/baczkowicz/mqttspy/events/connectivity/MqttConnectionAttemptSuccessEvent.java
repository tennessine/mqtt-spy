package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;

public class MqttConnectionAttemptSuccessEvent extends MqttConnectionSuccessEvent
{
	public MqttConnectionAttemptSuccessEvent(final MqttAsyncConnection connection)
	{
		super(connection);
	}
}
