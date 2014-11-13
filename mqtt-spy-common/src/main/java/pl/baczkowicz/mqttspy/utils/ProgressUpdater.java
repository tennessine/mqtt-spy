package pl.baczkowicz.mqttspy.utils;

public interface ProgressUpdater
{
	void update(final long current, final long max);
}
