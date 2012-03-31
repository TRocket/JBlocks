package org.jblocks.painteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * 
 * @author TRocket
 *
 */
public class ToolSelector extends JPanel implements ActionListener, ChangeListener {
	ButtonGroup toolbox = new ButtonGroup();
JRadioButton brush = new JRadioButton("Brush");
JRadioButton spiral = new JRadioButton("spiral");
JRadioButton rect = new JRadioButton("rectangle");
JSlider linethickness = new JSlider();

ToolChanged tc;
public ToolSelector(ToolChanged tc){
	this.tc = tc;
	
	toolbox.add(brush);
	toolbox.add(spiral);
	toolbox.add(rect);
	this.add(brush);
	this.add(spiral);
	this.add(rect);
	this.add(linethickness);
	linethickness.setMaximum(200);
	linethickness.setMinimum(1);
	linethickness.addChangeListener(this);

	brush.addActionListener(this);
	spiral.addActionListener(this);
	rect.addActionListener(this);
	

}


	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == brush) {
			tc.toolChanged(PaintEditorCanvas.TOOL_PAINT);
		} else if (e.getSource() == spiral){
			tc.toolChanged(PaintEditorCanvas.TOOL_SPIRAL);
		} else if (e.getSource() == rect) {
			tc.toolChanged(PaintEditorCanvas.TOOL_RECT);
		} else {
			
		}
		
		
		

}


	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		tc.lineThicknessChanged(linethickness.getValue());
		System.out.println("line thickness " + linethickness.getValue());
	}
}
