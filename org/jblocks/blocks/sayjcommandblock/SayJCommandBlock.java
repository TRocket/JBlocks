package org.jblocks.blocks.sayjcommandblock;

import javax.swing.JLabel;

import org.jblocks.editor.JCommandBlock;
import org.jblocks.editor.JReporterInput;
import org.jblocks.editor.JScriptPane;

public class SayJCommandBlock extends JCommandBlock {



	public SayJCommandBlock(JScriptPane pane) {
		super(pane);
		// TODO Auto-generated constructor stub
		this.add(new JLabel("Say "));
        JReporterInput inp1 = new JReporterInput(pane);
        inp1.reset();
        this.add(inp1);
	}

}
