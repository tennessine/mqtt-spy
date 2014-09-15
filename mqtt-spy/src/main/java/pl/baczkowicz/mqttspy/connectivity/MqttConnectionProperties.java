package pl.baczkowicz.mqttspy.connectivity;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import pl.baczkowicz.mqttspy.configuration.generated.ConfiguredMessage;
import pl.baczkowicz.mqttspy.configuration.generated.UserCredentials;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;

public class MqttConnectionProperties
{
	private String name;

	private String serverURI;

	private String clientId;

	private MqttConnectOptions options;

	public MqttConnectionProperties(final String name, final String serverURI, final String clientId, 
			final UserCredentials userCredentials,
			final ConfiguredMessage lwt,
			final Boolean cleanSession, final Integer connectionTimeout,
			final Integer keepAliveInterval) throws ConfigurationException
	{
		this.name = name;
		 
		this.serverURI = MqttUtils.getServerURI(serverURI);
		this.clientId = clientId;
		
		this.options = new MqttConnectOptions();
		try
		{			
			if (userCredentials != null)
			{
				this.options.setUserName(userCredentials.getUsername());
				this.options.setPassword(userCredentials.getPassword().toCharArray());
			}
			if (lwt != null)
			{
				this.options.setWill(lwt.getTopic(), lwt.getPayload().getBytes(), lwt.getQoS(), lwt.isRetained());
			}
			this.options.setCleanSession(cleanSession);
			this.options.setConnectionTimeout(connectionTimeout);
			this.options.setKeepAliveInterval(keepAliveInterval);
		}
		catch (IllegalArgumentException e)
		{
			throw new ConfigurationException("Invalid parameters", e);
		}
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getServerURI()
	{
		return serverURI;
	}

	public void setServerURI(String serverURI)
	{
		this.serverURI = serverURI;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public MqttConnectOptions getOptions()
	{
		return options;
	}
}
