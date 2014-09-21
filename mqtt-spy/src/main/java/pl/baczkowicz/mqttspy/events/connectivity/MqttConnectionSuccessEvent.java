package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.events.MqttSpyEvent;

public class MqttConnectionSuccessEvent implements MqttSpyEvent
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
