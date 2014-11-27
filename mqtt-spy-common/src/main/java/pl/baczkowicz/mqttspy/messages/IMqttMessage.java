package pl.baczkowicz.mqttspy.messages;

public interface IMqttMessage
{
	String getTopic();
	
	String getPayload();
	
	int getQoS();
	
	boolean isRetained();
}
