package pl.baczkowicz.mqttspy.connectivity.events;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;

public class MqttConnectionFailureEvent implements MqttEvent
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
