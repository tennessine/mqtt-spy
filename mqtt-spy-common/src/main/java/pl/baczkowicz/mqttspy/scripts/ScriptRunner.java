package pl.baczkowicz.mqttspy.scripts;

import java.io.FileReader;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptRunner implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);
	
	private final PublicationScriptProperties script;

	private ScriptEventManagerInterface eventManager;

	private Executor executor;

	public ScriptRunner(final ScriptEventManagerInterface eventManager, final PublicationScriptProperties script, final Executor executor)
	{
		this.script = script;
		this.eventManager = eventManager;
		this.executor = executor;
	}
	
	public void run()
	{
		script.getPublicationScriptIO().touch();
		script.setThread(Thread.currentThread());

		while (script.isRepeat())
		{
			changeState(script.getName(), ScriptRunningState.RUNNING, script);
			new Thread(new ScriptHealthDetector(eventManager, script, executor)).start();		
			
			try
			{
				final Object returnValue = script.getScriptEngine().eval(new FileReader(script.getFile()));
				logger.debug("Script {} returned with value {}", script.getName(), returnValue);
				
				// If nothing returned, assume all good
				if (returnValue == null)
				{
					changeState(script.getName(), ScriptRunningState.FINISHED, script);
				}
				// If boolean returned, check if OK
				else if (returnValue instanceof Boolean)
				{
					if ((Boolean) returnValue)
					{
						changeState(script.getName(), ScriptRunningState.FINISHED, script);
					}
					else
					{
						changeState(script.getName(), ScriptRunningState.STOPPED, script);
					}
				}
				// Anything else, assume all good
				else
				{
					changeState(script.getName(), ScriptRunningState.FINISHED, script);
				}
			}
			catch (Exception e)
			{
				changeState(script.getName(), ScriptRunningState.FAILED, script);
				logger.error("Script execution exception", e);
				break;
			}		
			
			if (script.isRepeat())
			{
				logger.debug("Re-running script {}", script.getName());
			}
		}
	}
	
	public static void changeState(final ScriptEventManagerInterface eventManager, final String scriptName, 
			final ScriptRunningState newState, final PublicationScriptProperties script, final Executor executor)
	{		
		logger.trace("Changing [{}] script's state to [{}]", scriptName, newState);
		script.setStatus(newState);
		
		if (eventManager != null && executor != null)
		{
			executor.execute(new Runnable()
			{			
				public void run()
				{							
					eventManager.notifyScriptStateChange(scriptName, newState);
				}
			});
		}
		else
		{
			logger.info("Changed [{}] script's state to [{}]", scriptName, newState);
		}
	}
	
	private void changeState(final String scriptName, final ScriptRunningState newState, final PublicationScriptProperties script)
	{
		changeState(eventManager, scriptName, newState, script, executor);
	}
}
