package pl.baczkowicz.mqttspy.ui.properties;

import javafx.beans.property.SimpleStringProperty;

public class BaseTopicProperty
{
	private SimpleStringProperty topic;
	
	public BaseTopicProperty(final String topic)
	{
		this.topic = new SimpleStringProperty(topic);
	}
	
	public SimpleStringProperty topicProperty()
	{
		return this.topic;
	}
}
