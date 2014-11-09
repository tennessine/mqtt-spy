package pl.baczkowicz.mqttspy.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pl.baczkowicz.mqttspy.exceptions.XMLException;

/**
 * 
 * Handles XML marshalling and unmarshalling.
 * 
 * @author Kamil Baczkowicz
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
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
			marshaller.setSchema(XMLSchemaUtils.createSchema(schema));
			unmarshaller = jc.createUnmarshaller();
			unmarshaller.setSchema(XMLSchemaUtils.createSchema(schema));
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + namespace, e);
		}
	}
	
	public XMLParser(final String schema, final Class classToBeBound) throws XMLException
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(classToBeBound);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(XMLSchemaUtils.createSchema(schema));
			unmarshaller = jc.createUnmarshaller();
			unmarshaller.setSchema(XMLSchemaUtils.createSchema(schema));
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + classToBeBound, e);
		}
	}
	
	public XMLParser(final String namespace, final String[] schemas) throws XMLException
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(namespace);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(XMLSchemaUtils.createSchema(schemas));
			unmarshaller = jc.createUnmarshaller();
			unmarshaller.setSchema(XMLSchemaUtils.createSchema(schemas));
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + namespace, e);
		}
	}
	
	public XMLParser(final Class classToBeBound, final String[] schemas) throws XMLException
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(classToBeBound);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(XMLSchemaUtils.createSchema(schemas));
			unmarshaller = jc.createUnmarshaller();
			unmarshaller.setSchema(XMLSchemaUtils.createSchema(schemas));
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + classToBeBound, e);
		}
	}
	
	public XMLParser(final Class classToBeBound) throws XMLException
	{
		try
		{
			JAXBContext jc = JAXBContext.newInstance(classToBeBound.getPackage().getName());
			marshaller = jc.createMarshaller();
			unmarshaller = jc.createUnmarshaller();
			
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot instantiate marshaller/unmarshaller for " + classToBeBound, e);
		}
	}
	
	public Object unmarshal(final String xml) throws XMLException
	{
		Object readObject = null;
		try
		{
			readObject = unmarshaller.unmarshal(new StreamSource(xml));
			if (readObject instanceof JAXBElement)
			{
				readObject = ((JAXBElement) readObject).getValue();
			}			
		}
		catch (JAXBException e)		
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (IllegalArgumentException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}

		return readObject;
	}
	
	public Object unmarshal(final String xml, final Class rootClass) throws XMLException
	{
		Object readObject = null;
		try
		{
	        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	        final InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));
	 	        
	        readObject = unmarshaller.unmarshal(db.parse(is).getFirstChild(), rootClass);
			if (readObject instanceof JAXBElement)
			{
				readObject = ((JAXBElement) readObject).getValue();
			}			
		}
		catch (JAXBException e)		
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (IllegalArgumentException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (SAXException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (IOException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (ParserConfigurationException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}

		return readObject;
	}

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
		catch (JAXBException e)		
		{
			throw new XMLException("Cannot read the XML ", e);
		}
		catch (IllegalArgumentException e)
		{
			throw new XMLException("Cannot read the XML ", e);
		}

		return readObject;
	}
	
	public Object loadFromFile(final File file) throws XMLException, FileNotFoundException
	{
		if (file == null || !file.exists())
		{
			throw new FileNotFoundException("Cannot load the configuration " + (file != null ? "from " + file.getAbsolutePath() : ""));
		}
		
		Object readObject = null;
		try
		{
			readObject = unmarshaller.unmarshal(file);
			if (readObject instanceof JAXBElement)
			{
				readObject = ((JAXBElement) readObject).getValue();
			}			
		}
		catch (JAXBException e)		
		{
			throw new XMLException("Cannot load the configuration from " + file.getAbsolutePath(), e);
		}
		catch (IllegalArgumentException e)
		{
			throw new XMLException("Cannot load the configuration from " + file.getAbsolutePath(), e);
		}

		return readObject;
	}

	public void saveToFile(final File file, final Object objectToSave) throws XMLException
	{
		try
		{
			marshaller.marshal(objectToSave, file);
		}
		catch (JAXBException e)
		{
			throw new XMLException("Cannot save configuration to " + file.getAbsolutePath(), e);
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
}
