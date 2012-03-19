package org.jblocks.editor;

import java.awt.BorderLayout;
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

        HatBlock block = new HatBlock(pane);
        block.add(new JLabel("Wenn Taste"));
        block.add(new javax.swing.JComboBox<String>(new String[]{"space", "a", "b", "c"}));
        block.add(new JLabel("gedrückt."));

        ReporterBlock rb = new ReporterBlock(pane);
        rb.add(new JLabel("Hallo Welt"));

        BooleanBlock rb2 = new BooleanBlock(pane);
        rb2.add(new JLabel("Test Reporter"));
        rb.add(rb2);

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
