package org.jblocks.spriter.model;

import java.util.List;

public class Frame {

    private String name;
    private List<Sprite> sprites;

    public Frame(String name, List<Sprite> sprites) {
        super();
        this.name = name;
        this.sprites = sprites;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the sprites
     */
    public List<Sprite> getSprites() {
        return sprites;
    }
}
