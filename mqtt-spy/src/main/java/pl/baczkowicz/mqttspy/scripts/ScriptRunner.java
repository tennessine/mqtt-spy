package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;

import javafx.collections.ObservableList;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import pl.baczkowicz.mqttspy.events.EventManager;
import pl.baczkowicz.mqttspy.ui.properties.PublicationScriptProperties;

public class ScriptRunner implements Runnable
{
	private File scriptFile;
	
	private final ScriptEngine scriptEngine;

	private final ObservableList<PublicationScriptProperties> observableScriptList;

	private EventManager eventManager;

	public ScriptRunner(final EventManager eventManager, final ScriptEngine scriptEngine, 
			final File scriptFile, final ObservableList<PublicationScriptProperties> observableScriptList)
	{
		this.scriptEngine = scriptEngine;
		this.scriptFile = scriptFile;
		this.observableScriptList = observableScriptList;
		this.eventManager = eventManager;
	}
	
	@Override
	public void run()
	{
		final String scriptName = ScriptManager.getScriptName(scriptFile);
		final PublicationScriptProperties publicationScriptProperties = ScriptManager.getPublicationScriptProperties(observableScriptList, scriptName);
		publicationScriptProperties.setStatus(ScriptRunningState.RUNNING);
		eventManager.notifyScriptStateChange(scriptName, ScriptRunningState.RUNNING);
		publicationScriptProperties.setLastStarted(new Date());
		publicationScriptProperties.setThread(Thread.currentThread());

		try
		{
			scriptEngine.eval(new FileReader(scriptFile));
			changeState(scriptName, ScriptRunningState.STOPPED, publicationScriptProperties);
		}
		catch (ScriptException e)
		{
			changeState(scriptName, ScriptRunningState.FAILED, publicationScriptProperties);
			ScriptManager.handleException(e);
		}
		catch (FileNotFoundException e)
		{
			changeState(scriptName, ScriptRunningState.FAILED, publicationScriptProperties);
			e.printStackTrace();
		}		
	}
	
	private void changeState(final String scriptName, final ScriptRunningState newState, final PublicationScriptProperties publicationScriptProperties)
	{
		publicationScriptProperties.setStatus(newState);
		eventManager.notifyScriptStateChange(scriptName, newState);
	}
}
