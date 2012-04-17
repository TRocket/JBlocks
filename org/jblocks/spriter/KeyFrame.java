package org.jblocks.spriter;

/**
 * this this class contains data about a spriter KeyFrame in a Spriter animation
 * @author TRocket
 */
public class KeyFrame {

    private final String name;
    private final float duration;

    /**
     * 
     * @param name the name of this KeyFrame
     * @param duration the duration of this KeyFrame
     */
    public KeyFrame(final String name, final float duration) {
        super();
        this.name = name;
        this.duration = duration;
    }

    /**
     * @return the name of this KeyFrame
     */
    public String getName() {
        return name;
    }

    /**
     * @return the duration of this KeyFrame
     */
    public float getDuration() {
        return duration;
    }
}
