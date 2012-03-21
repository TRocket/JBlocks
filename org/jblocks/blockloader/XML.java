package org.jblocks.blockloader;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * a class which reads block.xml files
 * @author TRocket
 *
 */
public class XML {
private String docRootName;
private String type;
private Node block;
private String classname;
/**
 * 
 * @param file
 */
	public XML(InputStream file){
	try{
	        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse(file);

	        // normalize text representation
	        doc.getDocumentElement ().normalize ();
	        docRootName = doc.getDocumentElement().getNodeName();


	        block = doc.getElementsByTagName("block").item(0);
	        type = block.getAttributes().getNamedItem("type").getNodeValue();
	        classname = block.getAttributes().getNamedItem("class").getNodeValue();



	        
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	}

	/**
	 * @return the docRootName
	 */
	public String getDocRootName() {
		return docRootName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the block
	 */
	public Node getBlock() {
		return block;
	}

	/**
	 * @return the classname
	 */
	public String getClassname() {
		return classname;
	}


}
