package pl.baczkowicz.mqttspy.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javafx.application.Platform;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class PublicationScriptIO implements PublicationScriptIOInterface
{
	private final static Logger logger = LoggerFactory.getLogger(PublicationScriptIO.class);
	
	private final MqttConnection connection;
	
	private PublicationScriptProperties script;
	
	private int publishedMessages;
	
	private long lastTouch;

	private final EventManager eventManager;
	
	public PublicationScriptIO(final MqttConnection connection, final EventManager eventManager, final PublicationScriptProperties script)
	{
		this.eventManager = eventManager;
		this.connection = connection;
		this.script = script;
	}
	
	@Override
	public void touch()
	{
		this.lastTouch = Utils.getMonotonicTimeInMilliseconds();
	}
	
	@Override
	public void setScriptTimeout(final long customTimeout)
	{
		script.setScriptTimeout(customTimeout);
		logger.debug("Timeout for script {} changed to {}", script.getName(), customTimeout);
	}
	
	@Override
	public boolean instantiate(final String className)
	{
		try
		{
			final Bindings bindings = script.getScriptEngine().getBindings(ScriptContext.ENGINE_SCOPE);
			bindings.put(className.replace(".", "_"), Class.forName(className).newInstance());
			script.getScriptEngine().setBindings(bindings, ScriptContext.ENGINE_SCOPE);
			return true;
		}
		catch (Exception e)
		{
			logger.error("Cannot instantiate class " + className, e);
			return false;
		}
	}
	
	@Override
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
	
	@Override
	public void publish(final String publicationTopic, final String data)
	{
		publish(publicationTopic, data, 0, false);
	}
	
	@Override
	public void publish(final String publicationTopic, final String data, final int qos, final boolean retained)
	{
		touch();
		if (connection.canPublish())
		{
			publishedMessages++;
			
			logger.debug("[JS {}] Publishing message to {} with payload = {}, qos = {}, retained = {}", script.getName(), publicationTopic, data, qos, retained);
			connection.publish(publicationTopic, data, qos, retained);
			
			Platform.runLater(new Runnable()
			{			
				@Override
				public void run()
				{
					script.setLastPublished(new Date());
					ScriptRunner.changeState(eventManager, script.getName(), ScriptRunningState.RUNNING, script);
					script.setCount(publishedMessages);				
				}
			});
		}
	}

	public long getLastTouch()
	{
		return lastTouch;
	}
}
