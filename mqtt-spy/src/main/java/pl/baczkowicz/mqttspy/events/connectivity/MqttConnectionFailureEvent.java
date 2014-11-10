package pl.baczkowicz.mqttspy.events.connectivity;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.events.MqttSpyEvent;

public class MqttConnectionFailureEvent implements MqttSpyEvent
{
	private final MqttAsyncConnection connection;
	private final Throwable cause;

	public MqttConnectionFailureEvent(final MqttAsyncConnection connection, final Throwable cause)
	{
		this.connection = connection;
		this.cause = cause;
	}

	public MqttAsyncConnection getConnection()
	{
		return connection;
	}

	public Throwable getCause()
	{
		return cause;
	}
}
