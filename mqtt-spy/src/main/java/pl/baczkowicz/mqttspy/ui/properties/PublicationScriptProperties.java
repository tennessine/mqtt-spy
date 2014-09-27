package pl.baczkowicz.mqttspy.ui.properties;

import java.io.File;
import java.util.Date;

import javax.script.ScriptEngine;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class PublicationScriptProperties
{
	private SimpleStringProperty name;
	
	private SimpleObjectProperty<ScriptRunningState> status;
	
	private SimpleStringProperty lastStarted;
	
	private SimpleIntegerProperty count;

	private File file;

	private Thread thread;
	
	private final ScriptEngine scriptEngine;

	public PublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastStarted, final int messageCount, final ScriptEngine scriptEngine)
	{
		this.name = new SimpleStringProperty(name);
		this.status = new SimpleObjectProperty<ScriptRunningState>(state);
		this.file = file;
		this.lastStarted = new SimpleStringProperty(lastStarted == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastStarted));
		this.count = new SimpleIntegerProperty(messageCount);
		this.scriptEngine = scriptEngine;
	}
	
	public SimpleStringProperty nameProperty()
	{
		return this.name;
	}
	
	public SimpleObjectProperty<ScriptRunningState> statusProperty()
	{
		return this.status;
	}
	
	public SimpleStringProperty lastStartedProperty()
	{
		return this.lastStarted;
	}
	
	public SimpleIntegerProperty countProperty()
	{
		return this.count;
	}
	
	public void setCount(final int messageCount)
	{
		this.count.set(messageCount);
	}
	
	public void setLastStarted(final Date lastStarted)
	{
		this.lastStarted.set(Utils.DATE_WITH_SECONDS_SDF.format(lastStarted));
	}
	
	public void setStatus(final ScriptRunningState state)
	{
		this.status.setValue(state);
	}
	
	public File getFile()
	{
		return this.file;
	}

	public void setThread(Thread currentThread)
	{
		this.thread = currentThread;		
	}
	
	public Thread getThread()
	{
		return this.thread;
	}

	public ScriptEngine getScriptEngine()
	{
		return scriptEngine;
	}

	public String getName()
	{
		return nameProperty().getValue();
	}

	public ScriptRunningState getStatus()
	{
		return status.getValue();
	}
}
