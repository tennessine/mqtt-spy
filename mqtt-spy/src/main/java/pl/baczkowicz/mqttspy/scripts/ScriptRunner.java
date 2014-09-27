package pl.baczkowicz.mqttspy.scripts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class ScriptRunner implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);
	
	private final PublicationScriptProperties script;

	private EventManager eventManager;

	public ScriptRunner(final EventManager eventManager, final PublicationScriptProperties script)
	{
		this.script = script;
		this.eventManager = eventManager;
	}
	
	@Override
	public void run()
	{
		script.setStatus(ScriptRunningState.RUNNING);
		eventManager.notifyScriptStateChange(script.getName(), ScriptRunningState.RUNNING);
		script.setLastStarted(new Date());
		script.setThread(Thread.currentThread());

		try
		{
			final Object returnValue = script.getScriptEngine().eval(new FileReader(script.getFile()));
			
			if (returnValue instanceof Boolean)
			{
				if ((boolean) returnValue)
				{
					changeState(script.getName(), ScriptRunningState.FINISHED, script);
				}
				else
				{
					changeState(script.getName(), ScriptRunningState.STOPPED, script);
				}
			}
			else
			{
				changeState(script.getName(), ScriptRunningState.FINISHED, script);
			}
		}
		catch (ScriptException e)
		{
			changeState(script.getName(), ScriptRunningState.FAILED, script);
			ScriptManager.handleException(e);
		}
		catch (FileNotFoundException e)
		{
			changeState(script.getName(), ScriptRunningState.FAILED, script);
			e.printStackTrace();
		}		
	}
	
	private void changeState(final String scriptName, final ScriptRunningState newState, final PublicationScriptProperties script)
	{
		logger.trace("Changing script state to " + newState);
		script.setStatus(newState);
		eventManager.notifyScriptStateChange(scriptName, newState);
	}
}
