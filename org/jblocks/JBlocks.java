package org.jblocks;

import org.jblocks.gui.JBlocksGUI;
import org.jblocks.gui.Splash;

public class JBlocks {

    public static final double VERSION = 0.001;
    public static final String LONGVERSIONNAME = "0.001 not started yet :P";
    public JBlocksGUI gui;
    public Splash splash;

    public JBlocks() {
        //
    }

    /**
     * 
     * Shows the Splash and the MainFrame. <br />
     * I moved it out of the constructor because it not a good idea to initialize a JFrame in a constructor. <br />
     */
    public void init() {
        splash = new Splash();
        gui = new JBlocksGUI();

        splash.setText("JBlocks version " + LONGVERSIONNAME);
        splash.setVisible(true);
        gui.setVisible(true);
    }
}
