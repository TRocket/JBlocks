package org.jblocks.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
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

        pane.add(pane.createBlock("command", "Hello Stack%{br}Yoo %{s}"));

        pane.add(pane.createBlock("hat", "when %{gf} clicked"));
        pane.add(pane.createBlock("cap", "<HTML><b>return</b></HTML> %{r}"));
        pane.add(pane.createBlock("reporter", "test-1 %{r}"));
        pane.add(pane.createBlock("reporter", "test-2"));
        pane.add(pane.createBlock("boolean", "test-3"));
        pane.add(pane.createBlock("command", "test %{b} , %{r}"));
        pane.add(pane.createBlock("command", "hello world"));
        pane.add(pane.createBlock("hat", "when %{combo;space;a;b;c;d;} key pressed"));
        
        JCommandBlock block = new JCommandBlock(pane);
        block.add(new JLabel("<HTML><b>Hello</b></HTML>"));
        JBlockSequence seq = new JBlockSequence(pane);
        
        seq.setStack(pane.createBlock("command", "Hallo Welt %{s}"));
        
        block.add(seq);
        pane.add(block);

        JFrame frm = new JFrame("Script-Pane : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(500, 400);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        frm.add(new JScrollPane(pane));

        frm.setVisible(true);
        pane.cleanup();
    }
}
