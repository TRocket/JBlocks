package org.jblocks.spriter.model;

import java.util.Vector;
/**
 * this this class contains data about a spriter Animation in a Spriter Character
 * @author TRocket
 *
 */
public class Animation {
private String name;
private Vector<Frame> frames;
	/**
	 * 
	 * @param name the name of this animation
	 * @param frames a Vector of Frames
	 */
	public Animation(String name, Vector<Frame> frames) {
		super();
		this.name = name;
		this.frames = frames;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the frames
	 */
	public Vector<Frame> getFrames() {
		return frames;
	}

	

}
