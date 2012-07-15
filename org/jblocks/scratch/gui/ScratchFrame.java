package org.jblocks.scratch.gui;

import java.awt.Image;

import javax.swing.JFrame;

import org.jblocks.gui.JImagePanel;

public class ScratchFrame extends JFrame {
	public ScratchFrame(Image img){
		this.setTitle("Scratch");
		JImagePanel jip = new JImagePanel(img);
		this.add(jip);
		this.pack();
		this.setVisible(true);
	}
}
