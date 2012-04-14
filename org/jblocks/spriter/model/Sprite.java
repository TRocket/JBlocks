package org.jblocks.spriter.model;
/**
 * I hold data from a spriter sprite
 * @author TRocket
 *
 */
public class Sprite {
private String image;
private int color;
private float opacity;
private float angle;
private boolean xFlip;
private boolean yFlip;
private float width;
private float height;
private float x;
private float y;
/**
 * 
 * @param image
 * @param color
 * @param opacity
 * @param angle
 * @param xFlip
 * @param yFlip
 * @param width
 * @param height
 * @param x
 * @param y
 */
public Sprite(String image, int color, float opacity, float angle,
		boolean xFlip, boolean yFlip, float width, float height, float x,
		float y) {
	super();
	this.image = image;
	this.color = color;
	this.opacity = opacity;
	this.angle = angle;
	this.xFlip = xFlip;
	this.yFlip = yFlip;
	this.width = width;
	this.height = height;
	this.x = x;
	this.y = y;
}
/**
 * @return the image
 */
public String getImage() {
	return image;
}
/**
 * @return the color
 */
public int getColor() {
	return color;
}
/**
 * @return the opacity
 */
public float getOpacity() {
	return opacity;
}
/**
 * @return the angle
 */
public float getAngle() {
	return angle;
}
/**
 * @return the xFlip
 */
public boolean isxFlip() {
	return xFlip;
}
/**
 * @return the yFlip
 */
public boolean isyFlip() {
	return yFlip;
}
/**
 * @return the width
 */
public float getWidth() {
	return width;
}
/**
 * @return the height
 */
public float getHeight() {
	return height;
}
/**
 * @return the x
 */
public float getX() {
	return x;
}
/**
 * @return the y
 */
public float getY() {
	return y;
}
}
