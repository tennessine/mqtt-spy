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

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceivedMqttMessage implements IMqttMessage
{
	private String topic;
	
	private MqttMessage message;

	private Date date;

	private final long id;
	
	public ReceivedMqttMessage(final long id, final String topic, final MqttMessage message)
	{
		this.id = id;
		this.topic = topic;
		this.message = message;
		this.setDate(new Date());
	}
	
	public ReceivedMqttMessage(final long id, final String topic, final MqttMessage message, final Date date)
	{
		this.id = id;
		this.topic = topic;
		this.message = message;
		this.setDate(date);
	}
	
	public static MqttMessage copyMqttMessage(final MqttMessage message)
	{
		final MqttMessage copy = new MqttMessage();
		
		copy.setPayload(message.getPayload());
		copy.setQos(message.getQos());
		copy.setRetained(message.isRetained());
		
		return copy;
	}

	public MqttMessage getMessage()
	{
		return message;
	}

	public void setMessage(MqttMessage message)
	{
		this.message = message;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public long getId()
	{
		return id;
	}
	
	// Convenience methods for accessing the message object
	
	public String getPayload()
	{
		return new String(this.message.getPayload());
	}
	
	public void setPayload(final String payload)
	{
		this.message.setPayload(payload.getBytes());
	}
	
	public int getQoS()
	{
		return this.message.getQos();
	}
	
	public boolean isRetained()
	{
		return this.message.isRetained();
	}
}
