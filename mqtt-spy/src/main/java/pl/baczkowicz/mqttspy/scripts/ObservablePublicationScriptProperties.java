package pl.baczkowicz.mqttspy.scripts;

import java.util.Date;

import pl.baczkowicz.mqttspy.common.generated.ScriptDetails;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class ObservablePublicationScriptProperties extends PublicationScriptProperties
{
	private SimpleObjectProperty<ScriptRunningState> statusProperty;
	
	private SimpleObjectProperty<ScriptTypeEnum> typeProperty;

	private SimpleStringProperty lastPublishedProperty;

	private SimpleLongProperty countProperty;
	
	private SimpleBooleanProperty repeatProperty;
	
	public ObservablePublicationScriptProperties()
	{
		super();
		
		this.statusProperty = new SimpleObjectProperty<ScriptRunningState>(ScriptRunningState.NOT_STARTED);		
		this.typeProperty = new SimpleObjectProperty<ScriptTypeEnum>(ScriptTypeEnum.PUBLICATION);
		this.lastPublishedProperty = new SimpleStringProperty("");
		this.countProperty = new SimpleLongProperty(0);
		this.repeatProperty = new SimpleBooleanProperty(false);
	}
	
//	public ObservablePublicationScriptProperties(final PublicationScriptProperties properties)
//	{
//		this(properties.getName(), properties.getFile(), properties.getStatus(), properties.getLastPublishedDate(), 
//				properties.getCount(), properties.getScriptEngine(), properties.isRepeat());
//		
//		super.setPublicationScriptIO(properties.getPublicationScriptIO());
//		super.setThread(properties.getThread());
//		super.setScriptTimeout(properties.getScriptTimeout());
//	}
	
//	public ObservablePublicationScriptProperties(final String name, final File file, final ScriptRunningState state, final Date lastPublished, 
//			final long messageCount, final ScriptEngine scriptEngine, final boolean repeat)
//	{
//		super(name, file, state, lastPublished, messageCount, scriptEngine, repeat);
////		this.name = new SimpleStringProperty(name);
//		
//		this.statusProperty = new SimpleObjectProperty<ScriptRunningState>(getStatus());		
//		this.lastPublishedProperty = new SimpleStringProperty(getLastPublished());
//		this.countProperty = new SimpleLongProperty(getCount());
//	}
	
//	public SimpleStringProperty nameProperty()
//	{
//		return this.name;
//	}
//	
	public SimpleObjectProperty<ScriptRunningState> statusProperty()
	{
		return this.statusProperty;
	}
	
	public SimpleObjectProperty<ScriptTypeEnum> typeProperty()
	{
		return this.typeProperty;
	}
	
	public SimpleStringProperty lastPublishedProperty()
	{
		return this.lastPublishedProperty;
	}
	
	public SimpleLongProperty countProperty()
	{
		return this.countProperty;
	}
	
	public SimpleBooleanProperty repeatProperty()
	{
		return this.repeatProperty;
	}
	
	public boolean isRepeat()
	{
		return this.repeatProperty.getValue();
	}
	
	public void setDetails(final ScriptDetails scriptDetails)
	{
		super.setDetails(scriptDetails);
		this.repeatProperty.set(scriptDetails.isRepeat());		
	}
	
	public void setCount(final long messageCount)
	{
		super.setCount(messageCount);
		this.countProperty.set(getCount());
	}

	public void setLastPublishedDate(final Date lastPublished)
	{
		super.setLastPublishedDate(lastPublished);	
		this.lastPublishedProperty.set(getLastPublished());
	}
	
	public void setStatus(final ScriptRunningState state)
	{
		super.setStatus(state);
		this.statusProperty().set(getStatus());
	}
}
