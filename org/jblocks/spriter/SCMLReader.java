package org.jblocks.spriter;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * static class which contains methods for reading Spriter Files
 * @author TRocket
 *
 */
public class SCMLReader {

	public static void readSCMLFile(){
		readXML(new File(SCMLReader.class.getResource("sample.SCML").getFile()));
	}
	
	private static void readXML(File file){
		String docRootName;
		NodeList characters;
		try {
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		docBuilder = docBuildFactory.newDocumentBuilder();
		
		
		Document doc = docBuilder.parse(file);
		doc.getDocumentElement ().normalize ();
	    docRootName = doc.getDocumentElement().getNodeName();
	    if(!docRootName.equals("spriterdata")){
	    	throw new IllegalArgumentException("file is not spriter SCML format");
	    }
	    Character currentCharacter;
		Vector<Character> currentCharacters;
	    characters = doc.getElementsByTagName("char");
	    for (int i = 0; i < characters.getLength(); i++) {
			
		}
	    
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
