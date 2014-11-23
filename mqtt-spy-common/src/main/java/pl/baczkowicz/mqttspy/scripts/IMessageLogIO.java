package pl.baczkowicz.mqttspy.scripts;

import pl.baczkowicz.mqttspy.messages.ReceivedMqttMessage;

public interface IMessageLogIO
{
	int readFromFile(final String logLocation);
	
	int getMessageCount();
	
	void start();
	
//	void pause();
	
// 	void resume();
	
	void stop();
	
	void setSpeed(final double speed);
	
	boolean isReadyToPublish(final int messageIndex);
	
	ReceivedMqttMessage getMessage(final int messageIndex);
}
