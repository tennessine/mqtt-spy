package pl.baczkowicz.mqttspy.connectivity;

import java.util.Queue;

import javafx.scene.paint.Color;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.events.ui.MqttSpyUIEvent;
import pl.baczkowicz.mqttspy.storage.ManagedMessageStoreWithFiltering;
import pl.baczkowicz.mqttspy.ui.SubscriptionController;

public class MqttSubscription extends ManagedMessageStoreWithFiltering
{
	private int id;
	
	private String topic;

	private Integer qos;

	private Color color;
	
	private Boolean active;

	private SubscriptionController subscriptionController;

	// private final EventManager eventManager;
	
	private MqttConnection connection;

	private boolean subscribing;

	public MqttSubscription(final String topic, final Integer qos, final Color color, 
			final int minMessagesPerTopic, final int preferredStoreSize, final Queue<MqttSpyUIEvent> uiEventQueue, final EventManager eventManager)
	{
		// Max size is double the preferred size
		super(topic, minMessagesPerTopic, preferredStoreSize, preferredStoreSize * 2, uiEventQueue, eventManager);
		
		this.topic = topic;
		this.qos = qos;
		this.color = color;
		this.active = false;
		
		// this.eventManager = eventManager;
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
		eventManager.notifySubscriptionStatusChanged(this);
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
