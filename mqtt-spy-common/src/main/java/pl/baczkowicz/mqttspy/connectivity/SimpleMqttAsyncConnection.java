package pl.baczkowicz.mqttspy.connectivity;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;

public class SimpleMqttAsyncConnection extends BaseMqttConnection
{
	final static Logger logger = LoggerFactory.getLogger(SimpleMqttAsyncConnection.class);
	
	public SimpleMqttAsyncConnection(final MqttConnectionDetails connectionDetails) throws ConfigurationException
	{
		super(new MqttConnectionDetailsWithOptions(connectionDetails));	
	}
	
	public boolean connect()
	{
		setConnectionStatus(MqttConnectionStatus.CONNECTING);				
		try
		{
			connectAndWait(connectionDetails.getOptions());
			logger.error("Successfully connected to {}", connectionDetails.getName());
			setConnectionStatus(MqttConnectionStatus.CONNECTED);
			return true;
		}
		catch (MqttSpyException e)
		{
			logger.error("Connection attempt failed", e);
			setConnectionStatus(MqttConnectionStatus.NOT_CONNECTED);
		}
		return false;
	}
	
	public void subscribe(final String topic, final int qos)
	{
		try
		{
			super.subscribe(topic, qos);
			logger.info("Successfully subscribed to " + topic);
		}
		catch (MqttSpyException e)
		{
			logger.error("Subscription attempt failed for topic {}", topic, e);
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
