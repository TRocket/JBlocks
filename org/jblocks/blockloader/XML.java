package org.jblocks.blockloader;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
private Dependency[] dependicies;
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
	        try{
	        NodeList dependenciesNodes = doc.getElementsByTagName("dependencies").item(0).getChildNodes();
	        this.dependicies = new Dependency[dependenciesNodes.getLength()];
	        for (int i = 0; i < dependenciesNodes.getLength(); i++) {
	        	Node dependencyNode = dependenciesNodes.item(i);
				this.dependicies[i] = new Dependency(dependencyNode.getAttributes().getNamedItem("class").getNodeValue(), dependencyNode.getAttributes().getNamedItem("name").getNodeValue());
			}
	        }catch (Exception e) {
				// do nothing
			}



	        
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

	/**
	 * @return the dependicies
	 */
	public Dependency[] getDependicies() {
		return dependicies;
	}


}
