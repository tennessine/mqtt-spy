package pl.baczkowicz.mqttspy.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;

public class PropertyFileLoader
{	
	public static final String VERSION_PROPERTY = "application.version";
	
	public static final String BUILD_PROPERTY = "application.build";
	
	public static final String DOWNLOAD_URL = "application.download.url";	
	
	private final Properties properties;	
	
	public PropertyFileLoader(final String propertyFile) throws ConfigurationException
	{
		properties = readPropertyFile(propertyFile);
	}
	
	public static Properties readPropertyFile(final String location) throws ConfigurationException
	{
		final Properties fileProperties = new Properties();
	
		try
		{
			final InputStream inputStream = PropertyFileLoader.class.getResourceAsStream(location);
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
		
	public String getBuildNumber()
	{
		return getProperty(PropertyFileLoader.BUILD_PROPERTY);
	}
	
	public String getFullVersionNumber()
	{
		return getProperty(PropertyFileLoader.VERSION_PROPERTY) + "-" + getBuildNumber();
	}
	
	public String getFullVersionName()
	{
		return getProperty(PropertyFileLoader.VERSION_PROPERTY).replace("-", " ") + " (build " + getBuildNumber() + ")";
	}
}
