package org.jblocks;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.Repainter;
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
     * This is the standalone main method
     * 
     * @param args the command line args
     */
    public static void main(String[] args) {
        Repainter.install();
        // TODO run JBlocks
        Splash splash = new Splash();
        splash.setAlwaysOnTop(true);
        splash.setText("JBlocks " + VERSION + " is starting...");
        splash.setVisible(true);
        
        JBlocksPane.setLaF();
        
        JFrame frm = new JFrame("JBlocks " + VERSION);
        frm.setIconImage(JBlocks.getImage("jblocks-icon.png"));
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(1000, 600);
        frm.setLocationByPlatform(true);
        frm.setLayout(new BorderLayout());
        
        frm.add(new JBlocks().getContentPane(), BorderLayout.CENTER);

        frm.setVisible(true);
        splash.setVisible(false);
    }
}
