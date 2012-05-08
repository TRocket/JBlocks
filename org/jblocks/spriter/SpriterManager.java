package org.jblocks.spriter;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jblocks.utils.TestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author ZeroLuck
 */
public class SpriterManager {

    public static void main(String[] args) throws Exception {
        final SpriterCharacter c = new SpriterCharacter(
                readCharacters(new FileInputStream("C:\\JTest\\example hero\\BetaFormatHero.scml"),
                new DefaultRessourceManager("C:\\JTest\\example hero"))[0]);
        c.setAnimation("idle_healthy");
        c.setLocation(200, 200);
        
        class TestView extends JComponent {

            private SpriterCharacter character;

            public TestView(SpriterCharacter c) {
                character = c;
            }

            @Override
            public void paintComponent(Graphics g) {
                character.paint((Graphics2D) g);
            }
        }
        final TestView test = new TestView(c);
        new javax.swing.Timer(10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                c.nextFrame();
                test.repaint();
            }
        }).start();

        TestUtils.displayWithSize(test, "Test : SpriterManager", new Dimension(600, 400));
    }

    private static void nullCheck(Object[] obj) {
        for (Object o : obj) {
            if (o == null) {
                throw new IllegalArgumentException("an object is null!");
            }
        }
    }

    private static final class SprtAnimation {

        private final String name;
        private final SprtKeyFrame[] frames;

        public SprtAnimation(String name, SprtKeyFrame[] frames) {
            nullCheck(frames);
            this.name = name;
            this.frames = frames;
        }
    }

    private static class SprtKeyFrame {

        private final float duration;
        private final SprtFrame frame;

        public SprtKeyFrame(float duration, SprtFrame frame) {
            this.frame = frame;
            this.duration = duration;
        }
    }

    private static final class SprtFrame {

        private final String name;
        private final SprtSprite[] sprites;

        public SprtFrame(String name, SprtSprite[] sprites) {
            nullCheck(sprites);
            this.name = name;
            this.sprites = sprites;
        }
    }

    private static final class SprtSprite {

        private final Image image;
        private final int color;
        private final float opacity;
        private final float angle;
        private final boolean xFlip;
        private final boolean yFlip;
        private final float width;
        private final float height;
        private final float x;
        private final float y;

        public SprtSprite(Image image, int color, float opacity, float angle,
                boolean xFlip, boolean yFlip, float width, float height, float x,
                float y) {
            this.image = image;
            this.color = color;
            this.opacity = opacity;
            this.angle = angle;
            this.xFlip = xFlip;
            this.yFlip = yFlip;
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }
    }

    private static final class SprtCharacter {

        private final String name;
        private final SprtAnimation[] animations;

        public SprtCharacter(String name, SprtAnimation[] animations) {
            nullCheck(animations);
            this.name = name;
            this.animations = animations;
        }
    }

    public static final class SpriterCharacter {

        private final SprtCharacter character;
        private SprtAnimation currentAnimation;
        private int currentFrame = 0;
        private float xPos;
        private float yPos;

        private SpriterCharacter(SprtCharacter character) {
            this.character = character;
        }

        /**
         * Sets the current animation. <br />
         * The current frame will 0. <br />
         * 
         * @throws IllegalArgumentException if the animation doesn't exists
         * @param name the name of the animation
         */
        public void setAnimation(String name) {
            for (SprtAnimation an : character.animations) {
                if (an.name.equals(name)) {
                    currentAnimation = an;
                    currentFrame = 0;
                    return;
                }
            }
            throw new IllegalArgumentException("animation for the name '" + name + "' doesn't exists!");
        }

        /**
         * Sets the location of this Sprite. <br />
         */
        public void setLocation(float x, float y) {
            xPos = x;
            yPos = y;
        }

        /**
         * @return the character's name
         */
        public String getName() {
            return character.name;
        }

        /**
         * Changes the current frame by one. <br />
         */
        public void nextFrame() {
            currentFrame = (currentFrame + 1) % currentAnimation.frames.length;
        }

        public void paint(Graphics2D g) {
            final SprtAnimation animation = currentAnimation;
            final SprtFrame frame = animation.frames[currentFrame].frame;
            final SprtSprite[] sprites = frame.sprites;
            final Composite backup = g.getComposite();

            for (SprtSprite s : sprites) {
                final float h = s.height;
                final float w = s.width;
                final Image img = s.image;
                final float newW = img.getWidth(null);
                final float newH = img.getHeight(null);

                AffineTransform tx = AffineTransform.getScaleInstance(s.xFlip ? -1 : 1, s.yFlip ? -1 : 1);
                tx.translate(xPos + s.x, yPos + s.y);
                tx.translate(s.xFlip ? -w : 0, s.yFlip ? -h : 0);
                tx.rotate(Math.toRadians(s.angle), w / 2, h / 2);
                tx.scale(w / newW, h / newH);

                if (s.opacity < 100) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, s.opacity / 100));
                }

                g.drawImage(img, tx, null);
                g.setComposite(backup);
            }
        }
    }

    /*************************************************************************
     *****************   XML Spriter Format Reading **************************
     *************************************************************************/
    public static interface RessourceManager {

        public InputStream open(String path)
                throws IOException;
    }

    public static class DefaultRessourceManager implements RessourceManager {

        private final String rel;

        public DefaultRessourceManager(String rel) {
            this.rel = rel.endsWith("\\") || rel.endsWith("/")
                    ? rel : rel + System.getProperty("file.separator");
        }

        @Override
        public InputStream open(String path) throws IOException {
            return new FileInputStream(rel + path);
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

    private static SprtCharacter[] readCharacters(InputStream in, RessourceManager mng) throws Exception {
        final DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
        final Document doc = docBuilder.parse(in);
        final Node root = doc.getDocumentElement();
        checkFormat(root);

        final NodeList frameNodes = getChildrenByTagName(root, "frame");
        final SprtFrame[] frames = new SprtFrame[frameNodes.getLength()];
        final Map<String, Image> imageCache = new HashMap<String, Image>(100);
        final Map<String, SprtFrame> frameCache = new HashMap<String, SprtFrame>(100);

        for (int i = 0; i < frames.length; i++) {
            final Node frameNode = frameNodes.item(i);
            final String frameName = getNameForNode(frameNode);
            final NodeList currentFrameSpritesNodes = getChildrenByTagName(frameNode, "sprite");
            final SprtSprite[] sprites = new SprtSprite[currentFrameSpritesNodes.getLength()];
            for (int j = 0; j < sprites.length; j++) {
                final Node spriteNode = currentFrameSpritesNodes.item(j);

                final String imageName = getChildText(spriteNode, "image");
                final Image image = imageCache.containsKey(imageName)
                        ? imageCache.get(imageName) : ImageIO.read(mng.open(imageName));

                final int currentSpriteColor = Integer.parseInt(getChildText(spriteNode, "color"));
                final float currentSpriteopacity = Float.parseFloat(getChildText(spriteNode, "opacity"));
                final float currentSpriteAngle = Float.parseFloat(getChildText(spriteNode, "angle"));
                final boolean currentSpriteXFlip = Boolean.parseBoolean(getChildText(spriteNode, "xflip"));
                final boolean currentSpriteYFlip = Boolean.parseBoolean(getChildText(spriteNode, "yflip"));
                final float currentSpriteWidth = Float.parseFloat(getChildText(spriteNode, "width"));
                final float currentSpriteHeight = Float.parseFloat(getChildText(spriteNode, "height"));
                final float currentSpriteX = Float.parseFloat(getChildText(spriteNode, "x"));
                final float currentSpriteY = Float.parseFloat(getChildText(spriteNode, "y"));

                sprites[j] = new SprtSprite(image,
                        currentSpriteColor, currentSpriteopacity,
                        currentSpriteAngle, currentSpriteXFlip,
                        currentSpriteYFlip, currentSpriteWidth,
                        currentSpriteHeight, currentSpriteX,
                        currentSpriteY);
            }
            SprtFrame frm = new SprtFrame(frameName, sprites);
            frames[i] = frm;
            frameCache.put(frameName, frm);
        }

        final NodeList characterNodes = getChildrenByTagName(root, "char");
        final SprtCharacter[] characters = new SprtCharacter[characterNodes.getLength()];

        for (int i = 0; i < characters.length; i++) {
            final Node charNode = characterNodes.item(i);
            final NodeList animationNodes = getChildrenByTagName(charNode, "anim");
            final String charName = getNameForNode(charNode);
            final SprtAnimation[] animations = new SprtAnimation[animationNodes.getLength()];

            for (int j = 0; j < animations.length; j++) {
                final Node animNode = animationNodes.item(j);
                final String animName = getNameForNode(animNode);
                final NodeList keyFrameNodes = getChildrenByTagName(animNode, "frame");
                final SprtKeyFrame[] keyFrames = new SprtKeyFrame[keyFrameNodes.getLength()];

                for (int k = 0; k < keyFrames.length; k++) {
                    final Node keyFrameNode = keyFrameNodes.item(k);
                    final String keyFrameName = getNameForNode(keyFrameNode);
                    final float keyFrameDuration = Float.valueOf(getChild(keyFrameNode, "duration").getTextContent());
                    keyFrames[k] = new SprtKeyFrame(keyFrameDuration, frameCache.get(keyFrameName));
                }
                animations[j] = new SprtAnimation(animName, keyFrames);
            }
            characters[i] = new SprtCharacter(charName, animations);
        }

        return characters;
    }
}
