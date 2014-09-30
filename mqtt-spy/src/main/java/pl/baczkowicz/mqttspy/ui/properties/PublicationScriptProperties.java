package pl.baczkowicz.mqttspy.ui.properties;

import java.io.File;
import java.util.Date;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.script.ScriptEngine;

import pl.baczkowicz.mqttspy.scripts.PublicationScriptIO;
import pl.baczkowicz.mqttspy.scripts.ScriptHealthDetector;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class PublicationScriptProperties
{
	private SimpleStringProperty name;
	
	private SimpleObjectProperty<ScriptRunningState> status;
	
	private SimpleStringProperty lastPublished;
	
	private SimpleIntegerProperty count;

	private File file;

	private Thread thread;
	
	private final ScriptEngine scriptEngine;

	private PublicationScriptIO publicationScriptIO;
	
	private long scriptTimeout = ScriptHealthDetector.DEFAULT_THREAD_TIMEOUT;

	public PublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastPublished, final int messageCount, final ScriptEngine scriptEngine)
	{
		this.name = new SimpleStringProperty(name);
		this.status = new SimpleObjectProperty<ScriptRunningState>(state);
		this.file = file;
		this.lastPublished = new SimpleStringProperty(lastPublished == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastPublished));
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
	
	public SimpleStringProperty lastPublishedProperty()
	{
		return this.lastPublished;
	}
	
	public SimpleIntegerProperty countProperty()
	{
		return this.count;
	}
	
	public void setCount(final int messageCount)
	{
		this.count.set(messageCount);
	}
	
	public void setLastPublished(final Date lastStarted)
	{
		this.lastPublished.set(Utils.DATE_WITH_SECONDS_SDF.format(lastStarted));
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

	public void setPublicationScriptIO(PublicationScriptIO publicationScriptIO)
	{
		this.publicationScriptIO = publicationScriptIO;
	}
	
	public PublicationScriptIO getPublicationScriptIO()
	{
		return publicationScriptIO;
	}

	public void setScriptTimeout(long customTimeout)
	{
		this.scriptTimeout = customTimeout;		
	}
	
	public long getScriptTimeout()
	{
		return scriptTimeout;
	}
}
