package pl.baczkowicz.mqttspy.scripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.Executor;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.connectivity.MqttConnectionInterface;
import pl.baczkowicz.mqttspy.utils.Utils;

public class PublicationScriptIO implements PublicationScriptIOInterface
{
	private final static Logger logger = LoggerFactory.getLogger(PublicationScriptIO.class);
	
	private final MqttConnectionInterface connection;
	
	private PublicationScriptProperties script;
	
	private int publishedMessages;
	
	private long lastTouch;

	private final ScriptEventManagerInterface eventManager;

	private Executor executor;
	
	public PublicationScriptIO(
			final MqttConnectionInterface connection, final ScriptEventManagerInterface eventManager, 
			final PublicationScriptProperties script, final Executor executor)
	{
		this.eventManager = eventManager;
		this.connection = connection;
		this.script = script;
		this.executor = executor;
	}
	
	public void touch()
	{
		this.lastTouch = Utils.getMonotonicTimeInMilliseconds();
	}
	
	public void setScriptTimeout(final long customTimeout)
	{
		script.setScriptTimeout(customTimeout);
		logger.debug("Timeout for script {} changed to {}", script.getName(), customTimeout);
	}
	
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
		touch();
		if (connection.canPublish())
		{
			publishedMessages++;
			
			if (!script.getStatus().equals(ScriptRunningState.RUNNING))
			{
				ScriptRunner.changeState(eventManager, script.getName(), ScriptRunningState.RUNNING, script, executor);
			}
			
			logger.debug("[JS {}] Publishing message to {} with payload = {}, qos = {}, retained = {}", script.getName(), publicationTopic, data, qos, retained);
			connection.publish(publicationTopic, data, qos, retained);
						
			executor.execute(new Runnable()
			{			
				public void run()
				{
					script.setLastPublished(new Date());
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
