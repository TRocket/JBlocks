package org.jblocks.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jblocks.JBlocks;


public class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7450533410948027737L;
	private BufferedImage image;
	private String text;

	public ImagePanel() {
		text = "";
		try {
			// this.setOpaque(false);
			image = ImageIO.read(JBlocks.class.getResource("res/jblocks splash.png"));
		} catch (IOException ex) {
			System.err.println("error finding image");
		}
			
		
	}
	
	public ImagePanel(String path){
		try {
			// this.setOpaque(false);
			image = ImageIO.read(new File(path));
		} catch (IOException ex) {
			System.err.println("error finding image");
		}
		
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null); // see javadoc for more info on the
										// parameters
		//text = "test8";
		g.setColor(new Color(0, 46, 184));
		g.drawString(text, 350, 350);
		//g.drawChars(text.toCharArray(), 0, text.toCharArray().length, 350, 350);
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}