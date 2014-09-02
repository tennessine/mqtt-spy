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

public class MqttCallbackHandler implements MqttCallback
{
	private final static Logger logger = LoggerFactory.getLogger(MqttCallbackHandler.class);
	
	private final Queue<MqttContent> queue = new LinkedBlockingQueue<MqttContent>();
	
	private MqttConnection connection;
	
	private long currentId = 1;

	public MqttCallbackHandler(final MqttConnection connection)
	{
		this.setConnection(connection);
	}

	public void connectionLost(Throwable cause)
	{
		logger.error("Connection " + connection.getProperties().getName() + " lost", cause);
		Platform.runLater(new MqttEventHandler(new MqttConnectionLostEvent(connection, cause)));
	}

	public void messageArrived(String topic, MqttMessage message)
	{
		logger.info("Received message on topic \"" + topic + "\". Payload = \"" + new String(message.getPayload()) + "\"");
		queue.add(new MqttContent(currentId, topic, message));
		currentId++;
		Platform.runLater(new MqttMessageHandler(connection, queue));
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
