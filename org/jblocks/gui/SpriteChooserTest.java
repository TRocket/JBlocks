package org.jblocks.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.jblocks.JBlocks;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.editor.JScriptPane;

/**
 * this class will be removed later... <br />
 * 
 * @author ZeroLuck
 */
class SpriteChooserTest {

    public static JSpriteChooser createTestSpriteChooser2(final JBlockEditor edt) {
        JSpriteChooser ch = new JSpriteChooser();

        for (int i = 0; i < 5; i++) {
            final JComponent comp = ch.addSpriteView(null,
                    "Sprite " + (i + 1),
                    JBlocks.getImage("jblocks-icon.png"));

            final JScriptPane pane = new JScriptPane();

            if (i == 0) {
                edt.setScriptPane(pane);
            }

            comp.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    edt.setScriptPane(pane);
                }
            });
        }
        return ch;
    }
}
