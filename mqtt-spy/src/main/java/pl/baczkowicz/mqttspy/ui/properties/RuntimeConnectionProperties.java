package pl.baczkowicz.mqttspy.ui.properties;

import pl.baczkowicz.mqttspy.configuration.ConfigurationException;
import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.UserCredentials;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionProperties;

public class RuntimeConnectionProperties extends MqttConnectionProperties
{
	// private final FormatterDetails formatter;
	
	private ConfiguredConnectionDetails configuredProperties;

	public RuntimeConnectionProperties(final ConfiguredConnectionDetails configuredProperties, final UserCredentials userCredentials) throws ConfigurationException
	{
		super(	configuredProperties.getName(), 
				configuredProperties.getServerURI(), 
				configuredProperties.getClientID(), userCredentials, 
				configuredProperties.isCleanSession(), 
				configuredProperties.getConnectionTimeout(), 
				configuredProperties.getKeepAliveInterval());
		
		this.configuredProperties = configuredProperties;		
	}
	
	// public RuntimeConnectionProperties(final String name, final String
	// serverURI, final String clientId,
	// final UserCredentials userCredentials,
	// final Boolean cleanSession, final Integer connectionTimeout, final
	// Integer keepAliveInterval,
	// Boolean autoConnect, final FormatterDetails formatter, final int
	// maxMessagesStored) throws ConfigurationException
	// {
	// super(name, serverURI, clientId, userCredentials, cleanSession,
	// connectionTimeout, keepAliveInterval);
	//
	// this.formatter = formatter;
	// this.autoConnect = autoConnect;
	// this.maxMessagesStored = maxMessagesStored;
	// }

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
