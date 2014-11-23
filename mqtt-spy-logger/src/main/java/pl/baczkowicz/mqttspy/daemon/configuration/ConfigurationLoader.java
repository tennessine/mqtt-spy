package pl.baczkowicz.mqttspy.daemon.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import pl.baczkowicz.mqttspy.configuration.PropertyFileLoader;
import pl.baczkowicz.mqttspy.daemon.configuration.generated.MqttSpyDaemonConfiguration;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.xml.XMLParser;

public class ConfigurationLoader extends PropertyFileLoader
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.daemon.configuration.generated";
	
	public static final String SCHEMA = "/mqtt-spy-daemon-configuration.xsd";
	
	public static final String COMMON_SCHEMA = "/mqtt-spy-common.xsd";
	
	public static final String DEFAULT_PROPERTIES_FILE_NAME = "/mqtt-spy-daemon.properties";
	
	private final XMLParser parser;

	private MqttSpyDaemonConfiguration configuration;
	
	public ConfigurationLoader() throws XMLException
	{
		super(DEFAULT_PROPERTIES_FILE_NAME);
		
		this.parser = new XMLParser(PACKAGE, new String[] {COMMON_SCHEMA, SCHEMA});					
	}
	
	public boolean loadConfiguration(final File file)
	{
		try
		{
			setConfiguration((MqttSpyDaemonConfiguration) parser.loadFromFile(file));	
			populateDefaults();
			return true;
		}
		catch (XMLException e)
		{							
			logger.error("Cannot process the configuration file at " + file.getAbsolutePath(), e);
		}
		catch (FileNotFoundException e)
		{
			logger.error("Cannot read the configuration file from " + file.getAbsolutePath(), e);
		}
		
		return false;
	}

	private void populateDefaults()
	{
		for (final ScriptDetails scriptDetails : configuration.getConnection().getBackgroundScript())
		{
			if (scriptDetails.isRepeat() == null)
			{
				scriptDetails.setRepeat(false);
			}
		}
		
		// Connection
		if (configuration.getConnection().getMessageLog().isLogConnection() == null)
		{
			configuration.getConnection().getMessageLog().setLogConnection(false);
		}
		
		// QoS
		if (configuration.getConnection().getMessageLog().isLogQos() == null)
		{
			configuration.getConnection().getMessageLog().setLogQos(false);
		}
		
		// Retained
		if (configuration.getConnection().getMessageLog().isLogRetained() == null)
		{
			configuration.getConnection().getMessageLog().setLogRetained(false);
		}
		
		// Subscription
		if (configuration.getConnection().getMessageLog().isLogSubscription() == null)
		{
			configuration.getConnection().getMessageLog().setLogSubscription(false);
		}
	}

	public MqttSpyDaemonConfiguration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final MqttSpyDaemonConfiguration configuration)
	{
		this.configuration = configuration;
	}
}
