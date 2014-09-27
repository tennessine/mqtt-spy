package pl.baczkowicz.mqttspy.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class ScriptedPublisher
{
	private final static Logger logger = LoggerFactory.getLogger(ScriptedPublisher.class);
	
	private final MqttConnection connection;
	
	private PublicationScriptProperties script;
	
	private int publishedMessages;
	
	public ScriptedPublisher(final MqttConnection connection, final PublicationScriptProperties script)
	{
		this.connection = connection;
		this.script = script;
	}
	
	public String execute(final String command) throws IOException, InterruptedException
	{
		Runtime rt = Runtime.getRuntime();
		Process p = rt.exec(command);
		p.waitFor();
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;

		try
		{
			final StringBuffer sb = new StringBuffer();
			while ((line = input.readLine()) != null)
			{
				sb.append(line);
			}
			return sb.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}			
		
		return null;
	}
	
	public void publish(final String publicationTopic, final String data)
	{
		publish(publicationTopic, data, 0, false);
	}
	
	public void publish(final String publicationTopic, final String data, final int qos, final boolean retained)
	{
		publishedMessages++;
		
		logger.debug("[JS {}] Publishing message to {} with payload = {}, qos = {}, retained = {}", script.getName(), publicationTopic, data, qos, retained);
		connection.publish(publicationTopic, data, qos, retained);
		
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				script.setCount(publishedMessages);				
			}
		});
	}
}
