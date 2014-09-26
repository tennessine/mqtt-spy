package pl.baczkowicz.mqttspy.ui.properties;

import java.io.File;
import java.util.Date;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import pl.baczkowicz.mqttspy.scripts.ScriptRunningState;
import pl.baczkowicz.mqttspy.ui.utils.Utils;

public class PublicationScriptProperties
{
	private SimpleStringProperty name;
	
	private ScriptRunningState state;
	
	private SimpleStringProperty lastCompleted;
	
	private SimpleIntegerProperty count;

	private File file;

	public PublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastCompleted, final int messageCount)
	{
		this.name = new SimpleStringProperty(name);
		this.state = state;
		this.file = file;
		this.lastCompleted = new SimpleStringProperty(lastCompleted == null ? "" : Utils.DATE_WITH_SECONDS_SDF.format(lastCompleted));
		this.count = new SimpleIntegerProperty(messageCount);
	}
	
	public SimpleStringProperty nameProperty()
	{
		return this.name;
	}
	
	public ScriptRunningState getState()
	{
		return this.state;
	}
	
	public SimpleStringProperty lastCompletedProperty()
	{
		return this.lastCompleted;
	}
	
	public SimpleIntegerProperty countProperty()
	{
		return this.count;
	}
	
	public void setCount(final int messageCount)
	{
		this.count.set(messageCount);
	}
	
	public void setLastCompleted(final Date lastCompleted)
	{
		this.lastCompleted.set(Utils.DATE_WITH_SECONDS_SDF.format(lastCompleted));
	}
	
	public void setState(final ScriptRunningState state)
	{
		this.state = state;
	}
	
	public File getFile()
	{
		return this.file;
	}
}
