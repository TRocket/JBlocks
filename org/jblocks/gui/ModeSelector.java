package org.jblocks.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * 
 * @author TRocket
 *
 */
public class ModeSelector extends JFrame {
	/**
	 * 
	 */
	public static final int MODE_SHELL = 1;
	/**
	 * 
	 */
	public static final int MODE_SCRATCH = 2;
	/**
	 * 
	 */
	public static final int MODE_SPRITER = 3;
	/**
	 * 
	 */
	public static final int MODE_RASPBERY_PI= 4;
	/**
	 * 
	 */
	public void createGUI(){
		this.setUndecorated(true);
		JPanel panel = new JPanel();
	}
	/**
	 * 
	 * @return
	 */
	public int getMode(){
		return 0;
	}
	
}
