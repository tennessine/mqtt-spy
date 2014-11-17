package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.Script;
import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionInterface;
import pl.baczkowicz.mqttspy.ui.utils.RunLaterExecutor;

public class InteractiveScriptManager extends ScriptManager
{
	private final static Logger logger = LoggerFactory.getLogger(InteractiveScriptManager.class);
	
	private final ObservableList<PublicationScriptProperties> observableScriptList = FXCollections.observableArrayList();
	
	// private MqttConnectionInterface connection;
	
	public InteractiveScriptManager(final ScriptEventManagerInterface eventManager, final MqttConnectionInterface connection)
	{
		super(eventManager, new RunLaterExecutor(), connection);
	}
			
	public void addScripts(final String directory)
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
		
		populateScriptsFromFileList(files);
	}
	
	public void populateScriptsFromFileList(final List<File> files)
	{
		for (final File scriptFile : files)
		{
			PublicationScriptProperties script = retrievePublicationScriptProperties(observableScriptList, scriptFile);
			if (script == null)					
			{
				final String scriptName = getScriptName(scriptFile);
				
				script = createScript(scriptName, scriptFile, connection, new Script(false, scriptFile.getName()));
				
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
