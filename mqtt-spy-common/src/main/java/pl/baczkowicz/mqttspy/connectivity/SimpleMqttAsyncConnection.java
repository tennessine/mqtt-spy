package pl.baczkowicz.mqttspy.connectivity;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;

public class SimpleMqttAsyncConnection extends BaseMqttConnection
{
	final static Logger logger = LoggerFactory.getLogger(SimpleMqttAsyncConnection.class);
	
	private final MqttConnectionDetails connectionDetails;
	
	private final MqttConnectOptions options;	

	public SimpleMqttAsyncConnection(final MqttConnectionDetails connectionDetails)
	{
		super();
		
		this.connectionDetails = connectionDetails;
			
		options = new MqttConnectOptions();
		
		if (connectionDetails.getServerURI().size() > 1)
		{
			options.setServerURIs((String[]) connectionDetails.getServerURI().toArray());
		}
		
		options.setCleanSession(connectionDetails.isCleanSession());
		options.setConnectionTimeout(connectionDetails.getConnectionTimeout());
		options.setKeepAliveInterval(connectionDetails.getKeepAliveInterval());
		
		if (connectionDetails.getUserCredentials() != null)
		{
			options.setUserName(connectionDetails.getUserCredentials().getUsername());
			options.setPassword(connectionDetails.getUserCredentials().getPassword().toCharArray());
		}
		
		if (connectionDetails.getLastWillAndTestament() != null)
		{
			options.setWill(connectionDetails.getLastWillAndTestament().getTopic(), 
					Base64.decodeBase64(connectionDetails.getLastWillAndTestament().getPayload()),
					connectionDetails.getLastWillAndTestament().getQos(),
					connectionDetails.getLastWillAndTestament().isRetained());
		}
	}
	
	public void connect() throws MqttSpyException
	{
		connect(options);
	}
	
	public void createClient(final MqttCallback callback) throws MqttSpyException
	{
		try
		{
			// Creating MQTT client instance
			client = new MqttAsyncClient(
					connectionDetails.getServerURI().get(0), 
					connectionDetails.getClientID(),
					null);
			
			// Set MQTT callback
			client.setCallback(callback);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Cannot instantiate client", e);
		}
	}
	
	public boolean publish(final String publicationTopic, final String data, final int qos, final boolean retained)
	{
		if (canPublish())
		{
			try
			{
				logger.info("Publishing message on topic \"" + publicationTopic + "\". Payload = \"" + data + "\"");
				client.publish(publicationTopic, data.getBytes(), qos, retained);
				
				logger.trace("Published message on topic \"" + publicationTopic + "\". Payload = \"" + data + "\"");
				
				return true;
			}
			catch (MqttException e)
			{
				logger.error("Cannot publish message on " + publicationTopic, e);
			}
		}
		else
		{
			logger.warn("No connection established yet...");
		}
		
		return false;
	}
}
