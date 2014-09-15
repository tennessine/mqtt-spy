package pl.baczkowicz.mqttspy.connectivity.events;

import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;

public class MqttContent
{
	private String topic;
	
	private MqttMessage message;

	private Date date;

	private MqttSubscription subscription;
	
	private final long id;
	
	private FormatterDetails lastUsedFormatter;
	
	private String formattedPayload;
	
	public MqttContent(final long id, final String topic, final MqttMessage message)
	{
		this.id = id;
		this.topic = topic;
		this.message = message;
		this.setDate(new Date());
		this.formattedPayload = new String(message.getPayload());
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

	public void format(final FormatterDetails formatter)
	{
		if (formatter == null)
		{
			formattedPayload = new String(message.getPayload());
		}		
		else if (!formatter.equals(lastUsedFormatter))
		{
			lastUsedFormatter = formatter;
			formattedPayload = FormattingUtils.convertText(formatter, new String(message.getPayload()));
		}
		
	}
	
	public String getFormattedPayload(final FormatterDetails formatter)
	{
		format(formatter);
		
		return formattedPayload;
	}
	
	public String getFormattedPayload()
	{
		return formattedPayload;
	}
}
