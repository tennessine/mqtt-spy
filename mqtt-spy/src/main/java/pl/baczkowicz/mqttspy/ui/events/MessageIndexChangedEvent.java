package pl.baczkowicz.mqttspy.ui.events;

public class MessageIndexChangedEvent extends UIEvent
{
	private int index;

	public MessageIndexChangedEvent(final int value)
	{
		this.index = value;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}
