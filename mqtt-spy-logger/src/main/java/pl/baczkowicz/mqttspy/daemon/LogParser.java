package pl.baczkowicz.mqttspy.daemon;

import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.logger.generated.LoggedMqttMessage;
import pl.baczkowicz.mqttspy.xml.XMLParser;

public class LogParser extends XMLParser
{
	// private static final String PACKAGE = "pl.baczkowicz.mqttspy.logger.generated";
	
//	private static final String SCHEMA = "/mqtt-spy-logger.xsd";
//	
//	public static final String COMMON_SCHEMA = "/mqtt-spy-common.xsd";
	
	public LogParser() throws XMLException
	{
		super(LoggedMqttMessage.class/*, new String[] {COMMON_SCHEMA, SCHEMA}*/);
	}
	
	public LoggedMqttMessage parse(final String xmlMessage) throws XMLException
	{
		return (LoggedMqttMessage) super.unmarshal(xmlMessage, LoggedMqttMessage.class);
	}
}
