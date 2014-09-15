package pl.baczkowicz.mqttspy.ui.events;

public class MessageIndexChangedEvent extends UIEvent
{
	private final int index;

	// private final MqttContent message;

	public MessageIndexChangedEvent(final int value)
	{
		this.index = value;
		// this.message = message;
	}

	public int getIndex()
	{
		return this.index;
	}

	// public MqttContent getMessage()
	// {
	// return message;
	// }
}
