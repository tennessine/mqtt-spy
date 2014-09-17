package pl.baczkowicz.mqttspy.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.stats.generated.MqttSpyStats;
import pl.baczkowicz.mqttspy.xml.XMLParser;

@SuppressWarnings({"unchecked", "rawtypes"})
public class StatisticsManager
{
	final static Logger logger = LoggerFactory.getLogger(StatisticsManager.class);
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.stats.generated";
	
	public static final String SCHEMA = "/mqtt-spy-stats.xsd";
	
	private static final String STATS_FILENAME = "mqtt-spy-stats.xml";
	
	private static final int MAX_PERIOD = 60;
	
	private final XMLParser parser;

	private File statsFile;

	public static MqttSpyStats stats;
	
	public static Map<Integer, List<ConnectionStats>> runtimeMessagesPublished = new HashMap<>();
	
	public static Map<Integer, List<ConnectionStats>> runtimeMessagesReceived = new HashMap<>();	
	
	public StatisticsManager() throws XMLException
	{
		this.parser = new XMLParser(SCHEMA, PACKAGE);
		statsFile = new File(ConfigurationManager.getDefaultHomeDirectory() + STATS_FILENAME);

		new Thread(new Runnable()
		{			
			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(1000);
						nextSecond();
					}
					catch (InterruptedException e)
					{
						break;
					}
				}				
			}
		}).start();
	}
	
	public boolean loadStats()
	{
		try
		{
			stats = (MqttSpyStats) parser.loadFromFile(statsFile);
			return true;
		}
		catch (XMLException e)
		{
			logger.error("Cannot process the statistics file at " + statsFile.getAbsolutePath(), e);
		}
		catch (FileNotFoundException e)
		{
			logger.error("Cannot read the statistics file from " + statsFile.getAbsolutePath(), e);
		}
		
		// If reading the stats failed...
		final Random random = new Random();
		
		stats = new MqttSpyStats();
		stats.setID(random.nextLong());
		
		final GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date());
		try 
		{
		    stats.setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gc));
		} 
		catch (DatatypeConfigurationException e) 
		{
		    logger.error("Cannot create date for stats", e);
		}

		return false;
	}
	
	public boolean saveStats()
	{
		try
		{
			if(!statsFile.exists()) 
			{
				statsFile.createNewFile();
			} 
			parser.saveToFile(statsFile, 
					new JAXBElement(new QName("http://baczkowicz.pl/mqtt-spy-stats", "MqttSpyStats"), MqttSpyStats.class, stats));
			return true;
		}
		catch (XMLException | IOException e)
		{
			logger.error("Cannot save the statistics file", e);
		}
		
		return false;
	}

	public static void newConnection()
	{
		stats.setConnections(stats.getConnections() + 1);		
	}

	public static void newSubscription()
	{
		stats.setSubscriptions(stats.getSubscriptions() + 1);		
	}
	
	public static void messageReceived(final int connectionId, final List<String> subscriptions)
	{
		stats.setMessagesReceived(stats.getMessagesReceived() + 1);
				
		if (runtimeMessagesReceived.get(connectionId) == null)
		{
			runtimeMessagesReceived.put(connectionId, new ArrayList<ConnectionStats>());
			runtimeMessagesReceived.get(connectionId).add(new ConnectionStats());
		}		
			
		runtimeMessagesReceived.get(connectionId).get(0).add(subscriptions);		
	}
	
	public static void messagePublished(final int connectionId, final String topic)
	{
		stats.setMessagesPublished(stats.getMessagesPublished() + 1);		

		// TODO:
//		for (final int interval : runtimeMessagesPublished.keySet())
//		{
//			// Count runtime stats (per connection)
//			if (runtimeMessagesPublished.get(interval).get(connectionId) == null)
//			{
//				runtimeMessagesPublished.get(interval).put(connectionId, new ConnectionStats());
//			}		
//			runtimeMessagesPublished.get(interval).get(connectionId).add(topic);
//		}
	}
	
	public void nextSecond()
	{
		for (final Integer connectionId : runtimeMessagesReceived.keySet())
		{
			runtimeMessagesReceived.get(connectionId).add(0, new ConnectionStats());
			if (runtimeMessagesReceived.get(connectionId).size() > MAX_PERIOD)
			{
				runtimeMessagesReceived.get(connectionId).remove(MAX_PERIOD - 1);
			}
		}
		
		for (final Integer connectionId : runtimeMessagesReceived.keySet())
		{
			runtimeMessagesReceived.get(connectionId).add(0, new ConnectionStats());
			if (runtimeMessagesReceived.get(connectionId).size() > MAX_PERIOD)
			{
				runtimeMessagesReceived.get(connectionId).remove(MAX_PERIOD - 1);
			}
		}
	}
	
//	public static void resetRuntimeStats(final int interval)
//	{
//		for (final Integer id : runtimeMessagesReceived.keySet())
//		{
//			runtimeMessagesReceived.get(interval).get(id).reset();
//		}
//		
//		for (final Integer id : runtimeMessagesPublished.keySet())
//		{
//			runtimeMessagesPublished.get(interval).get(id).reset();
//		}
//	}
	
	public static ConnectionStats getMessagesPublished(final int interval, final int connectionId)
	{
		if (runtimeMessagesPublished.get(connectionId) == null)
		{
			return new ConnectionStats();
		}
		
		final ConnectionStats aggregatedConnectionStats = new ConnectionStats();
		for (int i = 0; i < interval; i++)
		{
			if (i == runtimeMessagesPublished.get(connectionId).size())
			{
				break;
			}
			
			final ConnectionStats cs = runtimeMessagesPublished.get(connectionId).get(i);
			
			aggregatedConnectionStats.add(cs);
		}
		aggregatedConnectionStats.average(interval);
		return aggregatedConnectionStats;
	}
	
	public static ConnectionStats getMessagesReceived(final int interval, final int connectionId)
	{
		if (runtimeMessagesReceived.get(connectionId) == null)
		{
			return new ConnectionStats();
		}
		
		final ConnectionStats aggregatedConnectionStats = new ConnectionStats();
		for (int i = 0; i < interval; i++)
		{
			if (i == runtimeMessagesReceived.get(connectionId).size())
			{
				break;
			}
			
			final ConnectionStats cs = runtimeMessagesReceived.get(connectionId).get(i);
			
			aggregatedConnectionStats.add(cs);
		}
		aggregatedConnectionStats.average(interval);
		return aggregatedConnectionStats;
	}
}
