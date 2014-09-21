package pl.baczkowicz.mqttspy.connectivity;


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
