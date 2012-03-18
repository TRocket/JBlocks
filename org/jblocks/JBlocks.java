package org.jblocks;

import org.jblocks.gui.JBlocksGUI;
import org.jblocks.gui.Splash;

public class JBlocks {
public static final double VERSION = 0.001;
public static final String LONGVERSIONNAME = "0.001 not started yet :P";
public JBlocksGUI gui = new JBlocksGUI();
public Splash splash = new Splash();

	public JBlocks() {
		gui.setVisible(true);
		splash.setVisible(true);
		splash.setText("JBlocks version " + LONGVERSIONNAME);
	}

}
