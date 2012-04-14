package org.jblocks.spriter.model;

import java.util.Vector;
/**
 * I store the parsed contents of a spriter SCML
 * @author TRocket
 *
 */
public class SCML {
	private Vector<Character> characters;
	private Vector<Frame> frames;
	
	public SCML(Vector<Character> characters, Vector<Frame> frames) {
		super();
		this.characters = characters;
		this.frames = frames;
	}


	/**
	 * @return the characters
	 */
	public Vector<Character> getCharacters() {
		return characters;
	}


	/**
	 * @return the frames
	 */
	public Vector<Frame> getFrames() {
		return frames;
	}


}
