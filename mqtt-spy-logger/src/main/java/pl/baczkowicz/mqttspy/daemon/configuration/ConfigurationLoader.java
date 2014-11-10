package pl.baczkowicz.mqttspy.daemon.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.daemon.configuration.generated.MqttSpyDaemonConfiguration;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.xml.XMLParser;

public class ConfigurationLoader
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.daemon.configuration.generated";
	
	public static final String SCHEMA = "/mqtt-spy-daemon-configuration.xsd";
	
	public static final String COMMON_SCHEMA = "/mqtt-spy-common.xsd";
	
	private final XMLParser parser;

	private MqttSpyDaemonConfiguration configuration;
	
	public ConfigurationLoader() throws XMLException
	{
		this.parser = new XMLParser(PACKAGE, new String[] {COMMON_SCHEMA, SCHEMA});					
	}
	
	public boolean loadConfiguration(final File file)
	{
		try
		{
			setConfiguration((MqttSpyDaemonConfiguration) parser.loadFromFile(file));			
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

	public MqttSpyDaemonConfiguration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final MqttSpyDaemonConfiguration configuration)
	{
		this.configuration = configuration;
	}
}
