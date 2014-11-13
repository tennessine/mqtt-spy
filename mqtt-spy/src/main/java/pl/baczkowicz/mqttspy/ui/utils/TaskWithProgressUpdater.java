package pl.baczkowicz.mqttspy.ui.utils;

import pl.baczkowicz.mqttspy.utils.ProgressUpdater;
import javafx.concurrent.Task;

public abstract class TaskWithProgressUpdater<T> extends Task<T> implements ProgressUpdater
{
	@Override
	public void update(long current, long max)
	{
		super.updateProgress(current, max);		
	}
}
