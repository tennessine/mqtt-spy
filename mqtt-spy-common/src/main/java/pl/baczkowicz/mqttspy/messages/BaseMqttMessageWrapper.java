package pl.baczkowicz.mqttspy.messages;

import pl.baczkowicz.mqttspy.common.generated.BaseMqttMessage;

public class BaseMqttMessageWrapper implements IMqttMessage
{
	private final BaseMqttMessage message;

	public BaseMqttMessageWrapper(final BaseMqttMessage message)
	{
		this.message = message;
	}

	public String getTopic()
	{
		return message.getTopic();
	}

	public String getPayload()
	{
		return message.getValue();
	}

	public int getQoS()
	{
		return message.getQos();
	}

	public boolean isRetained()
	{
		return message.isRetained();
	}
}
