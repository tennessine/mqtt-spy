package pl.baczkowicz.mqttspy.connectivity.handlers;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.events.MqttConnectionLostEvent;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

/**
 * One MQTT callback handler per connection.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class MqttCallbackHandler implements MqttCallback
{
	private final static Logger logger = LoggerFactory.getLogger(MqttCallbackHandler.class);
	
	/** Stores all received messages, so that we don't block the receiving thread. */
	private final Queue<MqttContent> queue = new LinkedBlockingQueue<MqttContent>();
	
	private MqttConnection connection;
	
	private long currentId = 1;

	private MqttMessageHandler messageHandler;

	public MqttCallbackHandler(final MqttConnection connection)
	{
		this.setConnection(connection);
		this.messageHandler = new MqttMessageHandler(connection, queue);
		new Thread(messageHandler).start();
	}

	public void connectionLost(Throwable cause)
	{
		logger.error("Connection " + connection.getProperties().getName() + " lost", cause);
		Platform.runLater(new MqttEventHandler(new MqttConnectionLostEvent(connection, cause)));
	}

	public void messageArrived(String topic, MqttMessage message)
	{
		logger.debug("[{}] Received message on topic \"{}\". Payload = \"{}\"", queue.size(), topic, new String(message.getPayload()));
		queue.add(new MqttContent(currentId, topic, message));
		currentId++;
	}

	public void deliveryComplete(IMqttDeliveryToken token)
	{
		logger.trace("Delivery complete for " + token.getMessageId());
	}

	public MqttConnection getConnection()
	{
		return connection;
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;
	}
}
