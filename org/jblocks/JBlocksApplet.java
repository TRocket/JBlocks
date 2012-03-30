package org.jblocks;

import javax.swing.JApplet;
import org.jblocks.gui.JBlocksPane;

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
        // TODO run JBlocks

        String frame = getParameter("frame");
        if (frame != null && frame.equalsIgnoreCase("true")) {
            JBlocksLauncher.main(new String[]{});
        } else {
            JBlocksPane.setLaF();
            
            JBlocksPane p = new JBlocksPane();
            setContentPane(p);
        }
    }
}
