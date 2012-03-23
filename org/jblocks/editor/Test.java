package org.jblocks.editor;

import java.awt.BorderLayout;
import java.lang.reflect.Constructor;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 *
 * Class for testing the BlockEditor. <br />
 * 
 * @author ZeroLuck
 */
public class Test {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JScriptPane pane = new JScriptPane();
        
        pane.add(pane.createBlock("hat", "when %{gf} clicked"));
        pane.add(pane.createBlock("command", "say %{r}"));
        pane.add(pane.createBlock("reporter", "test-1"));
        pane.add(pane.createBlock("reporter", "test-2"));
        pane.add(pane.createBlock("boolean", "test-3"));
        pane.add(pane.createBlock("command", "test %{b} , %{r}"));
        pane.add(pane.createBlock("hat", "when %{combo;space;a;b;c;d;} key pressed"));

        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(500, 400);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        frm.add(pane);

        frm.setVisible(true);
        pane.cleanup();
    }
}
