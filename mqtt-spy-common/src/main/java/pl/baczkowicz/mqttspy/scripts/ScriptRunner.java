/***********************************************************************************
 * 
 * Copyright (c) 2014 Kamil Baczkowicz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * 
 *    Kamil Baczkowicz - initial API and implementation and/or initial documentation
 *    
 */
package pl.baczkowicz.mqttspy.scripts;

import java.io.FileReader;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.utils.ThreadingUtils;

public class ScriptRunner implements Runnable
{
	private final static Logger logger = LoggerFactory.getLogger(ScriptRunner.class);
	
	private final PublicationScriptProperties script;

	private IScriptEventManager eventManager;

	private Executor executor;

	public ScriptRunner(final IScriptEventManager eventManager, final PublicationScriptProperties script, final Executor executor)
	{
		this.script = script;
		this.eventManager = eventManager;
		this.executor = executor;
	}
	
	public void run()
	{
		Thread.currentThread().setName("Script " + script.getName());
		ThreadingUtils.logStarting();
		
		script.getPublicationScriptIO().touch();
		script.setThread(Thread.currentThread());
		
		boolean firstRun = true;

		while (firstRun || script.isRepeat())
		{
			firstRun = false;
			
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
		
		script.getPublicationScriptIO().stop();
		
		ThreadingUtils.logEnding();
	}
	
	public static void changeState(final IScriptEventManager eventManager, final String scriptName, 
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
