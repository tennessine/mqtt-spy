package pl.baczkowicz.mqttspy.configuration;

import pl.baczkowicz.mqttspy.xml.XMLException;

/**
 * Represents an exception during configuration loading.
 * 
 * @author Kamil Baczkowicz
 */
public class ConfigurationException extends XMLException
{
	/** serialVersionUID. */
	private static final long serialVersionUID = 5880158442069517297L;

	public ConfigurationException(String error)
	{
		super(error);
	}
	
	public ConfigurationException(String error, Throwable e)
	{
		super(error, e);
	}
}
