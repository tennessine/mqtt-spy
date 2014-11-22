package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.connectivity.MqttConnectionInterface;
import pl.baczkowicz.mqttspy.ui.utils.RunLaterExecutor;

public class InteractiveScriptManager extends ScriptManager
{
	private final static Logger logger = LoggerFactory.getLogger(InteractiveScriptManager.class);
	
	private final ObservableList<ObservablePublicationScriptProperties> observableScriptList = FXCollections.observableArrayList();
	
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
			ObservablePublicationScriptProperties script = retrievePublicationScriptProperties(observableScriptList, scriptFile);
			if (script == null)					
			{
				final String scriptName = getScriptName(scriptFile);
				
				final ObservablePublicationScriptProperties properties = new ObservablePublicationScriptProperties();
				
				createScript(properties, scriptName, scriptFile, connection, new ScriptDetails(false, scriptFile.getName())); 			
				
				observableScriptList.add(properties);
				getScripts().put(scriptFile, properties);
			}				
		}			
	}
	
	private static ObservablePublicationScriptProperties retrievePublicationScriptProperties(final List<ObservablePublicationScriptProperties> scriptList, final File scriptFile)
	{
		for (final ObservablePublicationScriptProperties script : scriptList)
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
	
	public static ObservablePublicationScriptProperties getPublicationScriptProperties(final ObservableList<ObservablePublicationScriptProperties> observableScriptList, final String scriptName)
	{
		for (final ObservablePublicationScriptProperties script : observableScriptList)
		{
			if (script.getName().equals(scriptName))
			{
				return script;				
			}
		}
		
		return null;
	}
	
	public ObservableList<ObservablePublicationScriptProperties> getObservableScriptList()
	{
		return observableScriptList;
	}		
}
