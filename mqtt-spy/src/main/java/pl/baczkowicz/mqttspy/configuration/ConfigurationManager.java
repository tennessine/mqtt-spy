package pl.baczkowicz.mqttspy.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.Connectivity;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.Formatting;
import pl.baczkowicz.mqttspy.configuration.generated.MqttSpyConfiguration;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.ui.utils.DialogUtils;
import pl.baczkowicz.mqttspy.xml.XMLParser;

/**
 * 
 * Manages loading and saving configuration files.
 * 
 * @author Kamil Baczkowicz
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationManager
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	
	public static final String VERSION_PROPERTY = "application.version";
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.configuration.generated";
	
	public static final String SCHEMA = "/mqtt-spy-configuration.xsd";

	public static final String DEFAULT_FILE_NAME = "mqtt-spy-configuration.xml";
	
	public static final String DEFAULT_PROPERTIES_FILE_NAME = "/mqtt-spy.properties";
	
	public static final String DEFAULT_DIRECTORY = System.getProperty("user.home");
	
	private MqttSpyConfiguration configuration;
	
	private List<ConfiguredConnectionDetails> connections = new ArrayList<ConfiguredConnectionDetails>();
	
	private int lastUsedId = 0;

	private File loadedConfigurationFile;

	private final Properties properties;

	private Exception lastException;

	private EventManager eventManager;
	
	private final XMLParser parser;

	public ConfigurationManager(final EventManager eventManager) throws XMLException
	{
		this.parser = new XMLParser(SCHEMA, PACKAGE);
					
		// Create empty configuration
		this.configuration = new MqttSpyConfiguration();
		this.configuration.setConnectivity(new Connectivity());
		this.configuration.setFormatting(new Formatting());		
		
		this.eventManager = eventManager;
		this.properties = readPropertyFile(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public int getNextAvailableId()
	{
		lastUsedId++;
		return lastUsedId;
	}
	
	public boolean loadConfiguration(final File file)
	{
		try
		{
			configuration = (MqttSpyConfiguration) parser.loadFromFile(file);
			createConnections();
			createConfigurationDefaults();
			loadedConfigurationFile = file;
			return true;
		}
		catch (XMLException e)
		{
			setLastException(e);
			DialogUtils.showInvalidConfigurationFileDialog("Cannot process the given configuration file. See the log file for more details.");					
			logger.error("Cannot process the configuration file at " + file.getAbsolutePath(), e);
			eventManager.notifyConfigurationFileReadFailure();
		}
		catch (FileNotFoundException e)
		{
			setLastException(e);
			DialogUtils.showInvalidConfigurationFileDialog("Cannot read the given configuration file. See the log file for more details.");
			logger.error("Cannot read the configuration file from " + file.getAbsolutePath(), e);
			eventManager.notifyConfigurationFileReadFailure();
		}
		
		return false;
	}
	
	private void createConfigurationDefaults()
	{
		if (configuration.getFormatting() == null)
		{
			configuration.setFormatting(new Formatting());
		}
	}
	
	private void createConnections()
	{
		for (final ConnectionDetails connectionDetails : getConfiguration().getConnectivity().getConnection())
		{
			// Put the defaults at the point of loading the config, so we don't need to do it again
			ConfigurationUtils.populateConnectionDefaults(connectionDetails);
			connections.add(new ConfiguredConnectionDetails(getNextAvailableId(), false, false, false, connectionDetails));
		}		
	}
	
	public static File getDefaultConfigurationFile()
	{
		final String filePathSeparator = System.getProperty("file.separator");
		String homeDirectory = DEFAULT_DIRECTORY;
		
		if (!homeDirectory.endsWith(filePathSeparator))
		{
			homeDirectory = homeDirectory + filePathSeparator;
		}
		
		return new File(homeDirectory + ConfigurationManager.DEFAULT_FILE_NAME);
	}

	public boolean createDefaultConfigurationFile()
	{
		final File dest = getDefaultConfigurationFile();
		
		try
		{
			final File orig = new File(ConfigurationManager.class.getResource("/" + ConfigurationManager.DEFAULT_FILE_NAME).toURI()); 			
			Files.copy(orig.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return true;
		}
		catch (IOException | URISyntaxException e)
		{
			setLastException(e);
			logger.error("Cannot create the default configuration file at " + dest.getAbsolutePath(), e);
			eventManager.notifyConfigurationFileCopyFailure();			
		}		
		
		return false;
	}
	
	public static Properties readPropertyFile(final String location) throws ConfigurationException
	{
		final Properties fileProperties = new Properties();
	
		try
		{
			final InputStream inputStream = ConfigurationManager.class.getResourceAsStream(location);
			fileProperties.load(inputStream);
			
			if (inputStream == null)
			{
				throw new FileNotFoundException("Property file '" + location + "' not found in the classpath");
			}
		}
		catch (IOException e)
		{
			throw new ConfigurationException("Cannot load the properties file", e);
		}
		return fileProperties;
	}

	public boolean saveConfiguration()
	{
		if (isConfigurationWritable())
		{
			try
			{
				configuration.getConnectivity().getConnection().clear();
				configuration.getConnectivity().getConnection().addAll(connections);				
				populateMissingFormatters(configuration.getFormatting().getFormatter(), connections);
				
				parser.saveToFile(loadedConfigurationFile, 
						new JAXBElement(new QName("http://baczkowicz.pl/mqtt-spy-configuration", "MqttSpyConfiguration"), MqttSpyConfiguration.class, configuration));
				return true;
			}
			catch (XMLException e)
			{
				setLastException(e);
				logger.error("Cannot save the configuration file", e);
				eventManager.notifyConfigurationFileWriteFailure();
			}
		}
		
		return false;
	}
	
	private void populateMissingFormatters(final List<FormatterDetails> formatters, final List<ConfiguredConnectionDetails> connections)
	{
		for (final ConfiguredConnectionDetails connection : connections)
		{
			if (connection.getFormatter() == null)
			{
				continue;
			}
			
			boolean formatterFound = false;
			
			for (final FormatterDetails formatter : formatters)
			{
				if (((FormatterDetails) connection.getFormatter()).getID().equals(formatter.getID()))
				{
					formatterFound = true;
				}
			}
			
			if (!formatterFound)
			{
				formatters.add((FormatterDetails) connection.getFormatter());
			}
		}
	}
	
	// ===============================
	// === Setters and getters =======
	// ===============================

	public Exception getLastException()
	{
		return lastException;
	}

	public void setLastException(Exception lastException)
	{
		this.lastException = lastException;
	}
	
	public String getProperty(final String propertyName)
	{
		return properties.getProperty(propertyName, "");
	}
	
	public File getLoadedConfigurationFile()
	{
		return loadedConfigurationFile;
	}
	
	public boolean isConfigurationWritable()
	{
		if (loadedConfigurationFile != null && loadedConfigurationFile.canWrite())
		{
			return true;
		}
		return false;
	}
	
	public boolean isConfigurationReadOnly()
	{
		if (loadedConfigurationFile != null && !loadedConfigurationFile.canWrite())
		{					
			return true;
		}
		
		return false;
	}
	
	public MqttSpyConfiguration getConfiguration()
	{
		return configuration;
	}
	
	public List<ConfiguredConnectionDetails> getConnections()
	{
		return connections;
	}
}
