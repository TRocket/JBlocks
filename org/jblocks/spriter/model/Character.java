package org.jblocks.spriter.model;

import java.util.List;


/**
 * this this class contains data about a spriter Character in a Spriter project
 * @author TRocket
 *
 */
public class Character {

    private String name;
    private List<Animation> animations;

    /**
     * 
     * @param name the name of this Charater
     * @param animations the animation this Charater sould have
     */
    public Character(String name, List<Animation> animations) {
        super();
        this.name = name;
        this.animations = animations;
    }

    /**
     * @return the name of this Character
     */
    public String getName() {
        return name;
    }

    /**
     * @return the animations this Character has
     */
    public List<Animation> getAnimations() {
        return animations;
    }
}
