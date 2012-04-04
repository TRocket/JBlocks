/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.soundeditor;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import org.jblocks.gui.JBlocksPane;

/**
 *
 * @author ZeroLuck
 */
public class Test {

    public static void main(String[] args) {
        JBlocksPane.setLaF();
        
        JSoundRecorder rec = new JSoundRecorder();
        
        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        frm.add(rec);
        frm.pack();

        frm.setVisible(true);
    }
}
