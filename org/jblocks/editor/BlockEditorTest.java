package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * this class will be removed later... <br />
 * 
 * @author ZeroLuck
 */
public class BlockEditorTest {

    public static JBlockEditor createTestEditor() {
        JBlockEditor ch = new JBlockEditor();

        ch.addCategory("Control", Color.ORANGE);
        ch.addCategory("Motion", Color.BLUE);
        ch.addCategory("Operators", Color.GREEN);
        ch.addCategory("Lists", Color.RED);

        ch.addCategory("Touching", Color.CYAN);
        ch.addCategory("Pen", Color.GREEN.darker());
        ch.addCategory("Sound", Color.MAGENTA);
        ch.addCategory("Looking", Color.MAGENTA.darker());

        JScriptPane pane = new JScriptPane();
        pane.add(JScriptPane.createBlock("hat", "When %{gf} clicked"));
        pane.cleanup();

        ch.setScriptPane(pane);

        ch.addBlock("Control", JScriptPane.createBlock("hat", "When %{gf} clicked"));
        ch.addBlock("Control", JScriptPane.createBlock("hat", "When key %{combo;space;a;b;c;d;e;f} pressed"));
        ch.addBlock("Control", JScriptPane.createBlock("command", "return %{r}"));

        ch.addBlock("Control", JScriptPane.createBlock("command", "when %{b}%{br}%{s}"));

        AbstrBlock b = JScriptPane.createBlock("reporter", "xpos");
        b.setBackground(new Color(32, 64, 189));
        ch.addBlock("Motion", b);

        return ch;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        JFrame frm = new JFrame("Block-Editor : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        JBlockEditor ch = createTestEditor();

        frm.add(ch);
        frm.pack();


        frm.setVisible(true);

    }
}
