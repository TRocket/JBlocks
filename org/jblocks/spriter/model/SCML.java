package org.jblocks.spriter.model;

import java.util.List;

/**
 * I store the parsed contents of a spriter SCML
 * @author TRocket
 *
 */
public class SCML {

    private List<Character> characters;
    private List<Frame> frames;

    public SCML(List<Character> characters, List<Frame> frames) {
        super();
        this.characters = characters;
        this.frames = frames;
    }

    /**
     * @return the characters
     */
    public List<Character> getCharacters() {
        return characters;
    }

    /**
     * @return the frames
     */
    public List<Frame> getFrames() {
        return frames;
    }
}
