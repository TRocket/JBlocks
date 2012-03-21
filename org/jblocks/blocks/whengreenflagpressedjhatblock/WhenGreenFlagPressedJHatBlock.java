package org.jblocks.blocks.whengreenflagpressedjhatblock;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jblocks.JBlocks;
import org.jblocks.editor.JHatBlock;
import org.jblocks.editor.JScriptPane;
import org.jblocks.gui.JImagePanel;


public class WhenGreenFlagPressedJHatBlock extends JHatBlock {

	public WhenGreenFlagPressedJHatBlock(JScriptPane pane) {
		super(pane);
		// TODO Auto-generated constructor stub
		this.add(new JLabel("when"));
		//this.add(new ImageIcon(JBlocks.class.getResource("res/splash.png")));
		try {
			this.add(new JImagePanel(ImageIO.read(JBlocks.class.getResourceAsStream("res/goButton.gif"))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.add(new JLabel("pressed"));
	}

}
