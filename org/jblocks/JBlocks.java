package org.jblocks;


import java.io.File;
import java.io.IOException;


import org.jblocks.gui.Splash;
import org.jblocks.settingsloader.SettingsLoader;
/**
 * this class is JBlocks
 * making a new instance of it makes a new instance of the entire program(except the launcher)
 * @author TRocket
 *
 */
public class JBlocks {

	public static File SETTINGS_FILE = new File(System.getProperty("user.dir") + "/JBlocks/settings/settings.xml");
    public static final double VERSION = 0.001;
    public static final String LONGVERSIONNAME = "0.001 not started yet :P";
   
    public Splash splash;

    public JBlocks() {
        //
    	try {
			SettingsLoader sl = new SettingsLoader(SETTINGS_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 
     * Shows the Splash and the MainFrame. <br />
     * I moved it out of the constructor because it not a good idea to initialize a JFrame in a constructor. <br />
     */
    public void init() {
        splash = new Splash();
        
       // gui = new JBlocksGUI();

        splash.setText("JBlocks version " + LONGVERSIONNAME);
        splash.setVisible(true);
       // gui.setVisible(true);
    }
    
}
