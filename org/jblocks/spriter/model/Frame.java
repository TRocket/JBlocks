package org.jblocks.spriter.model;

/**
 * this this class contains data about a spriter Frame in a Spriter animation
 * @author TRocket
 */
public class Frame {
private String name;
private Float duration;
	/**
	 * 
	 * @param name the name of this Frame
	 * @param duration the duration of this Frame
	 */
	public Frame(String name, Float duration){
		super();
		this.name = name;
		this.duration = duration;
	}

	/**
	 * @return the name of this Frame
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the duration of this Frame
	 */
	public Float getDuration() {
		return duration;
	}

}
