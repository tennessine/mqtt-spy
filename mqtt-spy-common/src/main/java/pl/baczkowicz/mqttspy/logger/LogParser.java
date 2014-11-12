package pl.baczkowicz.mqttspy.logger;

import pl.baczkowicz.mqttspy.common.generated.LoggedMqttMessage;
import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.xml.XMLParser;

public class LogParser extends XMLParser
{
	public LogParser() throws XMLException
	{
		super(LoggedMqttMessage.class);
	}
	
	public LoggedMqttMessage parse(final String xmlMessage) throws XMLException
	{
		return (LoggedMqttMessage) super.unmarshal(xmlMessage, LoggedMqttMessage.class);
	}
}
