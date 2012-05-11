package org.jblocks.spriter;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jblocks.spriter.SpriterParser.DefaultRessourceManager;

/**
 *
 * @author ZeroLuck
 */
public class SpriterManager {

    private final List<CharacterData> characters;
    private final List<Animation> animations;

    public SpriterManager() {
        characters = new ArrayList<CharacterData>();
        animations = new ArrayList<Animation>();
    }

    /**
     * Loads all the characters of the specified SCML-File. <br />
     * You can make an instance of a <code>SpriterCharacter</code> with {@link #create(java.lang.String)}. <br />
     * 
     * @param f the SCML-File
     * @throws IOException 
     */
    public void load(File f) throws IOException {
        CharacterData[] data = SpriterParser.read(new FileInputStream(f), new DefaultRessourceManager(f.getParent()));
        characters.addAll(Arrays.asList(data));
    }

    /**
     * Creates a new character. <br />
     * 
     * @param characterName the name of the character
     * @return the new character of null
     */
    public SpriterCharacter create(String characterName) {
        for (CharacterData d : characters) {
            if (d.name.equals(characterName)) {
                return new SpriterCharacter(d);
            }
        }
        return null;
    }

    /**
     * Creates a new character. <br />
     * 
     * @see #create(org.jblocks.spriter.SpriterCharacter) 
     */
    public SpriterCharacter create(SpriterCharacter character) {
        return create(character.getName());
    }

    public void stopAnimation(SpriterCharacter c) {
        for (Animation a : animations) {
            if (a.c == c) {
                animations.remove(a);
                break;
            }
        }
    }

    public void startAnimation(SpriterCharacter c) {
        stopAnimation(c);
        Animation anim = new Animation(c);
        anim.nextFrame = System.currentTimeMillis() + ((long) (c.getCurrentKeyFrame().duration * 10));
        animations.add(anim);
    }

    private void nextFrame(Animation a) {
        a.c.nextFrame();
        a.nextFrame += (long) (a.c.getCurrentKeyFrame().duration * 10);
    }

    public boolean stepAnimation() {
        for (Animation a : animations) {
            while (a.nextFrame < System.currentTimeMillis()) {
                nextFrame(a);
            }
        }
        return !animations.isEmpty();
    }

    public void optimize(GraphicsConfiguration cfg) {
        for (CharacterData d : characters) {
            for (AnimationData an : d.animations) {
                for (KeyFrameData frm : an.frames) {
                    for (SpriteData s : frm.frame.sprites) {
                        Image optimal = cfg.createCompatibleImage(s.image.getWidth(null),
                                s.image.getHeight(null), Transparency.TRANSLUCENT);

                        Graphics g = optimal.getGraphics();
                        g.drawImage(s.image, 0, 0, null);

                        s.image.flush();
                        s.image = optimal;
                    }
                }
            }
        }
    }

    private static class Animation {

        public final SpriterCharacter c;
        public volatile long nextFrame;

        public Animation(SpriterCharacter c) {
            this.c = c;
        }
    }
}
