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

import pl.baczkowicz.mqttspy.configuration.generated.ConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.Connectivity;
import pl.baczkowicz.mqttspy.configuration.generated.Formatting;
import pl.baczkowicz.mqttspy.configuration.generated.MqttSpyConfiguration;
import pl.baczkowicz.mqttspy.xml.XMLException;
import pl.baczkowicz.mqttspy.xml.XMLParser;

/**
 * 
 * Manages loading and saving configuration files.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class ConfigurationManager extends XMLParser
{
	public static final String VERSION_PROPERTY = "versionNumber";
	
	private static final String PACKAGE = "pl.baczkowicz.mqttspy.configuration.generated";
	
	private static final String SCHEMA = "/mqtt-spy-configuration.xsd";

	public static final String DEFAULT_FILE_NAME = "mqtt-spy-configuration.xml";
	
	public static final String DEFAULT_PROPERTIES_FILE_NAME = "/mqtt-spy.properties";
	
	public static final String DEFAULT_DIRECTORY = System.getProperty("user.home");
	
	private MqttSpyConfiguration configuration;
	
	private List<ConfiguredConnectionDetails> connections = new ArrayList<ConfiguredConnectionDetails>();
	
	private int lastUsedId = 0;

	private File loadedConfigurationFile;

	private final Properties properties;

	public ConfigurationManager() throws XMLException
	{
		super(SCHEMA, PACKAGE);
					
		// Create empty configuration
		this.configuration = new MqttSpyConfiguration();
		this.configuration.setConnectivity(new Connectivity());
		this.configuration.setFormatting(new Formatting());		
		
		this.properties = readPropertyFile(DEFAULT_PROPERTIES_FILE_NAME);
	}

	public int getNextAvailableId()
	{
		lastUsedId++;
		return lastUsedId;
	}
	
	public MqttSpyConfiguration loadConfiguration(final File file) throws XMLException
	{
		// TODO: show the error somewhere
		configuration = (MqttSpyConfiguration) loadFromFile(file);
		createConnections();
		loadedConfigurationFile = file;
		return configuration;
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

	public MqttSpyConfiguration getConfiguration()
	{
		return configuration;
	}
	
	public List<ConfiguredConnectionDetails> getConnections()
	{
		return connections;
	}

	public void saveConfiguration(final String filename) throws XMLException
	{
		// TODO: show the error somewhere
		saveToFile(filename, configuration);
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
	
	public File getLoadedConfigurationFile()
	{
		return loadedConfigurationFile;
	}
	
	public boolean isConfigurationReadOnly()
	{
		if (loadedConfigurationFile != null && !loadedConfigurationFile.canWrite())
		{
			// TODO: probably not needed anymore
			// DialogUtils.showReadOnlyWarning(configurationFile.getAbsolutePath());						
			return true;
		}
		
		return false;
	}

	public void createDefaultConfigurationFile()
	{
		try
		{
			final File orig = new File(ConfigurationManager.class.getResource("/" + ConfigurationManager.DEFAULT_FILE_NAME).toURI()); 
			final File dest = new File(getDefaultConfigurationFile().getAbsolutePath());
		
			Files.copy(orig.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);			
		}
		catch (IOException | URISyntaxException e)
		{
			// TODO: show the error somewhere
			e.printStackTrace();
		}		
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

	public String getProperty(final String propertyName)
	{
		return properties.getProperty(propertyName, "");
	}
}
