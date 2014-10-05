package pl.baczkowicz.mqttspy.storage;

import java.util.List;

import pl.baczkowicz.mqttspy.connectivity.MqttContent;

public interface MessageStore
{
	List<MqttContent> getMessages();
	
	// MessageListWithObservableTopicSummary getMessageList();

	boolean filtersEnabled();
}
