/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.laf;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author ZeroLuck
 */
public class Test {
    
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new JBLookAndFeel());
        JFrame frm = new JFrame("Hallo Welt");
        
        JDesktopPane pane = new JDesktopPane();
        JInternalFrame jif = new JInternalFrame();
        
        JButton butt = new JButton("<html><font color='red'>Hello</font></html>");
        butt.setBorder(null);
        
        jif.getContentPane().setLayout(new BorderLayout());
        jif.getContentPane().add(butt);
        
        jif.setSize(100,100);
        jif.setVisible(true);
        jif.setResizable(true);
        jif.setTitle("Hello World");
        pane.add(jif);
        
        frm.setLayout(new BorderLayout());
        frm.add(pane, BorderLayout.CENTER);
        
        frm.setSize(400,400);
        frm.setLocationByPlatform(true);
        frm.setVisible(true);
        
    }
}
