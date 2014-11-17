package pl.baczkowicz.mqttspy.messages;

import java.util.Date;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceivedMqttMessageWithSubscriptions extends ReceivedMqttMessage
{
	private List<String> subscriptions;
	
	public ReceivedMqttMessageWithSubscriptions(final long id, final String topic, final MqttMessage message)
	{
		super(id, topic, message);
	}
	
	public ReceivedMqttMessageWithSubscriptions(final long id, final String topic, final MqttMessage message, final Date date)
	{
		super(id, topic, message, date);
	}

	public List<String> getSubscriptions()
	{
		return subscriptions;
	}

	public void setSubscriptions(final List<String> subscriptions)
	{
		this.subscriptions = subscriptions;
	}
}
