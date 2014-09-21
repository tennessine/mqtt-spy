package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.events.MqttSpyEvent;

public class MqttConnectionFailureEvent implements MqttSpyEvent
{
	private final MqttConnection connection;
	private final Throwable cause;

	public MqttConnectionFailureEvent(final MqttConnection connection, final Throwable cause)
	{
		this.connection = connection;
		this.cause = cause;
	}

	public MqttConnection getConnection()
	{
		return connection;
	}

	public Throwable getCause()
	{
		return cause;
	}
}
