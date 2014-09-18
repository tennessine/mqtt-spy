package pl.baczkowicz.mqttspy.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionStats
{
	final public List<ConnectionIntervalStats> runtimeStats = new ArrayList<>();
	
	final public Map<Integer, ConnectionIntervalStats> avgPeriods = new HashMap<>();
	
	public ConnectionStats(List<Integer> periods)
	{
		for (final int period : periods)
		{
			avgPeriods.put(period, new ConnectionIntervalStats());
		}
	}
}
