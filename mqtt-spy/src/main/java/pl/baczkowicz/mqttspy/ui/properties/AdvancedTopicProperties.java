package pl.baczkowicz.mqttspy.ui.properties;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AdvancedTopicProperties extends BaseTopicProperty
{
	private SimpleIntegerProperty qos;
	private SimpleBooleanProperty show;	
	private SimpleStringProperty script;
			
	public AdvancedTopicProperties(final String topic, final String script, final int qos, final boolean show)
	{
		super(topic);

		this.script= new SimpleStringProperty(script);
		this.qos = new SimpleIntegerProperty(qos);
		this.show = new SimpleBooleanProperty(show);
	}
	
	public SimpleStringProperty scriptProperty()
	{
		return this.script;
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
