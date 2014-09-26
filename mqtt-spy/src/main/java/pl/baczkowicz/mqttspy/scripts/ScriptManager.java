package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.baczkowicz.mqttspy.connectivity.MqttConnection;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class ScriptManager
{
	private static final String REFERENCE_ERROR = "ReferenceError";
		
	private final ObservableList<PublicationScriptProperties> observableScriptList = FXCollections.observableArrayList();
	
	private ScriptEngine scriptEngine;
	
	private Map<String, Object> scriptVariables;
	
	public ScriptManager(final MqttConnection connection)
	{
		scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		
		scriptVariables = new HashMap<String, Object>();
		scriptVariables.put("mqttspy", new ScriptedPublisher(connection, observableScriptList));
				
		putJavaVariablesIntoEngine(scriptEngine, scriptVariables);
	}
			
	public void populateScripts(final String directory)
	{
		// Adds new entries to the list of new files are present
		if (directory != null && !directory.isEmpty())
		{
			final List<File> files = getFileNamesForDirectory(directory, ".js");
			
			for (final File scriptFile : files)
			{
				PublicationScriptProperties script = retrievePublicationScriptProperties(observableScriptList, scriptFile);
				if (script == null)					
				{
					script = new PublicationScriptProperties(scriptFile.getName().replace(".js",  ""), scriptFile, 
							ScriptRunningState.STOPPED, null, 0);
					observableScriptList.add(script);
				}				
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
		
		return files;
	}
		
	private static void putJavaVariablesIntoEngine(final ScriptEngine engine, final Map<String, Object> variables)
	{
		final Bindings bindings = new SimpleBindings();

		for (String key : variables.keySet())
		{
			bindings.put(key, variables.get(key));
		}

		engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
	}
	
	public void evaluateScriptFile(final File scriptFile)
	{
		new Thread(new Runnable()
		{				
			@Override
			public void run()
			{
				try
				{						
					scriptEngine.eval(new FileReader(scriptFile));
					
					// TODO: update last completed
				}
				catch (ScriptException e)
				{
					handleException(e);
				}
				catch (FileNotFoundException e)
				{			
					e.printStackTrace();
				}
			}						
		}).start();
	}
	
	
	private static void handleException(ScriptException exception)
	{
		if (isReferenceError(exception))
		{
			throw new IllegalArgumentException("I couldn't resolve a variable on expression with vars ", exception);
		}
		throw new RuntimeException(exception);
	}

	private static boolean isReferenceError(ScriptException exception)
	{
		String message = exception.getMessage();
		return message.startsWith(REFERENCE_ERROR);
	}
	
	public static void incrementMessageCount(final ObservableList<PublicationScriptProperties> observableScriptList, final String scriptName)
	{
		for (final PublicationScriptProperties script : observableScriptList)
		{
			if (script.nameProperty().getValue().equals(scriptName))
			{
				script.setCount(script.countProperty().getValue() + 1);
			}
		}
	}

	public ObservableList<PublicationScriptProperties> getObservableScriptList()
	{
		return observableScriptList;
	}
}
