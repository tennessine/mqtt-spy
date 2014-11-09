package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionInterface;
import pl.baczkowicz.mqttspy.ui.utils.RunLaterExecutor;

public class InteractiveScriptManager extends ScriptManager
{
	private final static Logger logger = LoggerFactory.getLogger(InteractiveScriptManager.class);
		
	private final ObservableList<PublicationScriptProperties> observableScriptList = FXCollections.observableArrayList();
	
	private MqttConnectionInterface connection;
	
	public InteractiveScriptManager(final ScriptEventManagerInterface eventManager, final MqttConnectionInterface connection)
	{
		super(eventManager, new RunLaterExecutor());
		this.connection = connection;
	}
			
	public void populateScripts(final String directory)
	{
		final List<File> files = new ArrayList<File>(); 
		
		// Adds new entries to the list of new files are present
		if (directory != null && !directory.isEmpty())
		{
			files.addAll(getFileNamesForDirectory(directory, ".js"));				
		}
		else
		{
			// If directory defined, use the mqtt-spy's home directory
			files.addAll(getFileNamesForDirectory(ConfigurationManager.getDefaultHomeDirectory(), ".js"));
		}	
		
		for (final File scriptFile : files)
		{
			PublicationScriptProperties script = retrievePublicationScriptProperties(observableScriptList, scriptFile);
			if (script == null)					
			{
				final String scriptName = getScriptName(scriptFile);
				final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");										
				
				script = new PublicationScriptProperties(scriptName, scriptFile, 
						ScriptRunningState.NOT_STARTED, null, 0, scriptEngine);
				script.setPublicationScriptIO(new PublicationScriptIO(connection, getEventManager(), script, new RunLaterExecutor()));
				
				final Map<String, Object> scriptVariables = new HashMap<String, Object>();
				scriptVariables.put("mqttspy", script.getPublicationScriptIO());	
				scriptVariables.put("logger", LoggerFactory.getLogger(ScriptRunner.class));
				
				putJavaVariablesIntoEngine(scriptEngine, scriptVariables);
				
				observableScriptList.add(script);
				getScripts().put(scriptFile, script);
			}				
		}			
	}
	
	private static PublicationScriptProperties retrievePublicationScriptProperties(final List<PublicationScriptProperties> scriptList, final File scriptFile)
	{
		for (final PublicationScriptProperties script : scriptList)
		{
			if (script.getFile().getAbsolutePath().equals(scriptFile.getAbsolutePath()))
			{
				return script;
			}
		}
		
		return null;
	}
	
	private static List<File> getFileNamesForDirectory(final String directory, final String extension)
	{
		final List<File> files = new ArrayList<File>();
		
		final File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null)
		{
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					if (extension == null || extension.isEmpty() ||  listOfFiles[i].getName().endsWith(extension))
					{
						files.add(listOfFiles[i]);
					}
				}
			}
		}
		else
		{
			logger.error("No files in {}", directory);
		}
		
		return files;
	}
	
	public void evaluateScriptFile(final File scriptFile)
	{
		PublicationScriptProperties script = getScripts().get(scriptFile);
		
		// Only start if not running already
		if (!script.getStatus().equals(ScriptRunningState.RUNNING))
		{
			new Thread(new ScriptRunner(getEventManager(), script, new RunLaterExecutor())).start();
		}		
	}
	
	public void stopScriptFile(final File scriptFile)
	{
		final Thread scriptThread = getPublicationScriptProperties(observableScriptList, getScriptName(scriptFile)).getThread();

		if (scriptThread != null)
		{
			scriptThread.interrupt();
		}
	}
	
	public static PublicationScriptProperties getPublicationScriptProperties(final ObservableList<PublicationScriptProperties> observableScriptList, final String scriptName)
	{
		for (final PublicationScriptProperties script : observableScriptList)
		{
			if (script.getName().equals(scriptName))
			{
				return script;				
			}
		}
		
		return null;
	}

	public ObservableList<PublicationScriptProperties> getObservableScriptList()
	{
		return observableScriptList;
	}		
}
