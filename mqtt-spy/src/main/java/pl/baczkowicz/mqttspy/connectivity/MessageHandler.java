package pl.baczkowicz.mqttspy.connectivity;

import pl.baczkowicz.mqttspy.connectivity.events.MqttContent;

/**
 * Interface for handling received messages.
 * 
 * @author Kamil Baczkowicz
 *
 */
public interface MessageHandler
{
	void messageReceived(final MqttContent message);
}
