package org.jblocks.painteditor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PaintEditorCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private BufferedImage canvas;
	private Graphics2D g;
	private int lastX, lastY;
	/**
	 * the paintbrush tool
	 */
	public static int TOOL_PAINT = 1;
	/**
	 * the rectangle tool
	 */
	public static int TOOL_RECT = 2;
	/**
	 * the spiral tool
	 */
	public static int TOOL_SPIRAL = 3;
	
	private int currentTool;

	public PaintEditorCanvas(int h, int w){
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = canvas.createGraphics();
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		this.lastX = e.getX();
		this.lastY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the current tool being used
	 */
	public int getCurrentTool() {
		return currentTool;
	}

	/**
	 * @param currentTool int of the new tool to use
	 */
	public void setCurrentTool(int currentTool) {
		this.currentTool = currentTool;
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(this.canvas, 0, 0, null);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		g.drawLine(this.lastX, this.lastY, e.getX(), e.getY());
		
		this.repaint();
		if (currentTool != 3) {
			this.lastX = e.getX();
			this.lastY = e.getY();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
