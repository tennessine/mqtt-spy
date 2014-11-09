package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

public class ScriptManager
{
	private Map<File, PublicationScriptProperties> scripts = new HashMap<File, PublicationScriptProperties>();

	private ScriptEventManagerInterface eventManager;

	private Executor executor;
	
	public ScriptManager(final ScriptEventManagerInterface eventManager, final Executor executor)
	{
		this.eventManager = eventManager;
		this.executor = executor;
	}
	
	public static String getScriptName(final File file)
	{
		return file.getName().replace(".js",  "");
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
	
	public void evaluateScriptFile(final File scriptFile)
	{
		final PublicationScriptProperties script = scripts.get(scriptFile);
		
		// Only start if not running already
		if (!script.getStatus().equals(ScriptRunningState.RUNNING))
		{
			new Thread(new ScriptRunner(eventManager, script, executor)).start();
		}		
	}

	public Map<File, PublicationScriptProperties> getScripts()
	{
		return scripts;
	}

	public ScriptEventManagerInterface getEventManager()
	{
		return eventManager;
	}
}
