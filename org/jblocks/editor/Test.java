/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.editor;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author ZeroLuck
 */
public class Test {
    
    public static void main(String[] args) {
        JScriptPane pane = new JScriptPane();
        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setSize(500, 400);
        frm.setLocationByPlatform(true);
        
        frm.setLayout(new BorderLayout());
        frm.add(pane);
        
        frm.setVisible(true);
    }
}
