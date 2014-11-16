package pl.baczkowicz.mqttspy.connectivity;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.utils.ConfigurationUtils;
import pl.baczkowicz.mqttspy.utils.ConversionUtils;

public class MqttConnectionDetailsWithOptions extends MqttConnectionDetails
{
	private MqttConnectOptions options;

	public MqttConnectionDetailsWithOptions(final MqttConnectionDetails details) throws ConfigurationException
	{
		this.setName(details.getName());
		this.setClientID(details.getClientID());
		this.getServerURI().addAll(details.getServerURI());
		
		this.setConnectionTimeout(details.getConnectionTimeout());
		this.setKeepAliveInterval(details.getKeepAliveInterval());
		this.setCleanSession(details.isCleanSession());
		
		this.setLastWillAndTestament(details.getLastWillAndTestament());
		this.setUserCredentials(details.getUserCredentials());
		this.setReconnectionSettings(details.getReconnectionSettings());
		
		ConfigurationUtils.populateServerURIs(this);
		ConfigurationUtils.populateConnectionDefaults(this);
		
		// Populate MQTT options
		options = new MqttConnectOptions();
		
		try
		{
			if (getServerURI().size() > 1)
			{
				options.setServerURIs(getServerURI().toArray(new String[getServerURI().size()]));
			}
			
			options.setCleanSession(isCleanSession());
			options.setConnectionTimeout(getConnectionTimeout());
			options.setKeepAliveInterval(getKeepAliveInterval());
			
			if (getUserCredentials() != null)
			{
				options.setUserName(getUserCredentials().getUsername());
				options.setPassword(ConversionUtils.base64ToString(getUserCredentials().getPassword()).toCharArray());
			}
			
			if (getLastWillAndTestament() != null)
			{
				options.setWill(getLastWillAndTestament().getTopic(), 
						Base64.decodeBase64(getLastWillAndTestament().getValue()),
						getLastWillAndTestament().getQos(),
						getLastWillAndTestament().isRetained());
			}
		}
		catch (IllegalArgumentException e)
		{
			throw new ConfigurationException("Invalid parameters", e);
		}
	}
	
	public MqttConnectOptions getOptions()
	{
		return options;
	}
}
