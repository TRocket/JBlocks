/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jblocks.gui.JBlocksPane;

/**
 *
 * @author ZeroLuck
 */
public class TestUtils {


    public static void displayPacked(Component c, String title) {
        JFrame frm = new JFrame(title == null ? "Test" : "Test : " + title);
        frm.setLocationByPlatform(true);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new BorderLayout());
        frm.add(c, BorderLayout.CENTER);
        frm.pack();

        frm.setVisible(true);
        JBlocksPane.setLaF();
        SwingUtilities.updateComponentTreeUI(frm);
    }

    public static void displayWithSize(Component c, String title, Dimension size) {
        JFrame frm = new JFrame(title == null ? "Test" : "Test : " + title);
        frm.setLocationByPlatform(true);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new BorderLayout());
        frm.add(c, BorderLayout.CENTER);
        frm.setSize(size);

        frm.setVisible(true);
        JBlocksPane.setLaF();
        SwingUtilities.updateComponentTreeUI(frm);
    }
}
