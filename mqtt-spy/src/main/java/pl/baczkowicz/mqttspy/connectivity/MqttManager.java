package pl.baczkowicz.mqttspy.connectivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javafx.application.Platform;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ReconnectionSettings;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttCallbackHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttConnectionResultHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttDisconnectionResultHandler;
import pl.baczkowicz.mqttspy.connectivity.handlers.MqttEventHandler;
import pl.baczkowicz.mqttspy.connectivity.reconnection.ReconnectionManager;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.connectivity.MqttConnectionAttemptFailureEvent;
import pl.baczkowicz.mqttspy.events.connectivity.MqttDisconnectionAttemptFailureEvent;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.exceptions.MqttSpyException;
import pl.baczkowicz.mqttspy.scripts.InteractiveScriptManager;
import pl.baczkowicz.mqttspy.scripts.ScriptTypeEnum;
import pl.baczkowicz.mqttspy.ui.properties.RuntimeConnectionProperties;
import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

public class MqttManager
{
	public final static int CONNECT_DELAY = 500;

	private final static Logger logger = LoggerFactory.getLogger(MqttManager.class);

	private Map<Integer, MqttAsyncConnection> connections = new HashMap<Integer, MqttAsyncConnection>();
	
	private EventManager eventManager;

	private ReconnectionManager reconnectionManager;
		
	public MqttManager(final EventManager eventManager)
	{
		this.eventManager = eventManager;
		this.reconnectionManager = new ReconnectionManager();
		new Thread(reconnectionManager).start();
	}
	
	public MqttAsyncConnection createConnection(final RuntimeConnectionProperties connectionProperties, final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		// Storing all client properties in a simple object
		final MqttAsyncConnection connection = new MqttAsyncConnection(
				connectionProperties, MqttConnectionStatus.DISCONNECTED, eventManager, uiEventQueue);
		
		final InteractiveScriptManager scriptManager = new InteractiveScriptManager(eventManager, connection);
		connection.setScriptManager(scriptManager);
		
		// TODO: no UI to populate that at the moment
		scriptManager.populateScripts(connectionProperties.getConfiguredProperties().getBackgroundScript(), ScriptTypeEnum.BACKGROUND);	

		// Store the created connection
		connections.put(connectionProperties.getConfiguredProperties().getId(), connection);

		return connection;
	}

	public MqttAsyncConnection connectToBroker(final MqttAsyncConnection connection)
	{
		try
		{
			connection.createClient(new MqttCallbackHandler(connection));
			
			final Runnable connectionRunnable = new Runnable()
			{				
				@Override
				public void run()
				{
					connection.setConnectionStatus(MqttConnectionStatus.CONNECTING);
					
					try
					{
						// TODO: move this away
						if (ThreadingUtils.sleep(CONNECT_DELAY))							
						{
							return;
						}

						// Asynch connect
						logger.info("Connecting client ID [{}] to server [{}]; options = {}",
								connection.getProperties().getClientID(), 
								connection.getProperties().getServerURI(), 
								connection.getProperties().getOptions().toString());
						
						connection.connect(connection.getProperties().getOptions(), connection, new MqttConnectionResultHandler());
						
						// TODO: resubscribe when connection regained
					}
					catch (MqttSpyException e)
					{
						Platform.runLater(new MqttEventHandler(new MqttConnectionAttemptFailureEvent(connection, e)));
						logger.error("Cannot connect to " + connection.getProperties().getServerURI(), e);
					}				
				}
			};
			
			final ReconnectionSettings reconnectionSettings = connection.getMqttConnectionDetails().getReconnectionSettings();
			
			if (reconnectionSettings == null)
			{
				new Thread(connectionRunnable).start();
			}
			else
			{			
				reconnectionManager.addConnection(connection, connectionRunnable);
			}

			return connection;
		}
		catch (IllegalArgumentException | MqttSpyException e)
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
		
		reconnectionManager.removeConnection(connection);

		// TODO: check if connected?

		connection.setConnectionStatus(MqttConnectionStatus.DISCONNECTING);
		connection.unsubscribeAll(true);

		try
		{			
			logger.info("Disconnecting " + connection.getProperties().getClientID() + " from "
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
