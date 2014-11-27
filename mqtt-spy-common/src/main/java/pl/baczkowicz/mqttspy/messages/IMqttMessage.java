package pl.baczkowicz.mqttspy.messages;

public interface IMqttMessage
{
	String getTopic();
	
	String getPayload();
	
	void setPayload(final String payload);
	
	int getQoS();
	
	boolean isRetained();
}
