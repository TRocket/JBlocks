package org.jblocks.painteditor2;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;

import org.jblocks.gui.JBlocksPane;

/**
 *
 * @author ZeroLuck
 */
public class Test {

    public static Component createTestPaintEditor() {
        return new JPaintEditor();
    }

    public static void main(String[] args) {
        JBlocksPane.setLaF();

        JFrame frm = new JFrame("JPaintEditor : Test");
        frm.setLocationByPlatform(true);
        frm.setLayout(new BorderLayout());
        frm.setResizable(false);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Component ch = createTestPaintEditor();

        frm.add(ch, BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);
    }
}
