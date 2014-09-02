package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class MqttMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(MqttMessageStore.class);

	public static final int DEFAULT_MAX_SIZE = 5000;

	private final Queue<MqttContent> messages;

	private final int maxSize;
	
	public MqttMessageStore(final int maxSize)
	{
		this.maxSize = maxSize;
		this.messages = new LinkedBlockingQueue<MqttContent>();
	}
	
	public void clear()
	{
		messages.clear();
	}

	public boolean add(final MqttContent message)
	{
		boolean removed = false;
		
		if (isMaxSize())
		{
			messages.remove();
			removed = true;
		}

		// Store the message
		messages.add(message);
		return removed;
	}
	
	public boolean isMaxSize()
	{
		return messages.size() == maxSize;
	}

	public Queue<MqttContent> getMessages()
	{
		return messages;
	}
}
