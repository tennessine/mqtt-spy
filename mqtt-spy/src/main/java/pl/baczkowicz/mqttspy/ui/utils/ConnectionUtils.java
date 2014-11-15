package pl.baczkowicz.mqttspy.ui.utils;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import pl.baczkowicz.mqttspy.configuration.generated.UserInterfaceMqttConnectionDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.connectivity.MqttUtils;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;

public class ConnectionUtils
{	
	public static final String SERVER_DELIMITER = ";";
	
	public static String composeConnectionName(final String cliendId, final List<String> serverURIs)
	{
		return cliendId + "@" + serverURIsToString(serverURIs);
	}
	
	public static String composeConnectionName(final String cliendId, final String serverURIs)
	{
		return cliendId + "@" + serverURIs;
	}
	
	public static String serverURIsToString(final List<String> serverURIs)
	{
		StringBuffer serverURIsAsString = new StringBuffer();
		boolean first = true;
		for (final String serverURI : serverURIs)
		{
			if (first)
			{
				serverURIsAsString.append(serverURI);
			}
			else
			{
				serverURIsAsString.append(ConnectionUtils.SERVER_DELIMITER + " " + serverURI);
			}
			
			first = false;
		}
		
		return serverURIsAsString.toString();
	}
	
	public static String validateConnectionDetails(final UserInterfaceMqttConnectionDetails connectionDetails, final boolean finalCheck)
	{
		if (connectionDetails.getServerURI() == null || connectionDetails.getServerURI().size() == 0)
		{
			return "Server URI cannot be empty";
		}
		
		boolean allEmpty = true;
		for (final String serverURI : connectionDetails.getServerURI())
		{
			if (!serverURI.trim().isEmpty())
			{
				allEmpty = false;
				break;
			}
		}		
		if (allEmpty)
		{
			return "Server URI cannot be empty";
		}
		
		if (connectionDetails.getClientID() == null
				|| connectionDetails.getClientID().trim().isEmpty() 
				|| connectionDetails.getClientID().length() > MqttUtils.MAX_CLIENT_LENGTH)
		{
			return "Client ID cannot be empty or longer than " + MqttUtils.MAX_CLIENT_LENGTH;
		}
		
		if (connectionDetails.getUserAuthentication() != null && connectionDetails.getUserCredentials() != null)
		{
			if ((finalCheck || !connectionDetails.getUserAuthentication().isAskForUsername()) && (connectionDetails.getUserCredentials().getUsername() == null
					|| connectionDetails.getUserCredentials().getUsername().trim().isEmpty()))
			{
				return "With user authentication enabled, user name cannot be empty";
			}
		}
		
		if (connectionDetails.getLastWillAndTestament() != null)
		{
			if (connectionDetails.getLastWillAndTestament().getTopic() == null || connectionDetails.getLastWillAndTestament().getTopic().isEmpty())				
			{
				return "With last will and testament enabled, publication topic cannot be empty";
			}
		}
		
		if (connectionDetails.getConnectionTimeout() < 0)
		{
			return "Connection timeout cannot be less than 0";
		}
		
		if (connectionDetails.getKeepAliveInterval() < 0)
		{
			return "Keep alive interval cannot be less than 0";
		}		
		
		return null;
	}
	
	public static EventHandler<ActionEvent> createDisconnectAction(final MqttManager mqttManager, final int connectionId)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				ConnectionManager.disconnect(mqttManager, connectionId);
				event.consume();
			}
		};
	}
	
	public static EventHandler<ActionEvent> createEmptyAction()
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{				
				event.consume();
			}
		};
	}
	
	public static EventHandler<ActionEvent> createDisconnectAndCloseAction(final int connectionId, final ConnectionManager connectionManager)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				connectionManager.disconnectAndCloseTab(connectionId);
				event.consume();
			}
		};
	}
	
	public static EventHandler<ActionEvent> createConnectAction(final MqttManager mqttManager, final int connectionId)
	{
		return new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				mqttManager.connectToBroker(connectionId);
				event.consume();
			}
		};
	}

	public static EventHandler<ActionEvent> createNextAction(final MqttConnectionStatus state, final int connectionId, final MqttManager mqttManager)
	{
		if (state == null)
		{
			return createEmptyAction();
		}
		
		switch (state)
		{
			case CONNECTED:				
				return ConnectionUtils.createDisconnectAction(mqttManager, connectionId);
			case CONNECTING:
				return ConnectionUtils.createEmptyAction();
			case DISCONNECTED:
				return ConnectionUtils.createConnectAction(mqttManager, connectionId);
			case DISCONNECTING:
				return ConnectionUtils.createEmptyAction();
			case NOT_CONNECTED:
				return ConnectionUtils.createConnectAction(mqttManager, connectionId);
			default:
				return ConnectionUtils.createEmptyAction();
		}		
	}		
}
