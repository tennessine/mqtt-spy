package pl.baczkowicz.mqttspy.connectivity.messagestore;

import java.util.Queue;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

public interface MessageStore
{
	Queue<MqttContent> getMessages();

	boolean filtersEnabled();
}
