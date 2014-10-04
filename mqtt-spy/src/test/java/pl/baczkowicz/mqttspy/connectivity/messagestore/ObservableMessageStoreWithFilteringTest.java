package pl.baczkowicz.mqttspy.connectivity.messagestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.storage.ObservableMessageStoreWithFiltering;

public class ObservableMessageStoreWithFilteringTest
{
	/** Class under test. */
	private ObservableMessageStoreWithFiltering store;
	
	@Before
	public void setUp() throws Exception
	{
		store = new ObservableMessageStoreWithFiltering("test", 5, 5, 5, new LinkedBlockingQueue<MqttSpyUIEvent>(), null);
	}

	@Test
	public final void testStoreMaxSize()
	{
		final MqttContent message = new MqttContent(1, "t1", new MqttMessage("test1".getBytes()));
		
		store.messageReceived(message);
				
		assertEquals(1, store.getMessages().size());
		
		store.messageReceived(message);
		store.messageReceived(message);
		store.messageReceived(message);
		store.messageReceived(message);
		
		assertEquals(5, store.getMessages().size());
		
		store.messageReceived(message);
		
		assertEquals(5, store.getMessages().size());
	}

	@Test
	public final void testDefaultActiveFilters()
	{
		testStoreMaxSize();
		assertTrue(store.getFilters().contains("t1"));
		
		final MqttContent message = new MqttContent(2, "t2", new MqttMessage("test2".getBytes()));
		store.messageReceived(message);
		assertTrue(store.getFilters().contains("t2"));
	}
	
	@Test
	public final void testActiveFiltersWhenNotAllEnabled()	
	{
		testDefaultActiveFilters();
		store.updateFilter("t2", false);
		
		final MqttContent message = new MqttContent(3, "t3", new MqttMessage("test3".getBytes()));
		store.messageReceived(message);
		assertFalse(store.getFilters().contains("t3"));
	}
}
