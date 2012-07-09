package org.jblocks.scratch.gui;

import java.awt.Point;
import java.awt.Rectangle;

public class UnbuiltRectangle {
	Object x;
	Object y;
	Object w;
	Object h;
	public UnbuiltRectangle(Object x, Object y, Object w, Object h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	public Object getX() {
		return x;
	}
	public Object getY() {
		return y;
	}
	public Rectangle build(int x, int y, int w, int h){
		return new Rectangle(x, y, w, h);
	}
	public Object getW() {
		return w;
	}
	public Object getH() {
		return h;
	}
}
