package com.currency.utils;

import java.io.StringReader;
import java.io.StringWriter;

import org.jboss.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XmlUtils {
    
    private static final Logger LOG = Logger.getLogger(XmlUtils.class);
    
    public static <T> String objectToXml(T object, Class<T> clazz) throws JAXBException {
        if (object == null) {
            throw new IllegalArgumentException("Object to marshal cannot be null");
        }
        
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);
            
            String result = writer.toString();
            LOG.debugf("Successfully marshalled object to XML: %s", result);
            return result;
            
        } catch (JAXBException e) {
            LOG.errorf("Failed to marshal object of type %s: %s", clazz.getSimpleName(), e.getMessage());
            throw e;
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T xmlToObject(String xml, Class<T> clazz) throws JAXBException {
        if (xml == null || xml.trim().isEmpty()) {
            throw new IllegalArgumentException("XML string cannot be null or empty");
        }
        
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            T result = (T) unmarshaller.unmarshal(new StringReader(xml));
            LOG.debugf("Successfully unmarshalled XML to object of type %s", clazz.getSimpleName());
            return result;
            
        } catch (JAXBException e) {
            LOG.errorf("Failed to unmarshal XML to type %s. XML content: %s. Error: %s", 
                      clazz.getSimpleName(), xml, e.getMessage());
            throw e;
        }
    }
}