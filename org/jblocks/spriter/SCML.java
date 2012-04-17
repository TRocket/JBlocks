package org.jblocks.spriter;

import java.awt.Image;
import java.util.Map;

/**
 * I store the parsed contents of a spriter SCML
 * @author TRocket
 *
 */
public class SCML {

    private Character[] characters;
    private Frame[] frames;
    private Map<String, Image> images;

    public SCML(Character[] characters, Frame[] frames,
            Map<String, Image> images) {
        super();
        this.characters = characters;
        this.frames = frames;
        this.images = images;
    }

    /**
     * @return the characters
     */
    public Character[] getCharacters() {
        return characters;
    }

    /**
     * @return the frames
     */
    public Frame[] getFrames() {
        return frames;
    }

    /**
     * @return the images
     */
    public Map<String, Image> getImages() {
        return images;
    }
}
