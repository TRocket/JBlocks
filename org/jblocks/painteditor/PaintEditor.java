
package org.jblocks.painteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.jblocks.gui.JSmallColorChooser;
import org.jblocks.gui.JSmallColorChooser.ColorChangedListener;
import org.jblocks.gui.JZoomChooser;
/**
 * 
 * @author TRocket
 *
 */
public class PaintEditor extends JInternalFrame implements ToolChanged, ActionListener,  ColorChangedListener{
public static int CANVAS_DEAFAULT_HEIGHT = 300;
public static int CANVAS_DEAFAULT_WIDTH =  300;
PaintEditorCanvas pEC = new PaintEditorCanvas(CANVAS_DEAFAULT_HEIGHT, CANVAS_DEAFAULT_WIDTH);
JPanel panel = new JPanel();
ToolSelector ts = new ToolSelector(this);
JButton clear = new JButton("clear");
JSmallColorChooser jcc = new JSmallColorChooser();
JZoomChooser jzc = new JZoomChooser();


 
	public PaintEditor(){
		this.setTitle("Paint Editor");
		//set the canvas to the deafault height and width
		pEC.setPreferredSize(new Dimension(CANVAS_DEAFAULT_WIDTH, CANVAS_DEAFAULT_HEIGHT));
		panel.add(clear);
		panel.add(ts);
		panel.add(jcc);
		panel.add(pEC);
		panel.add(jzc);
		clear.addActionListener(this);
		jcc.addColorChangedListener(this);
		
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
	public void lineThicknessChanged(int line) {
		// TODO Auto-generated method stub
		pEC.setLinewidth(line);
	}

	@Override
	public void colorChanged(JSmallColorChooser ch, Color c) {
		// TODO Auto-generated method stub
		pEC.setColor(c);
	}
	
	

}
