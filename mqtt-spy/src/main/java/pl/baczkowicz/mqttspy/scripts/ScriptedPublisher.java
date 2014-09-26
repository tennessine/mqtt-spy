package pl.baczkowicz.mqttspy.scripts;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class ScriptedPublisher
{
	private final static Logger logger = LoggerFactory.getLogger(ScriptedPublisher.class);
	
	private final MqttConnection connection;
	
	private ObservableList<PublicationScriptProperties> observableScriptList;
	
	public ScriptedPublisher(final MqttConnection connection, final ObservableList<PublicationScriptProperties> observableScriptList)
	{
		this.connection = connection;
		this.observableScriptList = observableScriptList;
	}
	
	public void publish(final String scriptName, final String publicationTopic, final String data)
	{
		publish(scriptName, publicationTopic, data, 0, false);
	}
	
	public void publish(final String scriptName, final String publicationTopic, final String data, final int qos, final boolean retained)
	{
		logger.info("[JS {}] Publishing message to {} with payload = {}, qos = {}, retained = {}", scriptName, publicationTopic, data, qos, retained);
		connection.publish(publicationTopic, data, qos, retained);
		
		Platform.runLater(new Runnable()
		{			
			@Override
			public void run()
			{
				ScriptManager.incrementMessageCount(observableScriptList, scriptName);				
			}
		});		
	}
}
