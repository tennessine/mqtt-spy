package pl.baczkowicz.mqttspy.connectivity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.Subscription;
import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.SubscriptionsStore;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage.QOSType;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import pl.baczkowicz.mqttspy.common.generated.MqttConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.topicmatching.MapBasedSubscriptionStore;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;

public abstract class BaseMqttConnection implements MqttConnectionInterface
{
	private SubscriptionsStore subscriptionsStore;
	
	protected MqttAsyncClient client;
	
	protected final MqttConnectOptions options;	

	private MqttConnectionStatus connectionStatus = MqttConnectionStatus.NOT_CONNECTED;
	
	public BaseMqttConnection(final MqttConnectionDetails connectionDetails)
	{
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
					Base64.decodeBase64(connectionDetails.getLastWillAndTestament().getValue()),
					connectionDetails.getLastWillAndTestament().getQos(),
					connectionDetails.getLastWillAndTestament().isRetained());
		}
		
		// Manage subscriptions, based on moquette
		subscriptionsStore = new SubscriptionsStore();
		subscriptionsStore.init(new MapBasedSubscriptionStore());
	}
	
	protected void connect(final MqttConnectOptions options) throws MqttSpyException
	{
		try
		{
			connectionStatus = MqttConnectionStatus.CONNECTING;
			client.connect(options).waitForCompletion();
			connectionStatus = MqttConnectionStatus.CONNECTED;
		}
		catch (MqttSecurityException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
		}
	}
	
	public void subscribe(final String topic, final int qos) throws MqttSpyException
	{
		try
		{
			client.subscribe(topic, qos);
			
			// Store the subscription topic for further matching
			subscriptionsStore.add(new Subscription(client.getClientId(), topic, QOSType.MOST_ONE, true));
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Subscription attempt failed", e);
		}
	}

	public boolean canPublish()
	{
		return client != null;
	}
	
	public MqttConnectionStatus getConnectionStatus()
	{
		return connectionStatus;
	}
	
	public void setConnectionStatus(final MqttConnectionStatus connectionStatus)
	{
		this.connectionStatus = connectionStatus;
	}
	
	public List<String> getMatchingSubscriptions(final ReceivedMqttMessage message)
	{		
		// Check matching subscription, based on moquette
		final List<Subscription> matchingSubscriptions = subscriptionsStore.matches(message.getTopic());
		
		final List<String> matchingSubscriptionTopics = new ArrayList<String>();
		
		// For all found subscriptions
		for (final Subscription matchingSubscription : matchingSubscriptions)
		{						
			matchingSubscriptionTopics.add(matchingSubscription.getTopic());
		}		

		return matchingSubscriptionTopics;
	}

	// public MqttConnectOptions getOptions()
	// {
	// return options;
	// }
}
