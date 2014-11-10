package pl.baczkowicz.mqttspy.exceptions;

/**
 * Represents a base exception.
 * 
 * @author Kamil Baczkowicz
 */
public class MqttSpyException extends Exception
{
	/** Generated serialVersionUID */
	private static final long serialVersionUID = -1041373917140441043L;

	public MqttSpyException(String error)
	{
		super(error);
	}
	
	public MqttSpyException(String error, Throwable e)
	{
		super(error, e);
	}
}
