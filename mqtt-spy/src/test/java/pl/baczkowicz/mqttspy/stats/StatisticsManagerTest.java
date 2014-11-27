package pl.baczkowicz.mqttspy.stats;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StatisticsManagerTest
{
	private StatisticsManager statisticsManager;
	
	@Before
	public void setUp() throws Exception
	{
		statisticsManager = new StatisticsManager();
		statisticsManager.loadStats();
	}

	@Test
	public final void testAverage()
	{
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		StatisticsManager.nextInterval();
		assertEquals(0.4, StatisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		StatisticsManager.nextInterval();
		assertEquals(1.4, StatisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		StatisticsManager.nextInterval();
		StatisticsManager.nextInterval();
		StatisticsManager.nextInterval();
		assertEquals(1.4, StatisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		StatisticsManager.nextInterval();
		assertEquals(1.0, StatisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		StatisticsManager.nextInterval();
		assertEquals(0, StatisticsManager.getMessagesPublished(3, 5).overallCount, 0);
	}

}
