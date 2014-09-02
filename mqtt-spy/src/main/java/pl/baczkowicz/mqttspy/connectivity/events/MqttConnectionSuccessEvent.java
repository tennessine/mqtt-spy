package pl.baczkowicz.mqttspy.connectivity.events;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionSuccessEvent implements MqttEvent
{
	private final MqttConnection connection;
	
	public MqttConnectionSuccessEvent(final MqttConnection connection)
	{
		this.connection = connection;
	}

	public MqttConnection getConnection()
	{
		return connection;
	}
}
