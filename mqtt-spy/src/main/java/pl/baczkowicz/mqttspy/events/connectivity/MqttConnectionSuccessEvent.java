package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.events.MqttSpyEvent;

public class MqttConnectionSuccessEvent implements MqttSpyEvent
{
	private final MqttAsyncConnection connection;
	
	public MqttConnectionSuccessEvent(final MqttAsyncConnection connection)
	{
		this.connection = connection;
	}

	public MqttAsyncConnection getConnection()
	{
		return connection;
	}
}
