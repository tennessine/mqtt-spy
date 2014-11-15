package pl.baczkowicz.mqttspy.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import pl.baczkowicz.mqttspy.exceptions.ConversionException;

public class ConversionUtils
{

	public static String stringToHex(final String data)
	{
		return new String(Hex.encodeHex(data.getBytes()));
	}
	
	public static String hexToString(final String data) throws ConversionException
	{
		try
		{
			return new String(Hex.decodeHex(data.toCharArray()));
		}
		catch (DecoderException e)
		{
			throw new ConversionException("Cannot convert given hex text into plain text", e);
		}
	}
	
	public static String hexToStringNoException(final String data)
	{
		try
		{
			return new String(Hex.decodeHex(data.toCharArray()));
		}
		catch (DecoderException e)
		{
			return "[invalid hex]";
		}
	}

	public static String base64ToString(final String data)
	{
		return new String(Base64.decodeBase64(data));
	}
	
	public static String stringToBase64(final String data)
	{
		return Base64.encodeBase64String(data.getBytes());
	}
}
