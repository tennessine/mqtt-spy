package pl.baczkowicz.mqttspy.ui.properties;

import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.UserCredentials;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionProperties;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;

public class RuntimeConnectionProperties extends MqttConnectionProperties
{
	private ConfiguredConnectionDetails configuredProperties;

	public RuntimeConnectionProperties(final ConfiguredConnectionDetails configuredProperties, final UserCredentials userCredentials) throws ConfigurationException
	{
		super(	configuredProperties.getName(), 
				configuredProperties.getServerURI(), 
				configuredProperties.getClientID(), 
				userCredentials,
				configuredProperties.getLastWillAndTestament(),
				configuredProperties.isCleanSession(), 
				configuredProperties.getConnectionTimeout(), 
				configuredProperties.getKeepAliveInterval());
		
		this.configuredProperties = configuredProperties;		
	}
	
	public FormatterDetails getFormatter()
	{
		return (FormatterDetails) configuredProperties.getFormatter();
	}

	public int getMaxMessagesStored()
	{
		return configuredProperties.getMaxMessagesStored();
	}

	public boolean isAutoConnect()
	{
		return configuredProperties.isAutoConnect();
	}
	
	public ConfiguredConnectionDetails getConfiguredProperties()
	{
		return this.configuredProperties;
	}
	
	public int getId()
	{
		return this.configuredProperties.getId();
	}
}
