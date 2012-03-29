package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * A block-editor class. <br />
 * The block-editor can switch between script-panes, <br />
 * and has a block-category system. <br />
 * 
 * @version 0.1
 * @author ZeroLuck
 */
public class JBlockEditor extends JPanel {

    private JScriptPane pane;
    private JCategoryChooser chooser;
    
    /**
     * creates a block-editor with an <b>empty</b> category-chooser. <br />
     * 
     * @see #addCategory(java.lang.String, java.awt.Color) 
     * @see #setScriptPane(org.jblocks.editor.JScriptPane) 
     */
    public JBlockEditor() {
        chooser = new JCategoryChooser();
        
        setLayout(new BorderLayout());
        add(chooser, BorderLayout.WEST);
    }
    
    /**
     * switches between script-panes. <br />
     * 
     * @see #getScriptPane() 
     * @param p - the new script pane to display.
     */
    public void setScriptPane(JScriptPane p) {
        if (pane != null) {
            remove(pane);
        }
        pane = p;
        if (pane != null) {
            add(pane, BorderLayout.CENTER);
        }
    }

    /**
     * Adds a new category to the block-chooser. <br />
     * 
     * @see JCategoryChooser#addCategory(java.lang.String, java.awt.Color)
     * @param name - the name of the category.
     * @param c - the color of the category.
     */
    public void addCategory(String name, Color c) {
        chooser.addCategory(name, c);
    }
    
    /**
     * 
     * @return - the current script-pane.
     */
    public JScriptPane getScriptPane() {
        return pane;
    }
}
