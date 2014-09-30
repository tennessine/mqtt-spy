package pl.baczkowicz.mqttspy.scripts;

import java.io.IOException;

public interface PublicationScriptIOInterface
{
	void publish(String publicationTopic, String data);

	void publish(String publicationTopic, String data, int qos, boolean retained);
	
	/**
	 * Informs the java side the script is still alive.
	 */
	void touch();

	/**
	 * Sets a custom thread timeout for the script.
	 *  
	 * @param customTimeout Custom timeout in milliseconds (normally expected to be higher than the default)
	 */
	void setScriptTimeout(long customTimeout);

	boolean instantiate(String className);

	String execute(String command) throws IOException, InterruptedException;
}