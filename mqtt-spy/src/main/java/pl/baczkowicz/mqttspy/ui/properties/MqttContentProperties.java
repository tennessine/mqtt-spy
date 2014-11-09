package pl.baczkowicz.mqttspy.ui.properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.utils.Utils;

public class MqttContentProperties
{
	private StringProperty topic;
	
	private StringProperty lastReceivedTimestamp;
	
	private StringProperty lastReceivedPayload;

	private MqttContent mqttContent;

	public MqttContentProperties(final MqttContent message, final FormatterDetails format)
	{
		this.topic = new SimpleStringProperty(message.getTopic());
		this.lastReceivedTimestamp = new SimpleStringProperty();
		this.lastReceivedPayload = new SimpleStringProperty();
		setMessage(message, format);
	}

	public StringProperty topicProperty()
	{
		return topic;
	}

	public StringProperty lastReceivedTimestampProperty()
	{
		return lastReceivedTimestamp;
	}

	public StringProperty lastReceivedPayloadProperty()
	{
		return lastReceivedPayload;
	}

	public void setTopic(String topic)
	{
		this.topic.set(topic);
	}

	public MqttSubscription getSubscription()
	{
		return mqttContent.getSubscription();
	}

	public void setMessage(final MqttContent message, final FormatterDetails format)
	{
		this.mqttContent = message;

		this.lastReceivedTimestamp.set(Utils.DATE_WITH_MILLISECONDS_SDF.format(mqttContent
				.getDate()));
		this.lastReceivedPayload.set(mqttContent.getFormattedPayload(format));
	}

	public long getId()
	{
		return mqttContent.getId();
	}
}
