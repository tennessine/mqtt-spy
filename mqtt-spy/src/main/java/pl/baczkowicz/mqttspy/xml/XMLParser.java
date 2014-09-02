package pl.baczkowicz.mqttspy.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * 
 * Handles XML marshalling and unmarshalling.
 * 
 * @author Kamil Baczkowicz
 *
 */
public class XMLParser
{	
	private final Marshaller marshaller;
	
	private final Unmarshaller unmarshaller;
	
	public XMLParser(final String schema, final String namespace) throws XMLException
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(namespace);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(createSchema(schema));
			unmarshaller = jc.createUnmarshaller();
			unmarshaller.setSchema(createSchema(schema));
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + namespace, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Object loadFromInputStream(final InputStream inputStream) throws XMLException
	{
		Object readObject = null;
		try
		{
			readObject = unmarshaller.unmarshal(inputStream);
			if (readObject instanceof JAXBElement)
			{
				readObject = ((JAXBElement) readObject).getValue();
			}			
		}
		catch (JAXBException | IllegalArgumentException e)
		{
			throw new XMLException("Cannot load the XML from stream ", e);
		}

		return readObject;
	}
	
	@SuppressWarnings("rawtypes")
	public Object loadFromFile(final File file) throws XMLException
	{
		Object readObject = null;
		try
		{
			readObject = unmarshaller.unmarshal(file);
			if (readObject instanceof JAXBElement)
			{
				readObject = ((JAXBElement) readObject).getValue();
			}			
		}
		catch (JAXBException | IllegalArgumentException e)
		{
			throw new XMLException("Cannot load the configuration from " + file.getPath(), e);
		}

		return readObject;
	}

	public void saveToFile(final String filename, final Object objectToSave) throws XMLException
	{
		try
		{
			marshaller.marshal(objectToSave, new File(filename));
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot write the XML to " + filename, e);
		}
	}

	public Marshaller getMarshaller()
	{
		return marshaller;
	}

	public Unmarshaller getUnmarshaller()
	{
		return unmarshaller;
	}

	/**
     * Creates the schema object.
     * 
     * @param schemaLocation Location of the XML schema file
     * @return Instance of the Schema object, based on the supplied XSD file
     * @throws XMLException Thrown when cannot create the schema object
     */
    public static Schema createSchema(final String schemaLocation) throws XMLException
    {
        Schema schema = null;
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try
        {
            final File file = new File(schemaLocation);
            if (file.exists())
            {
                schema = schemaFactory.newSchema(file);
            }
            else
            {
                final InputStream resourceAsStream = XMLParser.class.getResourceAsStream(schemaLocation);
                if (resourceAsStream == null)
                {
                    throw new XMLException("Cannot load the schema from file or classpath. Fix the schema or amend the location: " + schemaLocation);
                }

                schema = schemaFactory.newSchema(new StreamSource(resourceAsStream));
            }

            return schema;
        }
        catch (SAXException e)
        {
            throw new XMLException("Cannot set the schema. Please fix the schema or the location", e);
        }
    }
}
