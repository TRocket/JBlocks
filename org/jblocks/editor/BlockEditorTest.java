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

        ch.addCategory("Control", new Color(0xD6900A));
        ch.addCategory("Motion", new Color(0xff4a6cd6));
        ch.addCategory("Operators", new Color(0xff62c213));
        ch.addCategory("Lists", Color.RED);

        ch.addCategory("Touching", Color.CYAN);
        ch.addCategory("Pen", Color.GREEN.darker());
        ch.addCategory("Sound", Color.MAGENTA);
        ch.addCategory("Looking", Color.MAGENTA.darker());

        ch.addBlock("Control", BlockFactory.createBlock("hat", "When %{gf} clicked"));
        ch.addBlock("Control", BlockFactory.createBlock("hat", "When key %{combo;space;a;b;c;d;e;f} pressed"));
        ch.addBlock("Control", BlockFactory.createBlock("cap", "return %{r}"));

        ch.addBlock("Control", BlockFactory.createBlock("command", "when %{b}%{br}%{s}"));
        ch.addBlock("Control", BlockFactory.createBlock("reporter", "x pos"));
        ch.addBlock("Control", BlockFactory.createBlock("reporter", "y pos"));
        ch.addBlock("Control", BlockFactory.createBlock("boolean", "visible"));
        
        ch.addBlock("Control", BlockFactory.createBlock("reporter", "test %{b}%{r}"));
        ch.addBlock("Control", BlockFactory.createBlock("command", "when %{b}%{s}%{br}else%{s}"));
        ch.addBlock("Control", BlockFactory.createBlock("command", "script variable %{var;variable}"));
        
        ch.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}+%{r}"));
        ch.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}-%{r}"));
        ch.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}*%{r}"));
        ch.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}/%{r}"));
        ch.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}mod%{r}"));
        
        ch.cleanup();

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
