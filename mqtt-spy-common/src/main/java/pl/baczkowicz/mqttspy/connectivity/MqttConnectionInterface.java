package pl.baczkowicz.mqttspy.connectivity;

public interface MqttConnectionInterface
{
	boolean canPublish();
	
	boolean publish(final String publicationTopic, final String data, final int qos, final boolean retained);
}
