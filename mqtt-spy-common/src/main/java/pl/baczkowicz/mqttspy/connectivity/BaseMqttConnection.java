/***********************************************************************************
 * 
 * Copyright (c) 2014 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.mqttspy.connectivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.Subscription;
import org.dna.mqtt.moquette.messaging.spi.impl.subscriptions.SubscriptionsStore;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage.QOSType;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.topicmatching.MapBasedSubscriptionStore;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;
import pl.baczkowicz.mqttspy.utils.TimeUtils;

public abstract class BaseMqttConnection implements IMqttConnection
{
	final static Logger logger = LoggerFactory.getLogger(BaseMqttConnection.class);
	
	public static final long NEVER_STARTED = 0;

	private int connectionAttempts = 0;

	private long lastConnectionAttemptTimestamp = NEVER_STARTED;
	
	private Date lastSuccessfulConnectionAttempt;
	
	private SubscriptionsStore subscriptionsStore;
	
	protected MqttAsyncClient client;	

	protected final MqttConnectionDetailsWithOptions connectionDetails;	

	private MqttConnectionStatus connectionStatus = MqttConnectionStatus.NOT_CONNECTED;

	private String disconnectionReason;
	
	public BaseMqttConnection(final MqttConnectionDetailsWithOptions connectionDetails)
	{
		this.connectionDetails = connectionDetails;
		
		// Manage subscriptions, based on moquette
		subscriptionsStore = new SubscriptionsStore();
		subscriptionsStore.init(new MapBasedSubscriptionStore());
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
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Cannot instantiate the MQTT client", e);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Cannot instantiate the MQTT client", e);
		}
	}
	
	public void connect(final MqttConnectOptions options, final Object userContext, final IMqttActionListener callback) throws MqttSpyException
	{
		recordConnectionAttempt();
		
		try
		{
			client.connect(options, userContext, callback);
		}
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
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
	
	public void connectAndWait(final MqttConnectOptions options) throws MqttSpyException
	{
		recordConnectionAttempt();
		
		try
		{
			client.connect(options).waitForCompletion();
		}
		catch (IllegalArgumentException e)
		{
			throw new MqttSpyException("Connection attempt failed", e);
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
	
	protected void recordConnectionAttempt()
	{
		lastConnectionAttemptTimestamp = TimeUtils.getMonotonicTime();
		connectionAttempts++;
		
	}
	
	public void recordSuccessfulConnection()
	{
		lastSuccessfulConnectionAttempt = new Date();
	}
	
	public String getLastSuccessfulyConnectionAttempt()
	{
		return TimeUtils.DATE_WITH_SECONDS_SDF.format(lastSuccessfulConnectionAttempt);
	}
	
	public void subscribe(final String topic, final int qos) throws MqttSpyException
	{
		if (client == null || !client.isConnected())
		{
			logger.warn("Client not connected");
			return;
		}
		
		try
		{
			client.subscribe(topic, qos);
			
			addSubscriptionToStore(topic);
		}
		catch (MqttException e)
		{
			throw new MqttSpyException("Subscription attempt failed", e);
		}
	}
	
	protected void addSubscriptionToStore(final String topic)
	{
		// Store the subscription topic for further matching
		subscriptionsStore.add(new Subscription(connectionDetails.getClientID(), topic, QOSType.MOST_ONE, true));
	}
	
	protected void removeSubscriptionFromStore(final String topic)
	{
		subscriptionsStore.removeSubscription(topic, client.getClientId());
	}

	public boolean canPublish()
	{
		return client != null && client.isConnected();
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
	
	public void connectionLost(Throwable cause)
	{
		setDisconnectionReason(cause.getMessage());
		setConnectionStatus(MqttConnectionStatus.DISCONNECTED);
	}
	
	public void setDisconnectionReason(final String message)
	{
		this.disconnectionReason = message;
		if (!message.isEmpty())
		{
			this.disconnectionReason = this.disconnectionReason + " ("
					+ TimeUtils.DATE_WITH_SECONDS_SDF.format(new Date()) + ")";
		}
	}
	
	public String getDisconnectionReason()
	{
		return disconnectionReason;
	}
	
	public MqttConnectionDetailsWithOptions getMqttConnectionDetails()
	{
		return connectionDetails;
	}

	public long getLastConnectionAttemptTimestamp()
	{
		return lastConnectionAttemptTimestamp;
	}

	public int getConnectionAttempts()
	{
		return connectionAttempts;
	}
	

	public void disconnect()
	{
		try
		{
			client.disconnect(0);
			logger.info("Client {} disconnected", client.getClientId());
		}
		catch (MqttException e)
		{
			logger.error("Cannot disconnect", e);
		}
	}
}
