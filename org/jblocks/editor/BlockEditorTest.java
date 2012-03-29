package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;

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
        JBlockEditor ch = new JBlockEditor();
        
        ch.addCategory("Motion", Color.BLUE); 
        ch.addCategory("Operators", Color.GREEN);
        ch.addCategory("Lists", Color.RED);
        ch.addCategory("Control", Color.ORANGE);
        
        ch.addCategory("Touching", Color.CYAN);
        ch.addCategory("Pen", Color.GREEN.darker());
        ch.addCategory("Sound", Color.MAGENTA);
        ch.addCategory("Looking", Color.MAGENTA.darker());
        
        JScriptPane pane = new JScriptPane();
        pane.add(JScriptPane.createBlock("hat", "When %{gf} clicked"));
        pane.cleanup();
                
        ch.setScriptPane(pane);
        
        
        frm.add(ch);
        frm.pack();

        
        frm.setVisible(true);
   //     pane.cleanup();
    }
}
