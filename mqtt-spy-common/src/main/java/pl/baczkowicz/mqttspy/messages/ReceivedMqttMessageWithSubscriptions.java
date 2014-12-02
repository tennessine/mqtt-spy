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
package pl.baczkowicz.mqttspy.messages;

import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import pl.baczkowicz.mqttspy.connectivity.BaseMqttConnection;

public class ReceivedMqttMessageWithSubscriptions extends ReceivedMqttMessage
{
	private List<String> subscriptions;
	
	private final BaseMqttConnection connection;
	
	public ReceivedMqttMessageWithSubscriptions(final ReceivedMqttMessageWithSubscriptions message)
	{
		this(
				message.getId(), message.getTopic(), 
				copyMqttMessage(message.getMessage()), 
				message.getDate(), message.getConnection());
		
		this.setSubscriptions(message.getSubscriptions());
	}
	
	public ReceivedMqttMessageWithSubscriptions(final long id, final String topic, final MqttMessage message, final BaseMqttConnection connection)
	{
		super(id, topic, message);
		this.connection = connection;
	}
	
	public ReceivedMqttMessageWithSubscriptions(final long id, final String topic, final MqttMessage message, final Date date, final BaseMqttConnection connection)
	{
		super(id, topic, message, date);
		this.connection = connection;
	}

	public List<String> getSubscriptions()
	{
		return subscriptions;
	}

	public void setSubscriptions(final List<String> subscriptions)
	{
		this.subscriptions = subscriptions;
	}

	public BaseMqttConnection getConnection()
	{
		return connection;
	}
}
