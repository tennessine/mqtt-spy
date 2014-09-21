package pl.baczkowicz.mqttspy.ui.properties;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AdvancedTopicProperties extends BaseTopicProperty
{
	private SimpleIntegerProperty qos;
	private SimpleBooleanProperty show;

	public AdvancedTopicProperties(final String topic, final int qos, final boolean show)
	{
		super(topic);

		this.qos = new SimpleIntegerProperty(qos);
		this.show = new SimpleBooleanProperty(show);
	}
	
	public SimpleIntegerProperty qosProperty()
	{
		return this.qos;
	}
	
	public SimpleBooleanProperty showProperty()
	{
		return this.show;
	}
}
