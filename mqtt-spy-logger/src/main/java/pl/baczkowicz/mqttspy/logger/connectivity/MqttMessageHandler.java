package pl.baczkowicz.mqttspy.logger.connectivity;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.logger.Utils;

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
					logger.info(Utils.createLog(queue.remove()));					
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
			}
		}
	}
		
	public int getMessagesToProcess()
	{
		return queue.size();
	}
}
