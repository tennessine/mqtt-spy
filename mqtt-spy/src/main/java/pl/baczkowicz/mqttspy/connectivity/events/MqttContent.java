package pl.baczkowicz.mqttspy.connectivity.events;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;

public class MqttContent
{
	private String topic;
	
	private MqttMessage message;

	private Date date;

	private MqttSubscription subscription;
	
	private final long id;
	
	public MqttContent(final long id, final String topic, final MqttMessage message)
	{
		this.id = id;
		this.topic = topic;
		this.message = message;
		this.setDate(new Date());
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

	public MqttSubscription getSubscription()
	{
		return subscription;
	}

	public void setSubscription(MqttSubscription subscription)
	{
		this.subscription = subscription;
	}

	public long getId()
	{
		return id;
	}
}
