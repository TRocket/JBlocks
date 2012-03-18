package org.jblocks.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;



public class Splash extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6966282063729475150L;
	ImagePanel image;
	String text;
	JPanel panel = new JPanel();
	JProgressBar progbar;
	public Splash(){
		progbar = new JProgressBar();
		image = new ImagePanel();
		panel.add(image);
		this.add(image);
		
		//panel.add(progbar);
		this.setUndecorated(true);
		this.setSize(640, 400);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		this.setBackground(new Color(0, 0, 0, 0));
		this.setLocation(x, y);
		BufferedImage icon = null;
//		try {
//			icon = ImageIO.read(Splash.class.getResource("../images/icon.png"));
//		} catch (IOException e) {
//			// 
//			e.printStackTrace();
//		}
		this.setIconImage(icon);

		image.paint(getGraphics());
	}
	
	@Override
	public void repaint() {
		image.setText(text);

		super.repaint();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.repaint();
	}
}
