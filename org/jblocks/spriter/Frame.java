package org.jblocks.spriter;


public class Frame {

    private String name;
    private Sprite[] sprites;

    public Frame(String name, Sprite[] sprites) {
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
    public Sprite[] getSprites() {
        return sprites;
    }
}
