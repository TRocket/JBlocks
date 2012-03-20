package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Color;
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

        JHatBlock block = new JHatBlock(pane);
        block.add(new JLabel("Wenn Taste"));
        block.add(new javax.swing.JComboBox<String>(new String[]{"space", "a", "b", "c"}));
        block.add(new JLabel("gedrückt."));

        JReporterBlock rb = new JReporterBlock(pane);
        rb.add(new JLabel("Hallo Welt"));

        JReporterBlock rb2 = new JReporterBlock(pane);
        rb2.add(new JLabel("Test Command"));
        rb2.add(new javax.swing.JCheckBox());
        rb.add(rb2);
        
        AbstrBlock rb3 = new JBooleanBlock(pane);
        rb3.add(new JLabel("Test Command"));
        rb3.add(new javax.swing.JCheckBox());
        rb.add(rb3);

        block.add(rb);


        block.setSize(block.getPreferredSize());
        block.setLocation(50, 50);

        pane.add(block);


        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setSize(500, 400);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        frm.add(pane);

        frm.setVisible(true);
    }
}
