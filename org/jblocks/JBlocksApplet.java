package org.jblocks;

import javax.swing.JApplet;

import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.Repainter;

/**
 * 
 * Main class for the JBlocks applet. <br />
 * 
 * @author TRocket
 * @author ZeroLuck
 */
public class JBlocksApplet extends JApplet {

    /**
     * runs the JBlocks applet.
     */
    @Override
    public void init() {
        Repainter.install();

        String frame = getParameter("frame");
        if (frame != null && frame.equalsIgnoreCase("true")) {
            JBlocksLauncher.main(new String[]{});
        } else {
            JBlocksPane.setLaF();
            setContentPane(new JBlocks().getContentPane());
        }
    }
}
