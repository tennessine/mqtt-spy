package pl.baczkowicz.mqttspy.connectivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Tab;

import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.Subscription;
import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.SubscriptionsStore;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage.QOSType;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.connectivity.topicmatching.MapBasedSubscriptionStore;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;

public class MqttConnection extends ObservableMessageStoreWithFiltering
{
	final static Logger logger = LoggerFactory.getLogger(MqttConnection.class);

	private MqttConnectionStatus connectionStatus;

	private final Map<String, MqttSubscription> subscriptions = new HashMap<String, MqttSubscription>();

	private SubscriptionsStore subscriptionsStore;

	private final RuntimeConnectionProperties properties;

	private MqttAsyncClient client;
	
	private boolean isOpened;

	/** Maximum number of messages to store for this connection in each message store. */
	private int maxMessageStoreSize;

	private EventManager eventManager;

	private Tab connectionTab;

	public MqttConnection(final RuntimeConnectionProperties properties, 
			final MqttConnectionStatus status, final EventManager eventManager)
	{
		super(properties.getName(), properties.getMaxMessagesStored());
		this.setMaxMessageStoreSize(properties.getMaxMessagesStored());
		this.properties = properties;
		this.eventManager = eventManager;
		setConnectionStatus(status);

		// Manage subscriptions, based on moquette
		subscriptionsStore = new SubscriptionsStore();
		subscriptionsStore.init(new MapBasedSubscriptionStore());
	}

	public void messageReceived(final MqttContent message)
	{		
		// Check matching subscription, based on moquette
		List<Subscription> matchingSubscriptions = subscriptionsStore.matches(message.getTopic());
		for (final Subscription matchingSubscription : matchingSubscriptions)
		{
			final MqttSubscription mqttSubscription = subscriptions.get(matchingSubscription.getTopic());

			if (mqttSubscription != null && mqttSubscription.isActive())
			{
				message.setSubscription(mqttSubscription);
				mqttSubscription.messageReceived(message);
			}
		}		

		super.messageReceived(message);
	}
	
	public void publish(final String publicationTopic, final String data, final int qos)
	{
		try
		{
			logger.info("Publishing message on topic \"" + publicationTopic + "\". Payload = \"" + data + "\"");
			getClient().publish(publicationTopic, data.getBytes(), qos, false);
			
			logger.trace("Published message on topic \"" + publicationTopic + "\". Payload = \"" + data + "\"");
		}
		catch (MqttException e)
		{
			logger.error("Cannot publish message on " + publicationTopic, e);
		}
	}

	public void connectionLost(Throwable cause)
	{
		setConnectionStatus(MqttConnectionStatus.DISCONNECTED);
		unsubscribeAll();
	}

	public void addSubscription(final MqttSubscription subscription)
	{
		// Add it to the store if it hasn't been created before
		if (subscriptions.put(subscription.getTopic(), subscription) == null)
		{
			subscriptionsStore.add(new Subscription(properties.getClientId(), subscription
					.getTopic(), QOSType.MOST_ONE, true));
		}
	}

	public boolean resubscribeAll()
	{
		for (final MqttSubscription subscription : subscriptions.values())
		{
			resubscribe(subscription);
		}

		return true;
	}

	public boolean resubscribe(final MqttSubscription subscription)
	{
		return subscribe(subscription);
	}

	public boolean subscribe(final MqttSubscription subscription)
	{
		// If already active, simply ignore
		if (subscription.isActive())
		{
			return false;
		}

		if (!getClient().isConnected())
		{
			logger.info("Client not connected");
			return false;
		}

		try
		{
			logger.debug("Subscribing to " + subscription.getTopic());
			getClient().subscribe(subscription.getTopic(), subscription.getQos());

			addSubscription(subscription);
			logger.info("Subscribed to " + subscription.getTopic());

			subscription.setActive(true);
			logger.trace("Subscription " + subscription.getTopic() + " is active = "
					+ subscription.isActive());

			return true;
		}
		catch (MqttException e)
		{
			logger.error("Cannot subscribe to " + subscription.getTopic(), e);
			return false;
		}
	}

	public boolean unsubscribeAll()
	{
		for (final MqttSubscription subscription : subscriptions.values())
		{
			unsubscribe(subscription);
		}

		return true;
	}

	public boolean unsubscribe(final MqttSubscription subscription)
	{
		// If already unsubscribed, ignore
		if (!subscription.isActive())
		{
			return false;
		}

		logger.debug("Unsubscribing from " + subscription.getTopic());
		try
		{
			if (getClient().isConnected())
			{
				getClient().unsubscribe(subscription.getTopic());
			}
			logger.info("Unsubscribed from " + subscription.getTopic());
			return true;
		}
		catch (MqttException e)
		{
			logger.error("Cannot unsubscribe from " + subscription.getTopic(), e);

			return false;
		}
		finally
		{
			// As this is in 'finally', will be executed before the returns
			subscription.setActive(false);
			logger.trace("Subscription " + subscription.getTopic() + " is active = "
					+ subscription.isActive());

		}
	}

	public boolean unsubscribeAndRemove(final MqttSubscription subscription)
	{
		final boolean unsubscribed = unsubscribe(subscription);
		removeSubscription(subscription);
		logger.info("Subscription " + subscription.getTopic() + " removed");
		return unsubscribed;
	}

	public void removeSubscription(final MqttSubscription subscription)
	{
		subscriptions.remove(subscription.getTopic());
		subscriptionsStore.removeSubscription(subscription.getTopic(), getProperties()
				.getClientId());
	}

	public MqttConnectionStatus getConnectionStatus()
	{
		return connectionStatus;
	}

	public void setConnectionStatus(MqttConnectionStatus connectionStatus)
	{
		this.connectionStatus = connectionStatus;
		eventManager.notifyConnectionStatusChanged(this);

		// Notify observers
		this.setChanged();
		this.notifyObservers(connectionStatus);
	}

	public RuntimeConnectionProperties getProperties()
	{
		return properties;
	}

	public Map<String, MqttSubscription> getSubscriptions()
	{
		return subscriptions;
	}

	public MqttAsyncClient getClient()
	{
		return client;
	}

	public void setClient(MqttAsyncClient client)
	{
		this.client = client;
	}

	public int getMaxMessageStoreSize()
	{
		return maxMessageStoreSize;
	}

	public void setMaxMessageStoreSize(int maxMessageStoreSize)
	{
		this.maxMessageStoreSize = maxMessageStoreSize;
	}

	public void setTab(final Tab connectionTab)
	{
		this.connectionTab = connectionTab;		
	}
	
	public Tab getConnectionTab()
	{
		return this.connectionTab;
	}

	public boolean isOpened()
	{
		return isOpened;
	}

	public void setOpened(boolean isOpened)
	{
		this.isOpened = isOpened;
		
		eventManager.notifyConnectionStatusChanged(this);
	}
}
