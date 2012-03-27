
package org.jblocks.painteditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameListener;

public class PaintEditor extends JInternalFrame implements ToolChanged, ActionListener, ChangeListener{
public static int CANVAS_DEAFAULT_HEIGHT = 300;
public static int CANVAS_DEAFAULT_WIDTH = 300;
PaintEditorCanvas pEC = new PaintEditorCanvas(CANVAS_DEAFAULT_HEIGHT, CANVAS_DEAFAULT_WIDTH);
JPanel panel = new JPanel();
ToolSelector ts = new ToolSelector(this);
JButton clear = new JButton("clear");
JColorChooser jcc = new JColorChooser();

	public PaintEditor(){
		this.setTitle("Paint Editor");
		//set the canvas to the deafault height and width
		pEC.setPreferredSize(new Dimension(CANVAS_DEAFAULT_WIDTH, CANVAS_DEAFAULT_HEIGHT));
		panel.add(clear);
		panel.add(ts);
		panel.add(pEC);
		panel.add(jcc);
		clear.addActionListener(this);
		jcc.getSelectionModel().addChangeListener(this);
		
		//add the panel
		this.add(panel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}

	@Override
	public void toolChanged(int tool) {
		// TODO Auto-generated method stub
		pEC.setCurrentTool(tool);
		System.out.print(tool);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		pEC.clear();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		pEC.setColor(jcc.getColor());
	}
	
	

}
