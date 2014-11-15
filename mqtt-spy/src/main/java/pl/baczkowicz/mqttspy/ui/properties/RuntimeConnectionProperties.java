package pl.baczkowicz.mqttspy.ui.properties;

import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionDetailsWithOptions;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;

public class RuntimeConnectionProperties extends MqttConnectionDetailsWithOptions
{
	private ConfiguredConnectionDetails configuredProperties;

	public RuntimeConnectionProperties(final ConfiguredConnectionDetails configuredProperties) throws ConfigurationException
	{	
		super(configuredProperties);
//		super(	new MqttConnectionDetails(
//				configuredProperties.getName(), 
//				configuredProperties.getServerURI(), 
//				configuredProperties.getClientID(), 
//				configuredProperties.getUserCredentials(),
//				configuredProperties.getLastWillAndTestament(),
//				configuredProperties.isCleanSession(), 
//				configuredProperties.getConnectionTimeout(), 
//				configuredProperties.getKeepAliveInterval()));
		
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
