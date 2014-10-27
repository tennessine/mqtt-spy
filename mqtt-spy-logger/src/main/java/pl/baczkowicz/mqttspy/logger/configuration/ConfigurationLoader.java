package pl.baczkowicz.mqttspy.logger.configuration;

import java.io.File;
import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.exceptions.XMLException;
import pl.baczkowicz.mqttspy.common.xml.XMLParser;
import pl.baczkowicz.mqttspy.logger.configuration.generated.MqttSpyLoggerConfiguration;

public class ConfigurationLoader
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationLoader.class);
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.logger.configuration.generated";
	
	public static final String SCHEMA = "/mqtt-spy-logger-configuration.xsd";
	
	public static final String COMMON_SCHEMA = "/mqtt-spy-common-configuration.xsd";
	
	private final XMLParser parser;

	private MqttSpyLoggerConfiguration configuration;
	
	public ConfigurationLoader() throws XMLException
	{
		this.parser = new XMLParser(PACKAGE, new String[] {COMMON_SCHEMA, SCHEMA});					
	}
	
	public boolean loadConfiguration(final File file)
	{
		try
		{
			setConfiguration((MqttSpyLoggerConfiguration) parser.loadFromFile(file));			
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

	public MqttSpyLoggerConfiguration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final MqttSpyLoggerConfiguration configuration)
	{
		this.configuration = configuration;
	}
}
