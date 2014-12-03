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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import pl.baczkowicz.mqttspy.connectivity.IMqttConnection;
import pl.baczkowicz.mqttspy.exceptions.CriticalException;
import pl.baczkowicz.mqttspy.messages.IMqttMessage;
import pl.baczkowicz.mqttspy.scripts.io.ScriptIO;

// TODO: describe the class
public class ScriptManager
{
	public static final String RECEIVED_MESSAGE_PARAMETER = "receivedMessage";
	
	public static final String MESSAGE_PARAMETER = "message";
	
	private final static Logger logger = LoggerFactory.getLogger(ScriptManager.class);
	
	private Map<File, Script> scripts = new HashMap<File, Script>();
	
	private IScriptEventManager eventManager;

	private Executor executor;

	protected IMqttConnection connection;
	
	public ScriptManager(final IScriptEventManager eventManager, final Executor executor, final IMqttConnection connection)
	{
		this.eventManager = eventManager;
		this.executor = executor;
		this.connection = connection;
	}
	
	public static String getScriptName(final File file)
	{
		return file.getName().replace(".js",  "");
	}
	
	public void addScript(final ScriptDetails scriptDetails)
	{
		final File scriptFile = new File(scriptDetails.getFile());
		
		final String scriptName = getScriptName(scriptFile);
		
		final Script script = new Script();
				
		createScript(script, scriptName, scriptFile, connection, scriptDetails);
		
		logger.debug("Adding script {}", scriptDetails.getFile());
		scripts.put(scriptFile, script);
	}
	
	public void addScript(final String filename)
	{
		addScript(new ScriptDetails(false, filename));
	}
	
	public void addScripts(final List<ScriptDetails> scriptFiles)
	{
		for (final ScriptDetails script : scriptFiles)
		{
			addScript(script);
		}
	}
	
	public Map<File, Script> getScripts()
	{
		return scripts;
	}
		
	public void createScript(final Script scriptProperties,
			String scriptName, File scriptFile, final IMqttConnection connection, final ScriptDetails scriptDetails)
	// public PublicationScriptProperties createScript(String scriptName, File scriptFile, final MqttConnectionInterface connection, final Script scriptDetails)
	{
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");										
		
		if (scriptEngine != null)
		{
//			final PublicationScriptProperties script = new PublicationScriptProperties(scriptName, scriptFile, 
//					ScriptRunningState.NOT_STARTED, null, 0, scriptEngine, scriptDetails.isRepeat());
			scriptProperties.setName(scriptName);
			scriptProperties.setScriptFile(scriptFile);
			scriptProperties.setStatus(ScriptRunningState.NOT_STARTED);
			scriptProperties.setScriptEngine(scriptEngine);
			scriptProperties.setScriptDetails(scriptDetails);
			
			scriptProperties.setPublicationScriptIO(new ScriptIO(connection, eventManager, scriptProperties, executor));
			
			final Map<String, Object> scriptVariables = new HashMap<String, Object>();
			scriptVariables.put("mqttspy", scriptProperties.getPublicationScriptIO());	
			scriptVariables.put("logger", LoggerFactory.getLogger(ScriptRunner.class));
			scriptVariables.put("messageLog", scriptProperties.getPublicationScriptIO().getMessageLog());
			
			putJavaVariablesIntoEngine(scriptEngine, scriptVariables);
			
//			return script;
		}
		else
		{
			throw new CriticalException("Cannot instantiate the nashorn javascript engine - most likely you don't have Java 8 installed. "
					+ "Please either disable scripts in your configuration file or install the appropriate JRE/JDK.");
		}
	}
					
	public static void putJavaVariablesIntoEngine(final ScriptEngine engine, final Map<String, Object> variables)
	{
		final Bindings bindings = new SimpleBindings();

		for (String key : variables.keySet())
		{
			bindings.put(key, variables.get(key));
		}

		engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
	}
	
	public void runScriptFile(final Script script)
	{
		runScriptFile(script, true);
	}
	
	public void runScriptFile(final Script script, final boolean asynchronous)
	{
		// Only start if not running already
		if (!ScriptRunningState.RUNNING.equals(script.getStatus()))
		{
			script.createScriptRunner(eventManager, executor);
			
			if (asynchronous)
			{
				new Thread(script.getScriptRunner()).start();
			}
			else
			{
				script.getScriptRunner().run();
			}
		}
	}	

	public void runScriptFile(final File scriptFile)
	{
		final Script script = getScript(scriptFile); 
		
		if (script != null)
		{
			runScriptFile(script);
		}
		else
		{
			// Not good in this happens... ;-)
			logger.warn("No script for {}", scriptFile.getName());
		}
	}
	
	public void runScriptFileWithReceivedMessage(final String scriptFile, final boolean asynchronous, final IMqttMessage receivedMessage)
	{
		final Script script = getScript(new File(scriptFile));
		
		if (script != null)
		{
			script.getScriptEngine().put(ScriptManager.RECEIVED_MESSAGE_PARAMETER, receivedMessage);
			runScriptFile(script, asynchronous);
		}
		else
		{
			logger.warn("No script found for {}", scriptFile);
		}
	}
	
	public void runScriptFileWithMessage(final Script script, final IMqttMessage message)
	{				
		script.getScriptEngine().put(ScriptManager.MESSAGE_PARAMETER, message);
		runScriptFile(script);		
	}
	
	public Script getScript(final File scriptFile)
	{
		return scripts.get(scriptFile);
	}

	public IScriptEventManager getEventManager()
	{
		return eventManager;
	}

	public boolean areScriptsRunning()
	{
		for (final Script script : scripts.values())
		{
			if (ScriptRunningState.RUNNING.equals(script.getStatus()))
			{
				return true;
			}
		}

		return false;
	}
}
