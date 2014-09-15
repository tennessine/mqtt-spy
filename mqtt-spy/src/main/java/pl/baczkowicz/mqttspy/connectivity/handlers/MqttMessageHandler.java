package pl.baczkowicz.mqttspy.connectivity.handlers;

import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

/**
 * This class is responsible for handling received messages. One thread per connection expected here.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class MqttMessageHandler implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MqttMessageHandler.class);
	
	private final Queue<MqttContent> queue;

	private MqttConnection connection;

	public MqttMessageHandler(final MqttConnection connection, final Queue<MqttContent> queue)
	{
		this.queue = queue;
		this.connection = connection;
	}
	
	public void run()
	{
		logger.debug("Starting processing thread for connection " + connection.getProperties().getName());
		while (true)
		{
			try
			{
				if (queue.size() > 0)
				{
					final MqttContent content = queue.remove();
					connection.messageReceived(content);
					// Let other threads do stuff
					Thread.sleep(1);
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
				e.printStackTrace();
			}
		}
	}
		
	public int getMessagesToProcess()
	{
		return queue.size();
	}

}
