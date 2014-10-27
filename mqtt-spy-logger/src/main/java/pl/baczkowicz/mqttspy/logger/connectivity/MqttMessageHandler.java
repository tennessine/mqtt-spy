package pl.baczkowicz.mqttspy.logger.connectivity;

import java.util.Queue;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.messages.ReceivedMqttMessage;

/**
 * This class is responsible for handling received messages. One thread per connection expected here.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class MqttMessageHandler implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MqttMessageHandler.class);
	
	private final Queue<ReceivedMqttMessage> queue;

	public MqttMessageHandler(final Queue<ReceivedMqttMessage> queue)
	{
		this.queue = queue;
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				if (queue.size() > 0)
				{
					final ReceivedMqttMessage message = queue.remove();

					final StringBuffer logMessage = new StringBuffer();
					logMessage.append("<MqttMessage>");
					logMessage.append("<ID>" + message.getId() + "</ID>");
					logMessage.append("<Timestamp>" + message.getDate().getTime() + "</Timestamp>");
					logMessage.append("<Source>SUBSCRIPTION</Source>");
					logMessage.append("<Topic>" + message.getTopic() + "</Topic>");
					logMessage.append("<QoS>" + message.getMessage().getQos() + "</QoS>");
					logMessage.append("<Retained>" + message.getMessage().isRetained() + "</Retained>");
					logMessage.append("<Payload>" + Base64.encodeBase64String(message.getMessage().getPayload()) + "</Payload>");
					logMessage.append("</MqttMessage>");
					
					logger.info(logMessage.toString());					
				}
				else
				{
					// If no messages present, sleep a bit
					Thread.sleep(10);
				}
			}
			catch (InterruptedException e)
			{				
				// Not expected
				logger.error("Thread interrupted", e);
			}
		}
	}
		
	public int getMessagesToProcess()
	{
		return queue.size();
	}
}
