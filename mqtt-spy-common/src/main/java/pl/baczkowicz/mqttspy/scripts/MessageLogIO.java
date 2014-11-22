package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.logger.LogParserUtils;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.utils.Utils;

public class MessageLogIO implements MessageLogIOInterface, Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MessageLogIO.class);
	
	private List<ReceivedMqttMessage> messageLog;	

	private long replayDate;
	
	private boolean running = false;

	private long lastUpdated;

	private double speed;

	public int readFromFile(String logLocation)
	{
		try
		{
			messageLog = LogParserUtils.readAndConvertMessageLog(new File(logLocation));
						
			return messageLog.size();
		}
		catch (Exception e)
		{
			logger.error("Cannot read message log at " + logLocation, e);
			return 0;
		}
	}
	
	public void start()
	{
		if (messageLog != null)
		{
			replayDate = messageLog.get(0).getDate().getTime();
			lastUpdated = Utils.getMonotonicTimeInMilliseconds();
			running = true;
			
			new Thread(this).start();
		}		
	}

//	public void pause()
//	{
//		// TODO Auto-generated method stub
//		
//	}

	public void stop()
	{
		running = false;		
	}

	public void setSpeed(double speed)
	{
		this.speed = speed;
	}

	public boolean isReadyToPublish(final int messageIndex)
	{
		if (messageLog != null && messageLog.size() > messageIndex)
		{
			if (replayDate > messageLog.get(messageIndex).getDate().getTime())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public ReceivedMqttMessage get(final int messageIndex)
	{
		if (messageLog != null && messageLog.size() > messageIndex)
		{
			return messageLog.get(messageIndex);
		}
		
		return null;
	}
	
	public void run()
	{
		while (running)
		{
			final long now = Utils.getMonotonicTimeInMilliseconds();
			
			if (now > lastUpdated)
			{
				final long sinceLastUpdated = now - lastUpdated;				
				final double increase = sinceLastUpdated * speed;
				
				replayDate =+ (long) increase;
				lastUpdated = now;
			}
			
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}	
		stop();
	}

}
