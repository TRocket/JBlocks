package org.jblocks.editor;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 *
 * @author ZeroLuck
 */
class BlockEditorTest {
    
    public static void main(String[] args) {
        
        
        JFrame frm = new JFrame("Block-Editor : Test");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationByPlatform(true);

        frm.setLayout(new BorderLayout());
        JRootPane root = new JRootPane();
        root.setLayout(new BorderLayout());
        root.add(JScriptPane.createBlock("command", "Hello %{r} World"));
        frm.add(root);
        frm.pack();

        
        frm.setVisible(true);
   //     pane.cleanup();
    }
}
