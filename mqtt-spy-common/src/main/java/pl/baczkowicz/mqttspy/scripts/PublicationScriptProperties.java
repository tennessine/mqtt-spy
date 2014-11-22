package pl.baczkowicz.mqttspy.scripts;

import java.io.File;
import java.util.Date;

import javax.script.ScriptEngine;

import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import pl.baczkowicz.mqttspy.utils.Utils;

public class PublicationScriptProperties
{
	private String name;
	
	private ScriptRunningState status;
	
	// private boolean repeat;
	
	private String lastPublished;
	
	private Long messageCount;

	private File file;

	private Thread thread;
	
	private ScriptEngine scriptEngine;

	private PublicationScriptIO publicationScriptIO;
	
	private long scriptTimeout = ScriptHealthDetector.DEFAULT_THREAD_TIMEOUT;

	private Date lastPublishedDate;

	private ScriptDetails scriptDetails;

	public PublicationScriptProperties()
	{
		// Default
	}
	
//	public PublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastPublished, 
//			final long messageCount, final ScriptEngine scriptEngine, final boolean repeat)
//	{
//		this.name = name;
//		this.status = state;		
//		this.lastPublished = lastPublished == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastPublished);
//		this.messageCount = messageCount;
//		
//		this.file = file;
//		this.repeat = repeat;
//		this.scriptEngine = scriptEngine;
//	}
	
	public void setCount(final long messageCount)
	{
		this.messageCount = messageCount;
	}
	
	public String getLastPublished()
	{
		return lastPublished;
	}
	
	public Date getLastPublishedDate()
	{
		return lastPublishedDate;
	}

	public Long getCount()
	{
		return messageCount;
	}

	public void setLastPublishedDate(final Date lastPublishedDate)
	{
		this.lastPublishedDate = lastPublishedDate;
		this.lastPublished = Utils.DATE_WITH_SECONDS_SDF.format(lastPublishedDate);
	}
	
	public void setStatus(final ScriptRunningState state)
	{
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
		return name;
	}

	public ScriptRunningState getStatus()
	{
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
		return scriptDetails.isRepeat();
	}

//	public void setRepeat(boolean repeat)
//	{
//		this.repeat = repeat;
//	}

	public void setScriptEngine(final ScriptEngine scriptEngine)
	{
		this.scriptEngine = scriptEngine;
	}

	public void setScriptFile(final File scriptFile)
	{
		this.file = scriptFile;
	}

	public void setScriptName(String scriptName)
	{
		this.name = scriptName;
	}

	public void setDetails(final ScriptDetails scriptDetails)
	{
		this.scriptDetails = scriptDetails;		
	}
	
	public ScriptDetails getScriptDetails()
	{
		return this.scriptDetails ;
	}
}
