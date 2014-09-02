package pl.baczkowicz.mqttspy.connectivity.messagestore;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public class SubscriptionTopicSummary extends ObservableMqttContent
{
	final static Logger logger = LoggerFactory.getLogger(SubscriptionTopicSummary.class);
	
	private BooleanProperty show;
	
	private IntegerProperty count;

	public SubscriptionTopicSummary(final Boolean show, final Integer count, final MqttContent message, final FormatterDetails format)
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
