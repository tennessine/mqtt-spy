package pl.baczkowicz.mqttspy.scripts;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.utils.Utils;

public class ScriptHealthDetector implements Runnable
{
	public final static int DEFAULT_THREAD_TIMEOUT = 5000;
	
	private final static Logger logger = LoggerFactory.getLogger(ScriptHealthDetector.class);
	
	private final PublicationScriptProperties script;

	private ScriptEventManagerInterface eventManager;

	private Executor executor;

	public ScriptHealthDetector(final ScriptEventManagerInterface eventManager, final PublicationScriptProperties script, final Executor executor)
	{
		this.script = script;
		this.eventManager = eventManager;
		this.executor = executor;
	}
	
	public void run()
	{
		while (script.getStatus().equals(ScriptRunningState.RUNNING))
		{
			if (script.getPublicationScriptIO().getLastTouch() + script.getScriptTimeout() < Utils.getMonotonicTimeInMilliseconds())
			{
				logger.warn("Script {} detected as frozen, last touch = {}, current time = {}", script.getName(), 
						script.getPublicationScriptIO().getLastTouch(), Utils.getMonotonicTimeInMilliseconds());
				ScriptRunner.changeState(eventManager, script.getName(), ScriptRunningState.FROZEN, script, executor);
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
	}
}
