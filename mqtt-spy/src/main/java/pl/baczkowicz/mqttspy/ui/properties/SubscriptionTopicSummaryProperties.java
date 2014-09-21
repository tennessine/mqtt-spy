package pl.baczkowicz.mqttspy.ui.properties;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttContent;

public class SubscriptionTopicSummaryProperties extends MqttContentProperties
{
	private BooleanProperty show;
	
	private IntegerProperty count;

	public SubscriptionTopicSummaryProperties(final Boolean show, final Integer count, final MqttContent message, final FormatterDetails format)
	{
		super(message, format);
		
		this.show = new SimpleBooleanProperty(show);	               
        this.count = new SimpleIntegerProperty(count);                    
	}

	public BooleanProperty showProperty()
	{
		return show;
	}

	public IntegerProperty countProperty()
	{
		return count;
	}

	public void setCount(Integer count)
	{
		this.count.set(count);
	}
}
