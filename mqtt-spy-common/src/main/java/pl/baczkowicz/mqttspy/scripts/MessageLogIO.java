package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.logger.LogParserUtils;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;
import pl.baczkowicz.mqttspy.utils.Utils;

public class MessageLogIO implements IMessageLogIO, Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(MessageLogIO.class);
	
	private List<ReceivedMqttMessage> messageLog;	

	private long replayDate;
	
	private boolean running = false;

	private long lastUpdated;

	private double speed = 1;

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

	public void setSpeed(double newSpeed)
	{
		if (newSpeed > 1)
		{
			if (this.speed <= 1)
			{
				logger.info("Warp enabled. Changing replay speed from {} to {}", this.speed, newSpeed);
			}
			else
			{
				logger.info("Warp still on. Changing replay speed from {} to {}", this.speed, newSpeed);
			}		
		}
		else if (newSpeed == 1)
		{
			logger.info("Back to normal. Changing replay speed from {} to {}", this.speed, newSpeed);
		}
		else
		{
			logger.info("Tea time! Changing replay speed from {} to {}", this.speed, newSpeed);
		}
		
		this.speed = newSpeed;
	}

	public boolean isReadyToPublish(final int messageIndex)
	{
		if (messageLog != null && messageLog.size() > messageIndex)
		{
			// logger.info("Replay date = {}, message date = {}", replayDate, messageLog.get(messageIndex).getDate().getTime());
			if (replayDate > messageLog.get(messageIndex).getDate().getTime())
			{
				return true;
			}
		}
		
		return false;
	}
	
	public ReceivedMqttMessage getMessage(final int messageIndex)
	{
		if (messageLog != null && messageLog.size() > messageIndex)
		{
			return messageLog.get(messageIndex);
		}
		
		return null;
	}
	
	public void run()
	{
		Thread.currentThread().setName("Message Log IO");
		ThreadingUtils.logStarting();
		
		while (running)
		{
			final long now = Utils.getMonotonicTimeInMilliseconds();
			
			if (now > lastUpdated)
			{
				final long sinceLastUpdated = now - lastUpdated;				
				final double increase = sinceLastUpdated * speed;
				
				replayDate =  replayDate + (long) increase;
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
		
		ThreadingUtils.logEnding();
	}
	
	public int getMessageCount()
	{
		if (messageLog == null)
		{
			return 0;
		}
		
		return messageLog.size();
	}

}
