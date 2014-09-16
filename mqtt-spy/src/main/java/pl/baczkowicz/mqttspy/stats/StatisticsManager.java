package pl.baczkowicz.mqttspy.stats;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
	
	private final XMLParser parser;

	private File statsFile;

	public static MqttSpyStats stats;
	
	public static Map<Integer, Long> runtimeMessagesPublished = new HashMap<>();
	
	public static Map<Integer, Long> runtimeMessagesReceived = new HashMap<>();
	
	public StatisticsManager() throws XMLException
	{
		this.parser = new XMLParser(SCHEMA, PACKAGE);
		statsFile = new File(ConfigurationManager.getDefaultHomeDirectory() + STATS_FILENAME);
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
			parser.saveToFile(statsFile, 
					new JAXBElement(new QName("http://baczkowicz.pl/mqtt-spy-stats", "MqttSpyStats"), MqttSpyStats.class, stats));
			return true;
		}
		catch (XMLException e)
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
	
	public static void messageReceived(final int connectionId)
	{
		stats.setMessagesReceived(stats.getMessagesReceived() + 1);
		
		// Count runtime stats (per connection)
		if (runtimeMessagesReceived.get(connectionId) == null)
		{
			runtimeMessagesReceived.put(connectionId, (long) 0);
		}		
		runtimeMessagesReceived.put(connectionId, runtimeMessagesReceived.get(connectionId) + 1);
	}
	
	public static void messagePublished(final int connectionId)
	{
		stats.setMessagesPublished(stats.getMessagesPublished() + 1);		

		// Count runtime stats (per connection)
		if (runtimeMessagesPublished.get(connectionId) == null)
		{
			runtimeMessagesPublished.put(connectionId, (long) 0);
		}		
		runtimeMessagesPublished.put(connectionId, runtimeMessagesPublished.get(connectionId) + 1);
	}
	
	public static void resetRuntimeStats()
	{
		for (final Integer id : runtimeMessagesReceived.keySet())
		{
			runtimeMessagesReceived.put(id, (long) 0);
		}
		
		for (final Integer id : runtimeMessagesPublished.keySet())
		{
			runtimeMessagesPublished.put(id, (long) 0);
		}
	}
}
