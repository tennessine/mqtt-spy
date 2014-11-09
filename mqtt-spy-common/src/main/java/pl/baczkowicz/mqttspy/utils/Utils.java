package pl.baczkowicz.mqttspy.utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class Utils
{
	public final static String WITH_MILLISECONDS_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS";
	
	public final static String WITH_SECONDS_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	public final static String DATE_ONLY_FORMAT = "yyyy/MM/dd";

	public final static SimpleDateFormat DATE_WITH_MILLISECONDS_SDF = new SimpleDateFormat(WITH_MILLISECONDS_DATE_FORMAT);
	
	public final static SimpleDateFormat DATE_WITH_SECONDS_SDF = new SimpleDateFormat(WITH_SECONDS_DATE_FORMAT);
	
	public final static SimpleDateFormat DATE_SDF = new SimpleDateFormat(DATE_ONLY_FORMAT);
	
	public static boolean recordTopic(final String newTopic, final List<String> topics)
	{
		for (final String topic : topics)
		{
			if (topic.equals(newTopic))
			{
				// If the topic is already on the list, don't add it again
				return false;
			}
		}

		topics.add(newTopic);
		return true;
	}
	
	public static long getMonotonicTimeInMilliseconds()
	{
		return System.nanoTime() / 1000000;
	}
}
