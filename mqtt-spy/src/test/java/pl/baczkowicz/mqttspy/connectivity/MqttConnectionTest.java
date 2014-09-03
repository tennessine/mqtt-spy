package pl.baczkowicz.mqttspy.connectivity;

import java.util.Observable;
import java.util.Observer;

import javafx.scene.paint.Color;
import junit.framework.TestCase;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import pl.baczkowicz.mqttspy.configuration.ConfiguredConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.exceptions.ConfigurationException;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;

public class MqttConnectionTest extends TestCase
{
	private static final String name = "testClient";
	private static final String serverURI = "localhost";
	private static final String clientId = "testClientId";
	private static final String subscription_TOPIC = "/#";
	private static final String message_TOPIC = "/topic";
	private MqttAsyncClient mockClient;
	private Mockery context = new Mockery()
	{
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private Observer mockObserver;
	private EventManager mockEventManager;

	@Before
	public void setUp()
	{
		mockClient = context.mock(MqttAsyncClient.class);
		mockObserver = context.mock(Observer.class);
		mockEventManager = context.mock(EventManager.class);
	}

	@Test
	public void testUnsubscribeAndRemove() throws MqttException, ConfigurationException
	{
		// Set up connection
		final ConfiguredConnectionDetails configuredConnectionDetails = new ConfiguredConnectionDetails();
		configuredConnectionDetails.setName(name);
		configuredConnectionDetails.setServerURI(serverURI);
		configuredConnectionDetails.setClientID(clientId);
		configuredConnectionDetails.setCleanSession(false);
		configuredConnectionDetails.setConnectionTimeout(5);
		configuredConnectionDetails.setKeepAliveInterval(5);
		configuredConnectionDetails.setMaxMessagesStored(500);
		
		final RuntimeConnectionProperties connectionProperties = new RuntimeConnectionProperties(configuredConnectionDetails, null);
		context.checking(new Expectations()
		{
			{
				oneOf(mockEventManager).notifyConnectionStatusChanged(with(any(MqttConnection.class)));
			}
		});
		
		final MqttConnection connection = new MqttConnection(connectionProperties, MqttConnectionStatus.CONNECTING, mockEventManager);
		context.assertIsSatisfied();
		
		connection.setClient(mockClient);

		// This should add a subscription
		final MqttSubscription subscription = new MqttSubscription(subscription_TOPIC, 0, Color.WHITE, 100);

		context.checking(new Expectations()
		{
			{
				oneOf(mockClient).subscribe(subscription_TOPIC, 0);
				oneOf(mockClient).unsubscribe(subscription_TOPIC);
				
				allowing(mockClient).isConnected();
				will(returnValue(true));
				
				allowing(mockObserver).update(with(any(Observable.class)), with(subscription));
				oneOf(mockObserver).update(with(any(Observable.class)), with(any(MqttContent.class)));
			}
		});

		subscription.addObserver(mockObserver);
		assertTrue(connection.subscribe(subscription));

		// This should handle the message
		MqttContent message = new MqttContent(1, message_TOPIC, new MqttMessage("test".getBytes()));
		connection.messageReceived(message);

		// This should remove the subscription
		connection.unsubscribeAndRemove(subscription);

		// This should be ignored - no matching subscriptions
		connection.messageReceived(message);

		context.assertIsSatisfied();
	}

	@Test
	public void testUnsubscribe() throws MqttException, ConfigurationException
	{
		// Set up connection
		final ConfiguredConnectionDetails configuredConnectionDetails = new ConfiguredConnectionDetails();
		configuredConnectionDetails.setName(name);
		configuredConnectionDetails.setServerURI(serverURI);
		configuredConnectionDetails.setClientID(clientId);
		configuredConnectionDetails.setCleanSession(false);
		configuredConnectionDetails.setConnectionTimeout(5);
		configuredConnectionDetails.setKeepAliveInterval(5);
		configuredConnectionDetails.setMaxMessagesStored(200);
		
		final RuntimeConnectionProperties connectionProperties = new RuntimeConnectionProperties(configuredConnectionDetails, null);
		
		context.checking(new Expectations()
		{
			{
				oneOf(mockEventManager).notifyConnectionStatusChanged(with(any(MqttConnection.class)));
			}
		});
		
		final MqttConnection connection = new MqttConnection(connectionProperties, MqttConnectionStatus.CONNECTING, mockEventManager);
		context.assertIsSatisfied();
		
		connection.setClient(mockClient);

		// This should add a subscription
		final MqttSubscription subscription = new MqttSubscription(subscription_TOPIC, 0, Color.WHITE, 100);
		subscription.addObserver(mockObserver);
		
		context.checking(new Expectations()
		{
			{
				oneOf(mockClient).subscribe(subscription_TOPIC, 0);
				oneOf(mockClient).unsubscribe(subscription_TOPIC);
				
				allowing(mockClient).isConnected();
				will(returnValue(true));
				
				allowing(mockObserver).update(with(any(Observable.class)), with(subscription));
				oneOf(mockObserver).update(with(any(Observable.class)), with(any(MqttContent.class)));
			}
		});
	
		assertTrue(connection.subscribe(subscription));

		// This should handle the message
		MqttContent message = new MqttContent(1, message_TOPIC, new MqttMessage("test".getBytes()));
		connection.messageReceived(message);

		// This should remove the subscription
		connection.unsubscribe(subscription);

		// This should be ignored - subscription not active
		connection.messageReceived(message);
		context.assertIsSatisfied();
	}
}
