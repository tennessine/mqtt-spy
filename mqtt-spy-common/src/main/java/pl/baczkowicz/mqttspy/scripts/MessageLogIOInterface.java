package pl.baczkowicz.mqttspy.scripts;

public interface MessageLogIOInterface
{
	int readFromFile(final String logLocation);
	
	void start();
	
//	void pause();
	
	void stop();
	
	void setSpeed(final double speed);
	
	boolean isReadyToPublish(final int messageIndex);
}
