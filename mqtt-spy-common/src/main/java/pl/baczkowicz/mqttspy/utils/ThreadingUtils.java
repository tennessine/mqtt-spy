package pl.baczkowicz.mqttspy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadingUtils
{
	public final static Logger logger = LoggerFactory.getLogger(ThreadingUtils.class);
	
	public static final String STARTING_THREAD = "Starting %s thread...";
	
	public static final String ENDING_THREAD = "Ending %s thread...";	 
	
	public static void logStarting()
	{
		if (logger.isTraceEnabled())
		{
			logger.trace(String.format(ThreadingUtils.STARTING_THREAD, Thread.currentThread().getName()));
		}
	}
	
	public static void logEnding()
	{
		if (logger.isTraceEnabled())
		{
			logger.trace(String.format(ThreadingUtils.ENDING_THREAD, Thread.currentThread().getName()));
		}
	}
}
