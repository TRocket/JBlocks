package org.jblocks.painteditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Date;

import javax.swing.JPanel;
/**
 * 
 * @author TRocket
 *
 */

public class PaintEditorCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private BufferedImage canvas;
	private Graphics2D g;
	private int lastX, lastY;
	private int w;
	private int h;
	private Color color;
	private int linewidth;
	private BufferedImage rectangleOldImage;
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
		this.h = h;
		this.w = w;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		clear();
		
		
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
		if (currentTool == 2) {
			rectangleOldImage = deepCopy(canvas);
			g = canvas.createGraphics();
			g.setColor(color);
			g.fillRect(lastX, lastY, e.getX() - lastX, e.getY() - lastY);
			System.out.println("rectangle :" + lastX + " " + lastY + " " + (e.getX() - lastX) + " " + (e.getY() - lastY));
			this.repaint();
			
			canvas = deepCopy(rectangleOldImage);
			g = canvas.createGraphics();
			
			//g = canvas.createGraphics();
		} else {
			
			g.setStroke(new BasicStroke(this.linewidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(color);
			g.drawLine(this.lastX, this.lastY, e.getX(), e.getY());
			
			this.repaint();
			if (currentTool != 3) {
				this.lastX = e.getX();
				this.lastY = e.getY();
			}
		}
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void clear() {
		canvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		g = canvas.createGraphics();
		this.repaint();
	}
	/**
	 * @return the w
	 */
	public int getW() {
		return w;
	}
	/**
	 * @param w the w to set
	 */
	public void setW(int w) {
		this.w = w;
	}
	/**
	 * @return the h
	 */
	public int getH() {
		return h;
	}
	/**
	 * @param h the h to set
	 */
	public void setH(int h) {
		this.h = h;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	/**
	 * @return the linewidth
	 */
	public int getLinewidth() {
		return linewidth;
	}
	/**
	 * @param linewidth the linewidth to set
	 */
	public void setLinewidth(int linewidth) {
		this.linewidth = linewidth;
	}
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
}
