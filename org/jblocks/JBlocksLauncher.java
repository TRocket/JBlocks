package org.jblocks;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.JDragPane;
import org.jblocks.gui.Splash;

/**
 * 
 * Main class for the JBlocks desktop-application. <br />
 * 
 * @author TRocket
 * @author ZeroLuck
 */
public class JBlocksLauncher {

    private static final String VERSION = "0.5";
    
    /**
     * this is the standalone main method
     * @param args the command line args
     */
    public static void main(String[] args) {
        // TODO run JBlocks
    	Splash splash = new Splash();
    	splash.setVisible(true);
    	splash.setText("JBlocks " + VERSION + " is starting...");
        JBlocksPane.setLaF();
        
        JBlocksPane p = new JBlocksPane();
        JFrame frm = new JFrame("JBlocks " + VERSION);
        frm.setIconImage(new ImageIcon(JBlocks.class.getResource("res/jblocks-icon.png")).getImage());
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(1000, 600);
        frm.setLocationByPlatform(true);
        frm.setLayout(new BorderLayout());
        
        frm.add(new JDragPane(p), BorderLayout.CENTER);

        frm.setVisible(true);
        splash.setVisible(false);
    }
}
