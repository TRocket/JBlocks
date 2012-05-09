package org.jblocks.spriter;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * XML Spriter-Format reading. <br />
 * 
 * @author ZeroLuck
 */
class SpriterParser {
    
    static interface RessourceManager {

        public byte[] read(String path)
                throws IOException;
    }

    static class DefaultRessourceManager implements RessourceManager {

        private final String rel;

        public DefaultRessourceManager(String rel) {
            this.rel = rel.endsWith("\\") || rel.endsWith("/")
                    ? rel : rel + System.getProperty("file.separator");
        }

        @Override
        public byte[] read(String path) throws IOException {
            File f = new File(rel + path);
            byte[] data = new byte[(int) f.length()];
            int off = 0;
            FileInputStream in = new FileInputStream(f);
            while (off < data.length) {
                off += in.read(data, off, data.length - off);
            }
            in.close();
            return data;
        }
    }

    private static String getNameForNode(Node n) {
        return getChildText(n, "name");
    }

    private static String getChildText(Node n, String name) {
        return getChild(n, name).getTextContent();
    }

    private static Node getChild(Node n, String name) {
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                return child;
            }
        }
        return null;
    }

    private static NodeList getChildrenByTagName(Node n, String name) {
        final List<Node> nodes = new ArrayList<Node>();
        NodeList children = n.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                nodes.add(child);
            }
        }

        return new NodeList() {

            @Override
            public Node item(int i) {
                return nodes.get(i);
            }

            @Override
            public int getLength() {
                return nodes.size();
            }
        };
    }

    private static void checkFormat(Node n) {
        String name = n.getNodeName();
        if (!name.equalsIgnoreCase("spriterdata")) {
            throw new IllegalArgumentException("Not a SCML file.");
        }
    }

    private static SpriteData parseSprite(Node spriteNode, Image img) {
        final int currentSpriteColor = Integer.parseInt(getChildText(spriteNode, "color"));
        final float currentSpriteopacity = Float.parseFloat(getChildText(spriteNode, "opacity"));
        final float currentSpriteAngle = Float.parseFloat(getChildText(spriteNode, "angle"));
        final boolean currentSpriteXFlip = Boolean.parseBoolean(getChildText(spriteNode, "xflip"));
        final boolean currentSpriteYFlip = Boolean.parseBoolean(getChildText(spriteNode, "yflip"));
        final float currentSpriteWidth = Float.parseFloat(getChildText(spriteNode, "width"));
        final float currentSpriteHeight = Float.parseFloat(getChildText(spriteNode, "height"));
        final float currentSpriteX = Float.parseFloat(getChildText(spriteNode, "x"));
        final float currentSpriteY = Float.parseFloat(getChildText(spriteNode, "y"));

        return new SpriteData(img,
                currentSpriteColor, currentSpriteopacity,
                currentSpriteAngle, currentSpriteXFlip,
                currentSpriteYFlip, currentSpriteWidth,
                currentSpriteHeight, currentSpriteX,
                currentSpriteY);
    }

    public static CharacterData[] read(InputStream in, RessourceManager mng) throws IOException {
        try {
            final DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(in);
            final Node root = doc.getDocumentElement();
            checkFormat(root);

            final NodeList frameNodes = getChildrenByTagName(root, "frame");
            final FrameData[] frames = new FrameData[frameNodes.getLength()];
            final Map<String, Image> imageCache = new HashMap<String, Image>(100);
            final Map<String, FrameData> frameCache = new HashMap<String, FrameData>(100);

            for (int i = 0; i < frames.length; i++) {
                final Node frameNode = frameNodes.item(i);
                final String frameName = getNameForNode(frameNode);
                final NodeList currentFrameSpritesNodes = getChildrenByTagName(frameNode, "sprite");
                final SpriteData[] sprites = new SpriteData[currentFrameSpritesNodes.getLength()];
                for (int j = 0; j < sprites.length; j++) {
                    final Node spriteNode = currentFrameSpritesNodes.item(j);
                    final String imageName = getChildText(spriteNode, "image");
                    final Image image = imageCache.containsKey(imageName)
                            ? imageCache.get(imageName) : new ImageIcon(mng.read(imageName)).getImage();

                    sprites[j] = parseSprite(spriteNode, image);
                }
                frameCache.put(frameName, frames[i] = new FrameData(frameName, sprites));
            }

            final NodeList characterNodes = getChildrenByTagName(root, "char");
            final CharacterData[] characters = new CharacterData[characterNodes.getLength()];

            for (int i = 0; i < characters.length; i++) {
                final Node charNode = characterNodes.item(i);
                final NodeList animationNodes = getChildrenByTagName(charNode, "anim");
                final String charName = getNameForNode(charNode);
                final AnimationData[] animations = new AnimationData[animationNodes.getLength()];

                for (int j = 0; j < animations.length; j++) {
                    final Node animNode = animationNodes.item(j);
                    final String animName = getNameForNode(animNode);
                    final NodeList keyFrameNodes = getChildrenByTagName(animNode, "frame");
                    final KeyFrameData[] keyFrames = new KeyFrameData[keyFrameNodes.getLength()];

                    for (int k = 0; k < keyFrames.length; k++) {
                        final Node keyFrameNode = keyFrameNodes.item(k);
                        final String keyFrameName = getNameForNode(keyFrameNode);
                        final float keyFrameDuration = Float.valueOf(getChild(keyFrameNode, "duration").getTextContent());
                        keyFrames[k] = new KeyFrameData(keyFrameDuration, frameCache.get(keyFrameName));
                    }
                    animations[j] = new AnimationData(animName, keyFrames);
                }
                characters[i] = new CharacterData(charName, animations);
            }

            return characters;
        } catch (ParserConfigurationException ex) {
            throw new IOException("Couldn't read the character.", ex);
        } catch (SAXException ex) {
            throw new IOException("Couldn't read the character.", ex);
        }
    }
}
