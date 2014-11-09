package pl.baczkowicz.mqttspy.ui.utils;

import java.util.concurrent.Executor;

import javafx.application.Platform;

public class RunLaterExecutor implements Executor
{
	@Override
	public void execute(final Runnable command)
	{
		Platform.runLater(command);
	}
}
