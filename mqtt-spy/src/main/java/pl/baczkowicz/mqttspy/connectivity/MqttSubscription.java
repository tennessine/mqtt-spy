package pl.baczkowicz.mqttspy.connectivity;

import java.util.Queue;

import javafx.scene.paint.Color;
import pl.baczkowicz.mqttspy.connectivity.messagestore.ObservableMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;

public class MqttSubscription extends ObservableMessageStoreWithFiltering
{
	private int id;
	
	private String topic;

	private Integer qos;

	private Color color;
	
	private Boolean active;

	private SubscriptionController subscriptionController;

	private MqttConnection connection;

	private boolean subscribing;

	public MqttSubscription(final String topic, final Integer qos, final Color color, final int maxMessageStoreSize, final Queue<MqttSpyUIEvent> uiEventQueue)
	{
		super(topic, maxMessageStoreSize, uiEventQueue);
		
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

	public void setSubscriptionController(final SubscriptionController subscriptionController)
	{
		this.subscriptionController = subscriptionController;		
	}
	
	public SubscriptionController getSubscriptionController()
	{
		return subscriptionController;
	}

	public void setConnection(MqttConnection connection)
	{
		this.connection = connection;		
	}
	
	public MqttConnection getConnection()	
	{
		return connection;
	}

	public int getId()
	{
		return id;
	}

	public void setId(final int id)
	{
		this.id = id;		
	}

	public boolean isSubscribing()
	{
		return subscribing;
	}
	
	public void setSubscribing(final boolean value)
	{
		subscribing = value;
	}
}
