package pl.baczkowicz.mqttspy.scripts;

import java.util.concurrent.Executor;

public class SimpleExecutor implements Executor
{
	public void execute(final Runnable runnable)
	{
		new Thread(runnable).start();
	}	 
}
