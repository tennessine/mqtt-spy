package pl.baczkowicz.mqttspy.connectivity.handlers;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.application.Platform;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttAsyncConnection;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.connectivity.MqttConnectionLostEvent;

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
	private final Queue<MqttContent> messageQueue = new LinkedBlockingQueue<MqttContent>();
	
	private MqttAsyncConnection connection;
	
	private long currentId = 1;

	private MqttMessageHandler messageHandler;

	public MqttCallbackHandler(final MqttAsyncConnection connection)
	{
		this.setConnection(connection);
		this.messageHandler = new MqttMessageHandler(connection, messageQueue);
		new Thread(messageHandler).start();
	}

	public void connectionLost(Throwable cause)
	{
		logger.error("Connection " + connection.getProperties().getName() + " lost", cause);
		Platform.runLater(new MqttEventHandler(new MqttConnectionLostEvent(connection, cause)));
	}

	public void messageArrived(final String topic, final MqttMessage message)
	{
		logger.debug("[{}] Received message on topic \"{}\". Payload = \"{}\"", messageQueue.size(), topic, new String(message.getPayload()));
		messageQueue.add(new MqttContent(currentId, topic, message));
		currentId++;
	}

	public void deliveryComplete(IMqttDeliveryToken token)
	{
		logger.trace("Delivery complete for " + token.getMessageId());
	}

	public MqttAsyncConnection getConnection()
	{
		return connection;
	}

	public void setConnection(MqttAsyncConnection connection)
	{
		this.connection = connection;
	}
}
