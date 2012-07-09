package org.jblocks.scratch.gui;

import java.awt.Point;

public class UnBuiltPoint {
Object x;
Object y;
public UnBuiltPoint(Object x, Object y) {
	this.x = x;
	this.y = y;
}
public Object getX() {
	return x;
}
public Object getY() {
	return y;
}
public Point build(int x, int y){
	return new Point(x, y);
}
}
