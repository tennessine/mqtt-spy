package pl.baczkowicz.mqttspy.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class ScriptHealthDetector implements Runnable
{
	public final static int DEFAULT_THREAD_TIMEOUT = 5000;
	
	private final static Logger logger = LoggerFactory.getLogger(ScriptHealthDetector.class);
	
	private final PublicationScriptProperties script;

	private EventManager eventManager;

	public ScriptHealthDetector(final EventManager eventManager, final PublicationScriptProperties script)
	{
		this.script = script;
		this.eventManager = eventManager;
	}
	
	@Override
	public void run()
	{
		while (script.getStatus().equals(ScriptRunningState.RUNNING))
		{
			if (script.getPublicationScriptIO().getLastTouch() + script.getScriptTimeout() < Utils.getMonotonicTimeInMilliseconds())
			{
				logger.warn("Script {} detected as frozen, last touch = {}, current time = {}", script.getName(), 
						script.getPublicationScriptIO().getLastTouch(), Utils.getMonotonicTimeInMilliseconds());
				ScriptRunner.changeState(eventManager, script.getName(), ScriptRunningState.FROZEN, script);
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
