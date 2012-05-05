package org.jblocks.spriter;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Static class which contains methods for reading Spriter Files
 * 
 * @author TRocket
 */
public class SCMLReader {

    private SCMLReader() {
        // don't let anyone make an instance of this class.
    }

    /**
     * 
     * @param in the file to read(as an InputStream)
     * @throws SAXException due to XML parse error
     * @throws IOException if file can't be read
     * @throws ParserConfigurationException if there is a serious configuration problem with the parser
     */
    public static SCML readSCMLFile(InputStream in, String basePath) throws SAXException, IOException, ParserConfigurationException {
        return readXML(in, basePath);
    }

    /**
     * 
     * @param in the file to read(as an InputStream)
     * @throws SAXException due to XML parse error
     * @throws IOException if file can't be read
     * @throws ParserConfigurationException if there is a serious configuration problem with the parser
     */
    private static SCML readXML(final InputStream in, String basePath)
            throws SAXException, IOException, ParserConfigurationException {

        if (!basePath.endsWith("\\") && !basePath.endsWith("/")) {
            basePath += System.getProperty("file.separator");
        }
        final List<String> imageFiles = new ArrayList<String>();
        // make a document builder and parse the xml
        final DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
        final Document doc = docBuilder.parse(in);
        // normalize the document
        doc.normalize();
        // find the root element name-should be spriterdata
        final String docRootName = doc.getDocumentElement().getNodeName();
        // if it isn't throw an exeption
        if (!docRootName.equals("spriterdata")) {
            throw new IllegalArgumentException("file is not spriter SCML format");
        }

        // find all the charecters
        final NodeList characters = doc.getElementsByTagName("char");

        final Character[] currentCharacters = new Character[characters.getLength()];

        // loop over them
        for (int i = 0; i < currentCharacters.length; i++) {

            // the character's xml-elements
            final Element currentCharacterElement = (Element) characters.item(i);
            final NodeList currentCharacterAnimationNodes = currentCharacterElement.getElementsByTagName("anim");

            // find the name of the current character
            final String currentCharacterName = ((Element) currentCharacterElement.getElementsByTagName("name").item(0)).getFirstChild().getNodeValue();

            // the current charecter's animations            
            final Animation[] currentCharacterAnimations = new Animation[currentCharacterAnimationNodes.getLength()];

            // loop over all of this character's animations
            for (int j = 0; j < currentCharacterAnimations.length; j++) {
                // the current element having data taken from
                final Element currentAnimationElement = (Element) currentCharacterAnimationNodes.item(j);

                // the current element having data taken from
                final String currentAnimationName = currentAnimationElement.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();

                // loop over all of this animation's keyFrames
                final NodeList currentAnimationNodes = currentAnimationElement.getElementsByTagName("frame");
                final KeyFrame[] currentAnimationKeyFrames = new KeyFrame[currentAnimationNodes.getLength()];

                for (int k = 0; k < currentAnimationNodes.getLength(); k++) {
                    // the current element having data taken from
                    final Element currentKeyFrameElement = (Element) currentAnimationNodes.item(j);
                    // find the current keyFrame's name
                    final String currentkeyFrameName = currentKeyFrameElement.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                    // find the Duration of the current keyFrame
                    float currentkeyFrameDuration = Float.parseFloat(currentAnimationElement.getElementsByTagName("duration").item(0).getFirstChild().getNodeValue());
                    // make a new KeyFrame
                    currentAnimationKeyFrames[k] = new KeyFrame(currentkeyFrameName, currentkeyFrameDuration);
                }
                currentCharacterAnimations[j] = new Animation(currentAnimationName, currentAnimationKeyFrames);
            }
            currentCharacters[i] = new Character(currentCharacterName, currentCharacterAnimations);
        }
        final NodeList framesNodeList = doc.getElementsByTagName("frame");
        final Frame[] frames = new Frame[framesNodeList.getLength()];

        for (int i = 0; i < frames.length; i++) {
            final Node currentFrameNode = framesNodeList.item(i);
            final String currentFrameName = getValue(currentFrameNode, "name");
            final NodeList currentFrameSpritesNodes = ((Element) currentFrameNode).getElementsByTagName("sprite");
            final Sprite[] currentFrameSprites = new Sprite[currentFrameSpritesNodes.getLength()];
            for (int j = 0; j < currentFrameSprites.length; j++) {
                final Node currentSpriteNode = currentFrameSpritesNodes.item(j);

                String currentSpriteImage = getValue(currentSpriteNode, "image");
                imageFiles.add(currentSpriteImage);
                int currentSpriteColor = Integer.parseInt(getValue(currentSpriteNode, "color"));
                float currentSpriteopacity = Float.parseFloat(getValue(currentSpriteNode, "opacity"));
                float currentSpriteAngle = Float.parseFloat(getValue(currentSpriteNode, "angle"));
                boolean currentSpriteXFlip = Boolean.parseBoolean(getValue(currentSpriteNode, "xflip"));
                boolean currentSpriteYFlip = Boolean.parseBoolean(getValue(currentSpriteNode, "yflip"));
                float currentSpriteWidth = Float.parseFloat(getValue(currentSpriteNode, "width"));
                float currentSpriteHeight = Float.parseFloat(getValue(currentSpriteNode, "height"));
                float currentSpriteX = Float.parseFloat(getValue(currentSpriteNode, "x"));
                float currentSpriteY = Float.parseFloat(getValue(currentSpriteNode, "y"));

                final Sprite currentSprite = new Sprite(currentSpriteImage,
                        currentSpriteColor, currentSpriteopacity,
                        currentSpriteAngle, currentSpriteXFlip,
                        currentSpriteYFlip, currentSpriteWidth,
                        currentSpriteHeight, currentSpriteX,
                        currentSpriteY);

                currentFrameSprites[j] = currentSprite;
            }
            frames[i] = new Frame(currentFrameName, currentFrameSprites);
        }
        return new SCML(currentCharacters, frames, loadImages(imageFiles, basePath));
    }

    private static String getValue(Node node, String name) {
        Element element = (Element) node;
        NodeList elements = element.getElementsByTagName(name);
        Node item = elements.item(0);
        String value = item.getFirstChild().getNodeValue();
        return value;
    }

    private static Map<String, Image> loadImages(List<String> paths, String basePath) throws IOException {
        Map<String, Image> images = new HashMap<String, Image>(50);
        int i = 0;
        for (String p : paths) {
            if (!images.containsKey(p)) {
                Image currentImage = ImageIO.read(new File(basePath + p));
                images.put(p, currentImage);
            }
            i++;
        }
        return images;
    }
}
