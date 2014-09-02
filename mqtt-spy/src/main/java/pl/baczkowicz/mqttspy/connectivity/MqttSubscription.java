package pl.baczkowicz.mqttspy.connectivity;

import javafx.scene.paint.Color;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;

public class MqttSubscription extends ObservableMessageStoreWithFiltering
{
	private String topic;

	private Integer qos;

	private Color color;
	
	private Boolean active;

	public MqttSubscription(final String topic, final Integer qos, final Color color, final int maxMessageStoreSize)
	{
		super(topic, maxMessageStoreSize);
		
		this.topic = topic;
		this.qos = qos;
		this.color = color;
		this.active = false;			
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public Integer getQos()
	{
		return qos;
	}

	public void setQos(Integer qos)
	{
		this.qos = qos;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public Boolean isActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
		subscriptionStatusChanged();
	}

	public void subscriptionStatusChanged()
	{
		// Notifies the observers
		this.setChanged();
		this.notifyObservers(this);
	}
}
