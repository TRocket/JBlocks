package org.jblocks.editor;

import javax.swing.JPanel;

/**
 *
 * @author ZeroLuck
 */
public class JBlockEditor extends JPanel {

    private JScriptPane pane;
    
    public void setCurrentScriptPane(JScriptPane p) {
        if (pane != null) {
            remove(pane);
        }
        pane = p;
        if (pane != null) {
            add(pane);
        }
    }

    public JScriptPane getCurrentScriptPane() {
        return pane;
    }
}
