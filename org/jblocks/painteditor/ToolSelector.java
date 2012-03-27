package org.jblocks.painteditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolSelector extends JPanel implements ActionListener {
JButton brush = new JButton("Brush");
JButton spiral = new JButton("spiral");

ToolChanged tc;
public ToolSelector(ToolChanged tc){
	this.tc = tc;
	this.add(brush);
	this.add(spiral);

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
}
