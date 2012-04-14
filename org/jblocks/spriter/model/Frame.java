package org.jblocks.spriter.model;

import java.util.Vector;

public class Frame {
private String name;
private Vector<Sprite> sprites;
public Frame(String name, Vector<Sprite> sprites) {
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
public Vector<Sprite> getSprites() {
	return sprites;
}
}

