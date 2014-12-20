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
package pl.baczkowicz.mqttspy.ui.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionStatus;
import pl.baczkowicz.mqttspy.connectivity.MqttManager;
import pl.baczkowicz.mqttspy.ui.connections.ConnectionManager;

/**
 * Utilities for creating user actions.
 */
public class ActionUtils
{

	/**
	 * Creates an event handler with a disconnection action.
	 * 
	 * @param mqttManager Used for disconnecting
	 * @param connectionId The ID of the connection to disconnect
	 * 
	 * @return The EventHandler with the action
	 */
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
	
	/**
	 * Creates an event handler with an empty action.
	 * 
	 * @return The EventHandler with the action
	 */
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
	
	/**
	 * Creates an event handler with a disconnection and close action.
	 * 
	 * @param connectionId The ID of the connection
	 * @param connectionManager The connection manager used
	 * 
	 * @return The EventHandler with the action
	 */
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
	
	/**
	 * Creates an event handler with a 'connect to broker' action.
	 * 
	 * @param mqttManager The MQTT manager to be used to connect
	 * @param connectionId The ID of the connection to be used
	 * 
	 * @return The EventHandler with the action
	 */
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

	/**
	 * Creates an event handler with a next allowable action for the given connection.
	 * 
	 * @param state The next state
	 * @param connectionId The ID of the connection
	 * @param mqttManager The MQTT manager to be used
	 * 
	 * @return The EventHandler with the action
	 */
	public static EventHandler<ActionEvent> createNextAction(final MqttConnectionStatus state, final int connectionId, final MqttManager mqttManager)
	{
		if (state == null)
		{
			return createEmptyAction();
		}
		
		switch (state)
		{
			case CONNECTED:				
				return createDisconnectAction(mqttManager, connectionId);
			case CONNECTING:
				return createEmptyAction();
			case DISCONNECTED:
				return createConnectAction(mqttManager, connectionId);
			case DISCONNECTING:
				return createEmptyAction();
			case NOT_CONNECTED:
				return createConnectAction(mqttManager, connectionId);
			default:
				return createEmptyAction();
		}		
	}	
}
