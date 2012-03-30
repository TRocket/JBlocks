package org.jblocks.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 *
 * this class will be removed later... <br />
 * 
 * @author ZeroLuck
 */
class ScriptPaneTest {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JScriptPane pane = new JScriptPane();

        pane.add(JScriptPane.createBlock("command", "Hello Stack%{br}Yoo %{s}"));

        pane.add(JScriptPane.createBlock("hat", "when %{gf} clicked"));
        pane.add(JScriptPane.createBlock("cap", "<HTML><b>return</b></HTML> %{r}"));
        pane.add(JScriptPane.createBlock("reporter", "test-1 %{r}"));
        pane.add(JScriptPane.createBlock("reporter", "test-2"));
        pane.add(JScriptPane.createBlock("boolean", "test-3"));
        pane.add(JScriptPane.createBlock("command", "test %{b} , %{r}"));
        pane.add(JScriptPane.createBlock("command", "hello world"));
        pane.add(JScriptPane.createBlock("hat", "when %{combo;space;a;b;c;d;} key pressed"));
        
        JCommandBlock block = new JCommandBlock();
        block.add(new JLabel("<HTML><b>Hello</b></HTML>"));
        JBlockSequence seq = new JBlockSequence();

        seq.setStack(JScriptPane.createBlock("command", "Hallo Welt %{s}"));

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
