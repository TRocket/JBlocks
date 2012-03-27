package org.jblocks.painteditor;

import java.awt.Dimension;

import javax.swing.JInternalFrame;

public class PaintEditor extends JInternalFrame {
public static int CANVAS_DEAFAULT_HEIGHT = 300;
public static int CANVAS_DEAFAULT_WIDTH = 300;
PaintEditorCanvas pEC = new PaintEditorCanvas(CANVAS_DEAFAULT_HEIGHT, CANVAS_DEAFAULT_WIDTH);

	public PaintEditor(){
		this.setTitle("Paint Editor");
		//set the canvas to the deafault height and width
		pEC.setPreferredSize(new Dimension(CANVAS_DEAFAULT_WIDTH, CANVAS_DEAFAULT_HEIGHT));
		//add thee canvs
		this.add(pEC);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}

}
