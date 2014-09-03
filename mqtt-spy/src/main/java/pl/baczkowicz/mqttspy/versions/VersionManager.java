package pl.baczkowicz.mqttspy.versions;

import java.io.IOException;
import java.net.URL;

import pl.baczkowicz.mqttspy.exceptions.XMLException;
import pl.baczkowicz.mqttspy.versions.generated.MqttSpyVersions;
import pl.baczkowicz.mqttspy.xml.XMLParser;

/**
 * 
 * Manages loading of the version info.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class VersionManager extends XMLParser
{
	private static final String PACKAGE = "pl.baczkowicz.mqttspy.versions.generated";
	
	private static final String SCHEMA = "/mqtt-spy-versions.xsd";
	
	private static final String VERSION_INFO_URL = "http://baczkowicz.pl/mqtt-spy/version.xml";

	private MqttSpyVersions versions;

	public VersionManager() throws XMLException
	{
		super(SCHEMA, PACKAGE);
					
		this.versions = new MqttSpyVersions();
	}
	
	public MqttSpyVersions loadVersions() throws XMLException
	{
		try
		{
			final URL url = new URL(VERSION_INFO_URL);

			versions = (MqttSpyVersions) loadFromInputStream(url.openStream());			
		}
		catch (IOException e)
		{
			throw new XMLException("Cannot read version info from " + VERSION_INFO_URL, e);
		}
				
		return versions;
	}
	
	public MqttSpyVersions getVersions()
	{
		return versions;
	}

}
