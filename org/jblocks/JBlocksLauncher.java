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

    /**
     * this is the standalone main method
     * @param args the command line args
     */
    public static void main(String[] args) {
        // TODO run JBlocks
    	Splash splash = new Splash();
    	splash.setVisible(true);
    	splash.setText("JBlocks 0.4");
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        JBlocksPane.setLaF();
        
        JBlocksPane p = new JBlocksPane();
        JFrame frm = new JFrame("JBlocks 0.4");
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
