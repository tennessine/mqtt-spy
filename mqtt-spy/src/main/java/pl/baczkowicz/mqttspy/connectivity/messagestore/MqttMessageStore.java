package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class MqttMessageStore
{
	final static Logger logger = LoggerFactory.getLogger(MqttMessageStore.class);

	public static final int DEFAULT_MAX_SIZE = 5000;

	private final Deque<MqttContent> messages;

	private final int maxSize;
	
	public MqttMessageStore(final int maxSize)
	{
		this.maxSize = maxSize;
		this.messages = new LinkedBlockingDeque<MqttContent>();
	}
	
	public void clear()
	{
		messages.clear();
	}

	public MqttContent add(final MqttContent message)
	{
		MqttContent removed = null;
		
		if (isMaxSize())
		{
			removed = messages.remove();
		}

		// Store the message
		messages.add(message);
		return removed;
	}
	
	public boolean isMaxSize()
	{
		return messages.size() == maxSize;
	}

	public Deque<MqttContent> getMessages()
	{
		return messages;
	}
}
