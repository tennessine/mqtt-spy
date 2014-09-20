package pl.baczkowicz.mqttspy.xml;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import pl.baczkowicz.mqttspy.configuration.ConfigurationManager;
import pl.baczkowicz.mqttspy.exceptions.XMLException;

public class XMLParserTest
{
	private XMLParser parser;
	
	@Before
	public void setUp() throws Exception
	{
		parser = new XMLParser(ConfigurationManager.SCHEMA, ConfigurationManager.PACKAGE);
	}

	// @Ignore
	// @Test
	// public final void testLoadFromInputStream()
	// {
	// fail("Not yet implemented"); // TODO
	// }

	@Test
	(expected = FileNotFoundException.class)
	public final void testLoadFromFile() throws XMLException, FileNotFoundException
	{
		parser.loadFromFile(new File("/invalidpath/test.txt"));
	}

	// @Ignore
	// @Test
	// public final void testSaveToFile()
	// {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Ignore
	// @Test
	// public final void testGetMarshaller()
	// {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Ignore
	// @Test
	// public final void testGetUnmarshaller()
	// {
	// fail("Not yet implemented"); // TODO
	// }
	//
	// @Ignore
	// @Test
	// public final void testCreateSchema()
	// {
	// fail("Not yet implemented"); // TODO
	// }

}
