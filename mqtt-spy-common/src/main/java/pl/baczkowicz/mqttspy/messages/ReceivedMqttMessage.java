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
	
	public int getQoS()
	{
		return this.message.getQos();
	}
	
	public boolean isRetained()
	{
		return this.message.isRetained();
	}
}
