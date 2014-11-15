package pl.baczkowicz.mqttspy.ui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.baczkowicz.mqttspy.configuration.generated.ConversionFormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.ConversionMethod;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.FormatterFunction;
import pl.baczkowicz.mqttspy.configuration.generated.SubstringConversionFormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.SubstringExtractFormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.SubstringFormatterDetails;
import pl.baczkowicz.mqttspy.configuration.generated.SubstringReplaceFormatterDetails;
import pl.baczkowicz.mqttspy.exceptions.ConversionException;
import pl.baczkowicz.mqttspy.utils.ConversionUtils;

public class FormattingUtils
{
	final static Logger logger = LoggerFactory.getLogger(FormattingUtils.class);
	

	public static String custom(final FormatterDetails customFormatter, final String text)
	{
		logger.trace("Formatting '" + text + "' with " + customFormatter.getName());
		String formattedText = text;

		for (final FormatterFunction function : customFormatter.getFunction())
		{
			if (function.getSubstringReplace() != null)
			{
				formattedText = doSubstringReplacement(function.getSubstringReplace(), formattedText);
			}
			else if (function.getSubstringExtract() != null)
			{
				formattedText = doSubstringExtract(function.getSubstringExtract(), formattedText);
			}
			else if (function.getSubstringConversion() != null)
			{
				formattedText = doSubstringConversion(function.getSubstringConversion(), formattedText);
			}
			else if (function.getConversion() != null)
			{
				formattedText = convert(function.getConversion().getFormat(), formattedText);
			}
			else if (function.getCharacterReplace() != null)
			{
				formattedText = replaceCharacters(function.getCharacterReplace().getFormat(), formattedText, 
							function.getCharacterReplace().getCharacterRangeFrom(), function.getCharacterReplace().getCharacterRangeTo(), 
							function.getCharacterReplace().getWrapCharacter());				
			}
				
			
			logger.trace("After function transformation = '" + formattedText + "'");
		}

		return formattedText;
	}
	
	public static String replaceCharacters(final ConversionMethod conversionMethod, final String input, final int fromCharacter, final int toCharacter, final String wrap)
	{
		String convertedText = input;
		
		for (int i = fromCharacter; i <= toCharacter; i++)
		{			
			final String characterToReplace = new String(Character.toChars(i));
			
			if (wrap != null)
			{
				convertedText = convertedText.replace(
					characterToReplace, 
					wrap + convert(conversionMethod, characterToReplace) + wrap); 
			}
			else				
			{
				convertedText = convertedText.replace(
						characterToReplace, 
						convert(conversionMethod, characterToReplace));
			}
		}
		
		return convertedText;
	}
	
	private static String extractValueForConversion(final SubstringFormatterDetails details, final String text) throws ConversionException
	{
		final int startTagIndex = text.indexOf(details.getStartTag());
		
		if (startTagIndex != -1)
		{
			final int endTagIndex = text.indexOf(details.getEndTag(), startTagIndex);
			
			if (endTagIndex != -1)
			{
				return text.substring(startTagIndex + details.getStartTag().length(), endTagIndex);				
			}
		}
		
		throw new ConversionException("Cannot find tags");
	}
	
	private static String checkTags(final SubstringFormatterDetails details, final String text, final String input, final String output)
	{
		String convertedText = text;
		
		if (details.isKeepTags())
		{
			convertedText = convertedText.replace(input, output); 
		}
		else
		{
			convertedText = convertedText.replace(details.getStartTag() + input + details.getEndTag(), output); 
		}
		
		return convertedText;		
	}
	
	private static String doSubstringConversion(final SubstringConversionFormatterDetails details, final String text)
	{
		String convertedText = text;
		
		try
		{
			final String input = extractValueForConversion(details, convertedText);
			
			// The actual conversion value
			final String output = convert(details.getFormat(), input);
			
			convertedText = checkTags(details, convertedText, input, output);
		}
		catch (ConversionException e)
		{
			// Ignore, just use the input text as output
		}
				
		return convertedText;
	}
	
	private static String doSubstringReplacement(final SubstringReplaceFormatterDetails details, final String text)
	{
		String convertedText = text;
		
		try
		{
			final String input = extractValueForConversion(details, convertedText);
			
			// The actual replacement value
			final String output = details.getReplaceWith();
			
			convertedText = checkTags(details, convertedText, input, output);
		}
		catch (ConversionException e)
		{
			// Ignore, just use the input text as output
		}
				
		return convertedText;	
	}
	
	private static String doSubstringExtract(final SubstringExtractFormatterDetails details, final String text)
	{
		String convertedText = text;
		
		try
		{
			final String input = extractValueForConversion(details, convertedText);
						
			if (details.isKeepTags())
			{
				convertedText = details.getStartTag() + input + details.getEndTag();
			}
			else
			{
				convertedText = input; 
			}
		}
		catch (ConversionException e)
		{
			// Ignore, just use the input text as output
		}
				
		return convertedText;	
	}
	
	public static String convert(final ConversionMethod method, final String text)
	{
		switch (method)
		{
			case PLAIN:
			{
				return text;
			}
			case HEX_ENCODE:
			{
				return ConversionUtils.stringToHex(text);
			}
			case HEX_DECODE:
			{
				return ConversionUtils.hexToStringNoException(text);
			}
			case BASE_64_ENCODE:
			{
				return ConversionUtils.stringToBase64(text);
			}
			case BASE_64_DECODE:
			{
				return ConversionUtils.base64ToString(text);
			}		
			default:
				return text;
		}
	}
	

	public static String convertText(final FormatterDetails format, final String text)
	{		
		if (format != null)
		{
			return FormattingUtils.custom(format, text);
		}
		return text;
	}
	
	
	private static FormatterFunction createBasicFormatterFunction(final ConversionMethod conversionMethod)	
	{
		final FormatterFunction function = new FormatterFunction();
		
		final ConversionFormatterDetails conversionFormatterDetails = new ConversionFormatterDetails();
		conversionFormatterDetails.setFormat(conversionMethod);
		
		function.setConversion(conversionFormatterDetails);
		
		return function;
	}
	
	public static FormatterDetails createBasicFormatter(final String id, final String name, final ConversionMethod conversionMethod)	
	{
		final FormatterDetails formatter = new FormatterDetails();
		
		formatter.setID(id);
		formatter.setName(name);		
		formatter.getFunction().add(createBasicFormatterFunction(conversionMethod));
		
		return formatter;
	}
}
