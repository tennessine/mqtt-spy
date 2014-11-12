package pl.baczkowicz.mqttspy.daemon.connectivity;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.utils.MessageLoggingUtils;

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

	private final DaemonMqttConnectionDetails connectionSettings;

	public MqttMessageHandler(final Queue<ReceivedMqttMessage> queue, final DaemonMqttConnectionDetails connectionSettings)
	{
		this.queue = queue;
		this.connectionSettings = connectionSettings;
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				if (queue.size() > 0)
				{
					logger.info(MessageLoggingUtils.createLog(queue.remove(), connectionSettings.getMessageLog()));					
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
