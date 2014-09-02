package pl.baczkowicz.mqttspy.ui.format;

/**
 * Represents an exception during text conversion.
 * 
 * @author Kamil Baczkowicz
 */
public class ConversionException extends Exception
{
	/** serialVersionUID. */
	private static final long serialVersionUID = 5880158442069517297L;

	public ConversionException(String error)
	{
		super(error);
	}
	
	public ConversionException(String error, Throwable e)
	{
		super(error, e);
	}
}