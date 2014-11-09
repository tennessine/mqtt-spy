package pl.baczkowicz.mqttspy.exceptions;

/**
 * Represents an exception during loading - very bad.
 * 
 * @author Kamil Baczkowicz
 */
public class CriticalException extends RuntimeException
{
	/** serialVersionUID. */
	private static final long serialVersionUID = 5880158442069517297L;

	public CriticalException(String error)
	{
		super(error);
	}
	
	public CriticalException(String error, Throwable e)
	{
		super(error, e);
	}
}
