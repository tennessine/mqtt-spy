package pl.baczkowicz.mqttspy.connectivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javafx.application.Platform;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.handlers.MqttCallbackHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttConnectionResultHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttDisconnectionResultHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttEventHandler;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.connectivity.MqttConnectionAttemptFailureEvent;
import pl.baczkowicz.mqttspy.events.connectivity.MqttDisconnectionAttemptFailureEvent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;

public class MqttManager
{
	public final static String TCP_PREFIX = "tcp://";
	public final static String SSL_PREFIX = "ssl://";
	public final static int CONNECT_DELAY = 500;

	private final static Logger logger = LoggerFactory.getLogger(MqttManager.class);

	private Map<Integer, MqttAsyncConnection> connections = new HashMap<Integer, MqttAsyncConnection>();
	
	private EventManager eventManager;
		
	public MqttManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	
	public MqttAsyncConnection createConnection(final RuntimeConnectionProperties connectionProperties, final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		// Storing all client properties in a simple object
		final MqttAsyncConnection connection = new MqttAsyncConnection(connectionProperties, MqttConnectionStatus.DISCONNECTED, eventManager, uiEventQueue);

		// Store the created connection
		connections.put(connectionProperties.getConfiguredProperties().getId(), connection);

		return connection;
	}

	public MqttAsyncConnection connectToBroker(final MqttAsyncConnection connection)
	{
		try
		{
			// Creating MQTT client instance
			final MqttAsyncClient client = new MqttAsyncClient(
					connection.getProperties().getServerURI(), 
					connection.getProperties().getClientId(),
					null);
			
			// Set MQTT callback
			client.setCallback(new MqttCallbackHandler(connection));

			connection.setClient(client);
			connection.setConnectionStatus(MqttConnectionStatus.CONNECTING);
			
			new Thread(new Runnable()
			{				
				@Override
				public void run()
				{
					try
					{
						Thread.sleep(CONNECT_DELAY);

						// Asynch connect
						logger.info("Connecting client ID [{}] to server [{}]; options = {}",
								connection.getProperties().getClientId(), 
								connection.getProperties().getServerURI(), 
								connection.getProperties().getOptions().toString());
						
						connection.getClient().connect(connection.getProperties().getOptions(), connection, new MqttConnectionResultHandler());
					}
					catch (MqttException | IllegalArgumentException e)
					{
						Platform.runLater(new MqttEventHandler(new MqttConnectionAttemptFailureEvent(connection, e)));
						logger.error("Cannot connect to " + connection.getProperties().getServerURI(), e);
					}
					catch (InterruptedException e)
					{
						return;
					}				
				}
			}).start();

			return connection;
		}
		catch (MqttException | IllegalArgumentException e)
		{
			Platform.runLater(new MqttEventHandler(new MqttConnectionAttemptFailureEvent(connection, e)));
			logger.error("Cannot connect to " + connection.getProperties().getServerURI(), e);
		}

		return null;
	}
	
	public MqttAsyncConnection connectToBroker(final int connectionId)
	{
		return connectToBroker(connections.get(connectionId));
	}

	public void disconnectFromBroker(final int connectionId)
	{
		final MqttAsyncConnection connection = connections.get(connectionId);

		// TODO: check if connected?

		connection.setConnectionStatus(MqttConnectionStatus.DISCONNECTING);
		connection.unsubscribeAll();

		try
		{			
			logger.info("Disconnecting " + connection.getProperties().getClientId() + " from "
					+ connection.getProperties().getServerURI());
			if (connection.getClient() != null && connection.getClient().isConnected())
			{
				connection.getClient().disconnect(connection, new MqttDisconnectionResultHandler());
			}
			else
			{
				logger.debug("Already disconnected");
			}
		}
		catch (MqttException e)
		{
			Platform.runLater(new MqttEventHandler(new MqttDisconnectionAttemptFailureEvent(connection, e)));
			logger.error("Cannot disconnect from connection {} {}", 
					connection.getId(), connection.getProperties().getName(), e);
		}		
	}

	public void disconnectAll()
	{
		for (final MqttAsyncConnection connection : connections.values())
		{
			disconnectFromBroker(connection.getId());
		}
	}
	
	public Collection<MqttAsyncConnection> getConnections()
	{
		return connections.values();
	}

	public void close(final int connectionId)
	{
		final MqttAsyncConnection connection = connections.get(connectionId);
		connection.setOpened(false);
	}
}
