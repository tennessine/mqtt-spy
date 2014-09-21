package pl.baczkowicz.mqttspy;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttSpyUncaughtExceptionHandler implements UncaughtExceptionHandler
{
	private final static Logger logger = LoggerFactory.getLogger(MqttSpyUncaughtExceptionHandler.class);
	
	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		logger.error("Thread " + t + " failed with " + e, e);
	}
}
