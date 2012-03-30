package org.jblocks.gui;

import javax.swing.JFrame;

/**
 * this class will be removed later... <br />
 * 
 * @author ZeroLuck
 */
public class SpriteChooserTest {
    
    public static void main(String[] args) {
        JBlocksPane.setLaF();
        
        JFrame frm = new JFrame("JSpriteChooser : Test");
        frm.setLocationByPlatform(true);
        frm.add(new JSpriteChooser());
        frm.pack();
        frm.setVisible(true);
    }
}
