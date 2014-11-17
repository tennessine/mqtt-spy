package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.Date;

import javax.script.ScriptEngine;

import pl.baczkowicz.mqttspy.utils.Utils;

public class PublicationScriptProperties
{
	private String name;
	
	private ScriptRunningState status;
	
	private boolean repeat;
	
	private String lastPublished;
	
	private Long messageCount;

	private File file;

	private Thread thread;
	
	private final ScriptEngine scriptEngine;

	private PublicationScriptIO publicationScriptIO;
	
	private long scriptTimeout = ScriptHealthDetector.DEFAULT_THREAD_TIMEOUT;

	public PublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastPublished, 
			final long messageCount, final ScriptEngine scriptEngine, final boolean repeat)
	{
//		this.name = new SimpleStringProperty(name);
//		this.status = new SimpleObjectProperty<ScriptRunningState>(state);		
//		this.lastPublished = new SimpleStringProperty(lastPublished == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastPublished));
//		this.count = new SimpleIntegerProperty(messageCount);
		this.name = name;
		this.status = state;		
		this.lastPublished = lastPublished == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastPublished);
		this.messageCount = messageCount;
		
		this.file = file;
		this.repeat = repeat;
		this.scriptEngine = scriptEngine;
	}
	
//	public SimpleStringProperty nameProperty()
//	{
//		return this.name;
//	}
//	
//	public SimpleObjectProperty<ScriptRunningState> statusProperty()
//	{
//		return this.status;
//	}
//	
//	public SimpleStringProperty lastPublishedProperty()
//	{
//		return this.lastPublished;
//	}
//	
//	public SimpleIntegerProperty countProperty()
//	{
//		return this.count;
//	}
	
	public void setCount(final long messageCount)
	{
		// this.count.set(messageCount);
		this.messageCount = messageCount;
	}
	
	public String getLastPublished()
	{
		return lastPublished;
	}

	public Long getCount()
	{
		return messageCount;
	}

	public void setLastPublished(final Date lastPublished)
	{
		// this.lastPublished.set(Utils.DATE_WITH_SECONDS_SDF.format(lastStarted));
		this.lastPublished = Utils.DATE_WITH_SECONDS_SDF.format(lastPublished);
	}
	
	public void setStatus(final ScriptRunningState state)
	{
		// this.status.setValue(state);
		this.status = state;
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
		// return nameProperty().getValue();
		return name;
	}

	public ScriptRunningState getStatus()
	{
		// return status.getValue();
		return status;
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

	public boolean isRepeat()
	{
		return repeat;
	}

	public void setRepeat(boolean repeat)
	{
		this.repeat = repeat;
	}
}
