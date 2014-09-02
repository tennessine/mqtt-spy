package pl.baczkowicz.mqttspy.connectivity.messagestore;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.connectivity.MqttSubscription;
import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;
import pl.baczkowicz.mqttspy.ui.utils.FormattingUtils;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class ObservableMqttContent
{
	final static Logger logger = LoggerFactory.getLogger(ObservableMqttContent.class);
		
	private StringProperty topic;
	private StringProperty lastReceivedTimestamp;
	private StringProperty lastReceivedPayload;
	
	private MqttContent mqttContent;
	
	public ObservableMqttContent(final MqttContent message, final FormatterDetails format)
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
				
		this.lastReceivedTimestamp.set(Utils.SDF.format(mqttContent.getDate()));		
		this.lastReceivedPayload.set(FormattingUtils.convertText(format, new String(mqttContent.getMessage().getPayload())));
	}

	public long getId()
	{
		return mqttContent.getId();
	}
}
