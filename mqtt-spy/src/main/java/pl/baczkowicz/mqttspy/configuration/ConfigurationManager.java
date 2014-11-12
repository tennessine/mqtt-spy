package pl.baczkowicz.mqttspy.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.Connectivity;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.Formatting;
import pl.baczkowicz.mqttspy.configuration.generated.MqttSpyConfiguration;
import pl.baczkowicz.mqttspy.configuration.generated.UserInterfaceMqttConnectionDetailsV010;
import pl.baczkowicz.mqttspy.events.EventManager;
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
public class ConfigurationManager extends PropertyFileLoader
{
	final static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	
	public static final String VERSION_INFO_URL = "application.update.url";
	
	public static final String PACKAGE = "pl.baczkowicz.mqttspy.configuration.generated";
	
	public static final String SCHEMA = "/mqtt-spy-configuration.xsd";
	
	public static final String COMMON_SCHEMA = "/mqtt-spy-common.xsd";

	public static final String DEFAULT_FILE_NAME = "mqtt-spy-configuration.xml";
	
	public static final String DEFAULT_PROPERTIES_FILE_NAME = "/mqtt-spy.properties";
	
	public static final String DEFAULT_HOME_DIRECTORY = getDefaultHomeDirectory();
	
	// private static final String DEFAULT_DIRECTORY = ;
	
	private static final String DEFAULT_HOME_DIRECTORY_NAME = "mqtt-spy";
	
	private MqttSpyConfiguration configuration;
	
	private List<ConfiguredConnectionDetails> connections = new ArrayList<ConfiguredConnectionDetails>();
	
	private int lastUsedId = 0;

	private File loadedConfigurationFile;

	private Exception lastException;

	private EventManager eventManager;
	
	private final XMLParser parser;

	public ConfigurationManager(final EventManager eventManager) throws XMLException
	{
		super(DEFAULT_PROPERTIES_FILE_NAME);
		
		this.parser = new XMLParser(PACKAGE, new String[] {COMMON_SCHEMA, SCHEMA});
					
		// Create empty configuration
		this.configuration = new MqttSpyConfiguration();
		this.configuration.setConnectivity(new Connectivity());
		this.configuration.setFormatting(new Formatting());		
		
		this.eventManager = eventManager;
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
			clear();
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
		for (final Object connectionDetails : getConfiguration().getConnectivity().getConnectionOrConnectionV2())
		{
			if (connectionDetails instanceof UserInterfaceMqttConnectionDetailsV010)
			{
				// Put the defaults at the point of loading the config, so we don't need to do it again
				ConfigurationUtils.populateConnectionDefaults((UserInterfaceMqttConnectionDetailsV010) connectionDetails);
				connections.add(new ConfiguredConnectionDetails(getNextAvailableId(), false, false, false, 
						(UserInterfaceMqttConnectionDetailsV010) connectionDetails));
			}
			else
			{
				// TODO: 0.1.1 does not support the new config yet
			}
		}		
	}
	
	public static File getDefaultConfigurationFile()
	{			
		return new File(getDefaultHomeDirectory() + ConfigurationManager.DEFAULT_FILE_NAME);
	}
	
	public static File getDefaultConfigurationFileDirectory()
	{			
		return new File(getDefaultHomeDirectory());
	}
	
	public static String getDefaultHomeDirectory()
	{
		final String filePathSeparator = System.getProperty("file.separator");
		String userHomeDirectory = System.getProperty("user.home");
		
		if (!userHomeDirectory.endsWith(filePathSeparator))
		{
			userHomeDirectory = userHomeDirectory + filePathSeparator;
		}
		
		return userHomeDirectory + DEFAULT_HOME_DIRECTORY_NAME + filePathSeparator;
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

	public boolean saveConfiguration()
	{
		if (isConfigurationWritable())
		{
			try
			{
				configuration.getConnectivity().getConnectionOrConnectionV2().clear();
				configuration.getConnectivity().getConnectionOrConnectionV2().addAll(connections);				
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

	public void clear()
	{
		connections.clear();
		configuration = null;
		loadedConfigurationFile = null;
		lastException =  null;
		lastUsedId = 0;
	}
}
