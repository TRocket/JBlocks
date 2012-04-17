package org.jblocks.spriter;

/**
 * this this class contains data about a spriter Animation in a Spriter Character
 * @author TRocket
 *
 */
public class Animation {

    private String name;
    private KeyFrame[] keyFrames;

    /**
     * 
     * @param name the name of this animation
     * @param keyFrames a Vector of Frames
     */
    public Animation(String name, KeyFrame[] keyFrames) {
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
    public KeyFrame[] getFrames() {
        return keyFrames;
    }
}
