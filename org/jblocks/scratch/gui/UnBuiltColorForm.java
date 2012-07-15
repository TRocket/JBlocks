package org.jblocks.scratch.gui;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.Hashtable;

public class UnBuiltColorForm {
Object width;
Object height;
Object depth;
Object PrivateOffset;
Object bits;
Object colors;

int[] builtbits;
public UnBuiltColorForm(Object width, Object height, Object depth,
		Object privateOffset, Object bits, Object colors) {
	super();
	this.width = width;
	this.height = height;
	this.depth = depth;
	PrivateOffset = privateOffset;
	this.bits = bits;
	this.colors = colors;
	System.out.println("width " + width);
	System.out.println("height " + height);
	System.out.println("depth " + depth);
	System.out.println("privateOffset " + privateOffset);
}
/**
 * @return the width
 */
public Object getWidth() {
	return width;
}
/**
 * @return the height
 */
public Object getHeight() {
	return height;
}
/**
 * @return the depth
 */
public Object getDepth() {
	return depth;
}
/**
 * @return the privateOffset
 */
public Object getPrivateOffset() {
	return PrivateOffset;
}
/**
 * @return the bits
 */
public Object getBits() {
	return bits;
}
/**
 * @return the colors
 */
public Object getColors() {
	return colors;
}

public Image build() throws IOException{
	System.out.println(this.bits.getClass());
	this.builtbits = Images.UnHibernate(this.bits);
	
	Hashtable colorSet =(Hashtable) this.colors;
	Color[] newColors = new Color[colorSet.size()];
	for (int i = 0; i < colorSet.size(); i++) {
		newColors[i] = (Color) colorSet.get(i);
	}
	return Images.depthBuild((int) (short) this.depth, this.builtbits, (int) (short)this.width, (int) (short)this.height, newColors);
}
/**
 * @param bits the bits to set
 */
public void setBits(Object bits) {
	this.bits = bits;
}
/**
 * @param colors the colors to set
 */
public void setColors(Object colors) {
	this.colors = colors;
}
}
