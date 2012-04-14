package org.jblocks.spriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jblocks.spriter.model.Animation;
import org.jblocks.spriter.model.Frame;
import org.jblocks.spriter.model.KeyFrame;
import org.jblocks.spriter.model.SCML;
import org.jblocks.spriter.model.Sprite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * static class which contains methods for reading Spriter Files
 * @author TRocket
 *
 */
public class SCMLReader {
	/**
	 * 
	 * @param in the file to read(as an InputStream)
	 * @throws SAXException due to XML parse error
	 * @throws IOException if file can't be read
	 * @throws ParserConfigurationException if there is a serious configuration problem with the parser
	 */
	public static SCML readSCMLFile(InputStream in) throws SAXException, IOException, ParserConfigurationException{
		return readXML(in);
	}
	/**
	 * 
	 * @param in the file to read(as an InputStream)
	 * @throws SAXException due to XML parse error
	 * @throws IOException if file can't be read
	 * @throws ParserConfigurationException if there is a serious configuration problem with the parser
	 */
	private static SCML readXML(InputStream in) throws SAXException, IOException, ParserConfigurationException{
		String docRootName;
		NodeList characters;
		try {
			//make a document builder and parse the xml
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		docBuilder = docBuildFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(in);
		doc.getDocumentElement ().normalize ();
	    //find the root element name-should be spriterdata
		docRootName = doc.getDocumentElement().getNodeName();
	    //if it isn't throw an exeption
		if(!docRootName.equals("spriterdata")){
	    	throw new IllegalArgumentException("file is not spriter SCML format");
	    }
		
		Vector<org.jblocks.spriter.model.Character> currentCharacters = new Vector<org.jblocks.spriter.model.Character>();
		Vector<Frame> frames = new Vector<Frame>();
		
		//find all the charecters
	    characters = doc.getElementsByTagName("char");
	    //loop over them
	    for (int i = 0; i < characters.getLength(); i++) {
		    org.jblocks.spriter.model.Character currentCharacter;//the current Character ready to be assebled
		    Vector<Animation> currentCharacterAnimations = new Vector<Animation>();		    //the current charecter's animations
		    String currentCharacterName = ((Element)((Element)characters.item(i)).getElementsByTagName("name").item(0)).getFirstChild().getNodeValue();		    //find the name of the current character
		   
		    for (int j = 0; j < ((Element)characters.item(i)).getElementsByTagName("anim").getLength(); j++) {		    //loop over all of this character's animations
		    	Element currentAnimationElement = (Element) ((Element)characters.item(i)).getElementsByTagName("anim").item(j);		    	//the current element having data taken from
		    	 String currentAnimationName = currentAnimationElement.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();		    	//find the name of the current animation
				Vector<KeyFrame> currentAnimationKeyFrames = new Vector<KeyFrame>();
				
				for (int k = 0; k < currentAnimationElement.getElementsByTagName("frame").getLength(); k++) {				//loop over all of this animation's keyFrames
					KeyFrame currentKeyFrame;
					Element currentKeyFrameElement = (Element) currentAnimationElement.getElementsByTagName("frame").item(j);			    	//the current element having data taken from
			    	String currentkeyFrameName = currentKeyFrameElement.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();					//find the current keyFrame's name
			    	float currentkeyFrameDuration = Float.parseFloat(currentAnimationElement.getElementsByTagName("duration").item(0).getFirstChild().getNodeValue());			    	//find the Duration of the current keyFrame
			    	currentKeyFrame = new KeyFrame(currentkeyFrameName, currentkeyFrameDuration);			    	//make a new KeyFrame
			    	currentAnimationKeyFrames.add(currentKeyFrame);
				}
				Animation currentAnimation = new Animation(currentAnimationName, currentAnimationKeyFrames);
				currentCharacterAnimations.add(currentAnimation);
			}
		    currentCharacter = new org.jblocks.spriter.model.Character(currentCharacterName, currentCharacterAnimations);
		    currentCharacters.add(currentCharacter);
		    
		    
		}
	    NodeList framesNodeList;
	    framesNodeList = doc.getElementsByTagName("frame");
	    
	    for (int i = 0; i < framesNodeList.getLength(); i++) {
	    	Node currentFrameNode = framesNodeList.item(i);
			Frame currentFrame;
			String currentFrameName = getValue(currentFrameNode, "name");
			NodeList currentFrameSpritesNodes = ((Element)currentFrameNode).getElementsByTagName("sprite");
			Vector<Sprite> currentFrameSprites = new Vector<Sprite>();
			for (int j = 0; j < currentFrameSpritesNodes.getLength(); j++) {
				Node currentSpriteNode = currentFrameSpritesNodes.item(j);
				Sprite currentSprite;
				
				String currentSpriteImage = getValue(currentSpriteNode, "image");
				int curruntSpriteColor = Integer.parseInt(getValue(currentSpriteNode, "color"));
				float currentSpriteopacity = Float.parseFloat(getValue(currentSpriteNode, "opacity"));
				float currentSpriteAngle = Float.parseFloat(getValue(currentSpriteNode, "angle"));
				boolean currentSpriteXFlip = Boolean.parseBoolean(getValue(currentSpriteNode, "xflip"));
				boolean currentSpriteYFlip = Boolean.parseBoolean(getValue(currentSpriteNode, "yflip"));
				float currentSpriteWidth = Float.parseFloat(getValue(currentSpriteNode, "width"));
				float currentSpriteHeight = Float.parseFloat(getValue(currentSpriteNode, "height"));
				float currentSpriteX = Float.parseFloat(getValue(currentSpriteNode, "x"));
				float currentSpriteY = Float.parseFloat(getValue(currentSpriteNode, "y"));
				currentSprite = new Sprite(currentSpriteImage, curruntSpriteColor, currentSpriteopacity, currentSpriteAngle, currentSpriteXFlip, currentSpriteYFlip, currentSpriteWidth, currentSpriteHeight, currentSpriteX, currentSpriteY);
				currentFrameSprites.add(currentSprite);
			}
			currentFrame = new Frame(currentFrameName, currentFrameSprites);
			frames.add(currentFrame);
		}
	    return new SCML(currentCharacters, frames);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		
		
	}
		private static String getValue(Node node, String name){
			Element element = (Element) node;
			NodeList elements = element.getElementsByTagName(name);
			Node item =	elements.item(0);
			String value = item.getFirstChild().getNodeValue();
			return value;
		}
		
	
	
	
}
