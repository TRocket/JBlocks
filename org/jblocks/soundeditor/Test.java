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
        
        JSoundEditor rec = new JSoundEditor();
        
        JFrame frm = new JFrame("Sound-Editor : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        frm.add(rec);
        frm.setSize(650,400);

        frm.setVisible(true);
    }
}
