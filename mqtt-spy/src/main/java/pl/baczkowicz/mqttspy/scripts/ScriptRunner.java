package pl.baczkowicz.mqttspy.scripts;

import java.io.FileReader;

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
		script.getPublicationScriptIO().touch();
		script.setThread(Thread.currentThread());

		changeState(script.getName(), ScriptRunningState.RUNNING, script);
		new Thread(new ScriptHealthDetector(eventManager, script)).start();		
		
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
		catch (Exception e)
		{
			changeState(script.getName(), ScriptRunningState.FAILED, script);
			logger.error("Script execution exception", e);
			// ScriptManager.handleException(e);
		}		
	}
	
	public static void changeState(final EventManager eventManager, final String scriptName, 
			final ScriptRunningState newState, final PublicationScriptProperties script)
	{
		logger.trace("Changing script state to " + newState);
		script.setStatus(newState);
		eventManager.notifyScriptStateChange(scriptName, newState);
	}
	
	private void changeState(final String scriptName, final ScriptRunningState newState, final PublicationScriptProperties script)
	{
		changeState(eventManager, scriptName, newState, script);
	}
}
