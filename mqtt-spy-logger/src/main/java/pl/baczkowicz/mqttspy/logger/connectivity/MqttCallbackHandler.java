package pl.baczkowicz.mqttspy.logger.connectivity;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.logger.connectivity.MqttMessageHandler;

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
	private final Queue<ReceivedMqttMessage> messageQueue = new LinkedBlockingQueue<ReceivedMqttMessage>();
	
	private MqttMessageHandler messageHandler;
	
	private long currentId = 1;

	public MqttCallbackHandler()
	{
		this.messageHandler = new MqttMessageHandler(messageQueue);
		new Thread(messageHandler).start();
	}

	public void connectionLost(Throwable cause)
	{
		logger.error("Connection lost", cause);
	}

	public void messageArrived(final String topic, final MqttMessage message)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("[{}] Received message on topic \"{}\". Payload = \"{}\"", messageQueue.size(), topic, new String(message.getPayload()));
		}
		messageQueue.add(new ReceivedMqttMessage(currentId, topic, message));
		currentId++;
	}

	public void deliveryComplete(IMqttDeliveryToken token)
	{
		if (logger.isTraceEnabled())
		{
			logger.trace("Delivery complete for " + token.getMessageId());
		}
	}
}
