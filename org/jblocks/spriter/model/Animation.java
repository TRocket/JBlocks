package org.jblocks.spriter.model;

import java.util.List;


/**
 * this this class contains data about a spriter Animation in a Spriter Character
 * @author TRocket
 *
 */
public class Animation {

    private String name;
    private List<KeyFrame> keyFrames;

    /**
     * 
     * @param name the name of this animation
     * @param keyFrames a Vector of Frames
     */
    public Animation(String name, List<KeyFrame> keyFrames) {
        super();
        this.name = name;
        this.keyFrames = keyFrames;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the keyFrames
     */
    public List<KeyFrame> getFrames() {
        return keyFrames;
    }
}
