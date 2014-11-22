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
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionInterface;
import pl.baczkowicz.mqttspy.exceptions.CriticalException;

public class ScriptManager
{
	final static Logger logger = LoggerFactory.getLogger(ScriptManager.class);
	
	private Map<File, PublicationScriptProperties> scripts = new HashMap<File, PublicationScriptProperties>();
	
	private ScriptEventManagerInterface eventManager;

	private Executor executor;

	protected MqttConnectionInterface connection;
	
	public ScriptManager(final ScriptEventManagerInterface eventManager, final Executor executor, final MqttConnectionInterface connection)
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
		
		final PublicationScriptProperties script = new PublicationScriptProperties();
				
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
	
	public Map<File, PublicationScriptProperties> getScripts()
	{
		return scripts;
	}
		
	public void createScript(final PublicationScriptProperties scriptProperties,
			String scriptName, File scriptFile, final MqttConnectionInterface connection, final ScriptDetails scriptDetails)
	// public PublicationScriptProperties createScript(String scriptName, File scriptFile, final MqttConnectionInterface connection, final Script scriptDetails)
	{
		final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");										
		
		if (scriptEngine != null)
		{
//			final PublicationScriptProperties script = new PublicationScriptProperties(scriptName, scriptFile, 
//					ScriptRunningState.NOT_STARTED, null, 0, scriptEngine, scriptDetails.isRepeat());
			scriptProperties.setScriptName(scriptName);
			scriptProperties.setScriptFile(scriptFile);
			scriptProperties.setStatus(ScriptRunningState.NOT_STARTED);
			scriptProperties.setScriptEngine(scriptEngine);
			scriptProperties.setDetails(scriptDetails);
			
			scriptProperties.setPublicationScriptIO(new PublicationScriptIO(connection, eventManager, scriptProperties, executor));
			
			final Map<String, Object> scriptVariables = new HashMap<String, Object>();
			scriptVariables.put("mqttspy", scriptProperties.getPublicationScriptIO());	
			scriptVariables.put("logger", LoggerFactory.getLogger(ScriptRunner.class));
			
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
	
	public void runScriptFile(final PublicationScriptProperties script)
	{
		// Only start if not running already
		if (!ScriptRunningState.RUNNING.equals(script.getStatus()))
		{
			new Thread(new ScriptRunner(eventManager, script, executor)).start();
		}		
	}	

	public void runScriptFile(final File scriptFile)
	{
		final PublicationScriptProperties script = getScript(scriptFile); 
		
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
	
	public PublicationScriptProperties getScript(final File scriptFile)
	{
		return scripts.get(scriptFile);
	}

	public ScriptEventManagerInterface getEventManager()
	{
		return eventManager;
	}
}
