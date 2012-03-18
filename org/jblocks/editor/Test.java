/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.editor;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * Class for testing the BlockEditor. <br />
 * 
 * @author ZeroLuck
 */
public class Test {
    
    public static void main(String[] args) {
        JScriptPane pane = new JScriptPane();
        
        HatBlock block = new HatBlock(pane);
        block.add(new JLabel("Hallo Welt"));
        
        HatBlock b2 = new HatBlock(pane);
        b2.add(new JLabel("Input :D"));
        block.add(b2);
        
        System.out.println(block.getPreferredSize());
        block.setSize(block.getPreferredSize());
        block.setLocation(50,50);
        
        pane.add(block);
        
        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setSize(500, 400);
        frm.setLocationByPlatform(true);
        
        frm.setLayout(new BorderLayout());
        frm.add(pane);
        
        frm.setVisible(true);
    }
}
