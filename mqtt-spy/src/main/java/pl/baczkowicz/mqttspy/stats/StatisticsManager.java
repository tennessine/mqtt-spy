package pl.baczkowicz.mqttspy.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
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
public class StatisticsManager implements Runnable
{
	final static Logger logger = LoggerFactory.getLogger(StatisticsManager.class);
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.stats.generated";
	
	public static final String SCHEMA = "/mqtt-spy-stats.xsd";
	
	private static final String STATS_FILENAME = "mqtt-spy-stats.xml";
	
	public final static List<Integer> periods = Arrays.asList(5, 30, 300);
	
	private final static int removeAfter = 301;
	
	private final XMLParser parser;

	private File statsFile;

	public static MqttSpyStats stats;
	
	public static Map<Integer, ConnectionStats> runtimeMessagesPublished = new HashMap<>();
	
	public static Map<Integer, ConnectionStats> runtimeMessagesReceived = new HashMap<>();	
	
	public StatisticsManager() throws XMLException
	{
		this.parser = new XMLParser(SCHEMA, PACKAGE);
		
		statsFile = new File(ConfigurationManager.getDefaultHomeDirectory() + STATS_FILENAME);

		new Thread(this).start();
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
			statsFile.mkdirs();
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
			logger.error("Cannot save the statistics file - " + statsFile.getAbsolutePath(), e);
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
		// Global stats (saved to XML)
		stats.setMessagesReceived(stats.getMessagesReceived() + 1);
				
		// Runtime stats
		if (runtimeMessagesReceived.get(connectionId) == null)
		{
			runtimeMessagesReceived.put(connectionId, new ConnectionStats(periods));
			runtimeMessagesReceived.get(connectionId).runtimeStats.add(new ConnectionIntervalStats());
		}		
			
		runtimeMessagesReceived.get(connectionId).runtimeStats.get(0).add(subscriptions);
	}
	
	public static void messagePublished(final int connectionId, final String topic)
	{
		// Global stats (saved to XML)
		stats.setMessagesPublished(stats.getMessagesPublished() + 1);		

		// Runtime stats
		if (runtimeMessagesPublished.get(connectionId) == null)
		{
			runtimeMessagesPublished.put(connectionId, new ConnectionStats(periods));
			runtimeMessagesPublished.get(connectionId).runtimeStats.add(new ConnectionIntervalStats());
		}		
			
		runtimeMessagesPublished.get(connectionId).runtimeStats.get(0).add(topic);
	}
	
	public static void nextInterval(final Map<Integer, ConnectionStats> runtimeMessages)
	{
		for (final Integer connectionId : runtimeMessages.keySet())
		{
			final Map<Integer, ConnectionIntervalStats> avgs = runtimeMessages.get(connectionId).avgPeriods;
			final List<ConnectionIntervalStats> runtime = runtimeMessages.get(connectionId).runtimeStats;	
			
			final ConnectionIntervalStats lastComplete = runtime.size() > 0 ? runtime.get(0) : new ConnectionIntervalStats(); 
					
			// Add the last complete and subtract over the interval
			for (final int period : avgs.keySet())
			{
				avgs.get(period).plus(lastComplete);
				if (runtime.size() > period)
				{
					avgs.get(period).minus(runtime.get(period));
				}
			}
			
			runtime.add(0, new ConnectionIntervalStats());
			
			if (runtime.size() > removeAfter)
			{
				runtime.remove(removeAfter);
			}
		}
	}
	
	public static void nextInterval()
	{		
		nextInterval(runtimeMessagesPublished);
		nextInterval(runtimeMessagesReceived);		
	}
	
	public ConnectionIntervalStats getMessagesPublished(final int connectionId, final int period)
	{
		if (runtimeMessagesPublished.get(connectionId) == null)
		{
			return new ConnectionIntervalStats();
		}
				
		return runtimeMessagesPublished.get(connectionId).avgPeriods.get(period).average(period);
	}
	
	public ConnectionIntervalStats getMessagesReceived(final int connectionId, final int period)
	{
		if (runtimeMessagesReceived.get(connectionId) == null)
		{
			return new ConnectionIntervalStats();
		}
		
		return runtimeMessagesReceived.get(connectionId).avgPeriods.get(period).average(period);
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(1000);
				nextInterval();
			}
			catch (InterruptedException e)
			{
				break;
			}
		}					
	}
}
