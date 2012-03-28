package org.jblocks.painteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * 
 * @author TRocket
 *
 */
public class ToolSelector extends JPanel implements ActionListener, ChangeListener {
JButton brush = new JButton("Brush");
JButton spiral = new JButton("spiral");
JSlider linethickness = new JSlider();

ToolChanged tc;
public ToolSelector(ToolChanged tc){
	this.tc = tc;
	this.add(brush);
	this.add(spiral);
	this.add(linethickness);
	linethickness.setMaximum(50);
	linethickness.setMinimum(1);
	linethickness.addChangeListener(this);

	brush.addActionListener(this);
	spiral.addActionListener(this);

}


	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == brush) {
			tc.toolChanged(PaintEditorCanvas.TOOL_PAINT);
		} else if (e.getSource() == spiral){
			tc.toolChanged(PaintEditorCanvas.TOOL_SPIRAL);
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
