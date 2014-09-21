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
		statisticsManager.nextInterval();
		assertEquals(0.4, statisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.messagePublished(3, "/test");
		statisticsManager.nextInterval();
		assertEquals(1.4, statisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		statisticsManager.nextInterval();
		statisticsManager.nextInterval();
		statisticsManager.nextInterval();
		assertEquals(1.4, statisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		statisticsManager.nextInterval();
		assertEquals(1.0, statisticsManager.getMessagesPublished(3, 5).overallCount, 0);
		
		statisticsManager.nextInterval();
		assertEquals(0, statisticsManager.getMessagesPublished(3, 5).overallCount, 0);
	}

}
