package pl.baczkowicz.mqttspy.daemon.connectivity;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.logger.LogParserUtils;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessageWithSubscriptions;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

/**
 * This class is responsible for handling received messages. One thread per connection expected here.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class MqttMessageLogger implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MqttMessageLogger.class);
	
	private final Queue<ReceivedMqttMessageWithSubscriptions> queue;

	private final DaemonMqttConnectionDetails connectionSettings;
	
	private boolean running;

	public MqttMessageLogger(final Queue<ReceivedMqttMessageWithSubscriptions> queue, final DaemonMqttConnectionDetails connectionSettings)
	{
		this.queue = queue;
		this.connectionSettings = connectionSettings;
	}
	
	public void run()
	{
		Thread.currentThread().setName("Message Logger");
		ThreadingUtils.logStarting();
		running = true;
		
		while (running)
		{
			try
			{
				if (queue.size() > 0)
				{
					logger.info(LogParserUtils.createReceivedMessageLog(queue.remove(), connectionSettings.getMessageLog()));					
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
		
		ThreadingUtils.logEnding();
	}
		
	public int getMessagesToProcess()
	{
		return queue.size();
	}
	
	public void stop()
	{
		running = false;
	}
}
