package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionAttemptSuccessEvent extends MqttConnectionSuccessEvent
{
	public MqttConnectionAttemptSuccessEvent(final MqttConnection connection)
	{
		super(connection);
	}
}
