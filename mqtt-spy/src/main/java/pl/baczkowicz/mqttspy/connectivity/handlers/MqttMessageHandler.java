package pl.baczkowicz.mqttspy.connectivity.handlers;

import java.util.Queue;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class MqttMessageHandler implements Runnable
{
	private final Queue<MqttContent> queue;
	private MqttConnection connection;

	public MqttMessageHandler(final MqttConnection connection, final Queue<MqttContent> queue)
	{
		this.queue = queue;
		this.connection = connection;
	}
	
	public void run()
	{
		connection.messageReceived(queue.remove());
	}
}
