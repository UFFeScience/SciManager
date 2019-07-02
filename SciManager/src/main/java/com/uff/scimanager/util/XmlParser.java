package com.uff.scimanager.util;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Throwables;
import com.uff.scimanager.exception.InvalidEntityException;

public class XmlParser {

	private static final Logger log = LoggerFactory.getLogger(XmlParser.class);
	
	public static String getXmlAttributeValue(String attributeName, String tagName, String xmlContent) throws InvalidEntityException {
		Document xmlDocument = createXmlFile(xmlContent);
		
		if (xmlDocument != null) {
			Node node = getXmlNodeValue(tagName, xmlDocument);
			
			if (node != null) {
				return getXmlAttributeValue(attributeName, node);
			}
		}
		
		return null;
	}
	
	public static String getXmlAttributeValue(String attributeName, Node node) throws InvalidEntityException {
		if (node == null || node.getAttributes() == null || node.getAttributes().getNamedItem(attributeName) == null ||
			node.getAttributes().getNamedItem(attributeName).getNodeValue() == null) {
			
			throw new InvalidEntityException("Arquivo modelo deve conter atributo 'workflow_exectag'");
		}
		
		return node.getAttributes().getNamedItem(attributeName).getNodeValue();
	}
	
	public static Node getXmlNodeValue(String tagName, Document xmlDocument) {
		if (xmlDocument != null) {
			Element rootElement = xmlDocument.getDocumentElement();
	        
			NodeList elementsList = rootElement.getElementsByTagName(tagName);
			
			if (elementsList != null && elementsList.getLength() == 1) {
				return elementsList.item(0);
			}
			
	        if (elementsList != null && elementsList.getLength() > 1) {
	        	
	        	for (int i = 0; i < elementsList.getLength(); i++) {
	        		
	        		if (elementsList.item(i).getNodeName().equals(tagName)) {
	        			return elementsList.item(i);
	        		}
	        	}	   
        	}
	    }

		return null;
	}
	
	public static Document createXmlFile(String xmlContent) {
	    try {  
	    	log.info("Começando processo de criação de arquivo xml a partir da string {}", xmlContent);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
	    	DocumentBuilder builder = factory.newDocumentBuilder();  
	        return builder.parse(new InputSource(new StringReader(xmlContent)));  
	    } 
	    catch (Exception e) {  
	    	log.error("Erro criando arquivo xml a partir da string {}\n{}", xmlContent, Throwables.getStackTraceAsString(e));
	    	return null;
	    } 
	}
	
}