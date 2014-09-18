package pl.baczkowicz.mqttspy.stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionIntervalStats
{
	public Map<String, Long> messageCount = new HashMap<>();
	
	public double overallCount = 0;
	
	public void add(final String topic)
	{
		overallCount++;
		
		if (messageCount.get(topic) == null)
		{
			messageCount.put(topic, (long) 1);
		}
		else
		{
			messageCount.put(topic, messageCount.get(topic) + 1);
		}		
	}
	
	public void add(final List<String> topics)
	{
		overallCount++;
		
		for (final String topic : topics)
		{
			if (messageCount.get(topic) == null)
			{
				messageCount.put(topic, (long) 1);
			}
			else
			{
				messageCount.put(topic, messageCount.get(topic) + 1);
			}
		}
	}
	
	public void reset()
	{
		overallCount = 0;
		messageCount.clear();
	}



	public ConnectionIntervalStats average(final int interval)
	{
		final ConnectionIntervalStats avg = new ConnectionIntervalStats();
		avg.overallCount = overallCount / interval;
		
		// TODO: for topics
		
		return avg;
	}
	
	public void plus(final ConnectionIntervalStats cs)
	{
		overallCount = overallCount + cs.overallCount;
		for (final String topic : cs.messageCount.keySet())
		{
			if (messageCount.containsKey(topic))
			{
				messageCount.put(topic, messageCount.get(topic) + cs.messageCount.get(topic));
			}
			else
			{
				messageCount.put(topic, cs.messageCount.get(topic));
			}
		}
	}
	public void minus(final ConnectionIntervalStats cs)
	{
		overallCount = overallCount - cs.overallCount;
		for (final String topic : cs.messageCount.keySet())
		{
			if (messageCount.containsKey(topic))
			{
				messageCount.put(topic, messageCount.get(topic) - cs.messageCount.get(topic));
			}			
		}		
	}
}
