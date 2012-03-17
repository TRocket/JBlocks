package org.jblocks.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import org.jblocks.JBlocks;

/**
 * 
 * @author TRocket
 *
 */
public class JBlocksGUI extends JFrame {

	/**
	 * @throws HeadlessException
	 */
	public JBlocksGUI() throws HeadlessException {
		super();
		this.setTitle("JBlocks" + JBlocks.LONGVERSIONNAME);
	}

	
	
}
