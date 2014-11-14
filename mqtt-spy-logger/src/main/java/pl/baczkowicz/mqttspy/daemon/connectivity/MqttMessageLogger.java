package pl.baczkowicz.mqttspy.daemon.connectivity;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.daemon.configuration.generated.DaemonMqttConnectionDetails;
import pl.baczkowicz.mqttspy.logger.LogParserUtils;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;

/**
 * This class is responsible for handling received messages. One thread per connection expected here.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class MqttMessageLogger implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MqttMessageLogger.class);
	
	private final Queue<ReceivedMqttMessage> queue;

	private final DaemonMqttConnectionDetails connectionSettings;

	public MqttMessageLogger(final Queue<ReceivedMqttMessage> queue, final DaemonMqttConnectionDetails connectionSettings)
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
					logger.info(LogParserUtils.createLog(queue.remove(), connectionSettings.getMessageLog()));					
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
