package org.jblocks.spriter.model;

import java.awt.Image;
import java.util.HashMap;
import java.util.List;

/**
 * I store the parsed contents of a spriter SCML
 * @author TRocket
 *
 */
public class SCML {

    private List<Character> characters;
    private List<Frame> frames;
    private HashMap<String, Image> images;

    public SCML(List<Character> characters, List<Frame> frames,
			HashMap<String, Image> images) {
		super();
		this.characters = characters;
		this.frames = frames;
		this.images = images;
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


	/**
	 * @return the images
	 */
	public HashMap<String, Image> getImages() {
		return images;
	}
    
}
