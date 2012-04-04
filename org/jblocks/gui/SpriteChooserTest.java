package org.jblocks.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.jblocks.JBlocks;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.editor.JScriptPane;

/**
 * this class will be removed later... <br />
 * 
 * @author ZeroLuck
 */
public class SpriteChooserTest {

    public static JSpriteChooser createTestSpriteChooser2(final JBlockEditor edt) {
        JSpriteChooser ch = new JSpriteChooser();

        for (int i = 0; i < 5; i++) {
            final JComponent comp = ch.addSpriteView(null,
                    "Sprite " + (i + 1),
                    new ImageIcon(JBlocks.class.getResource("res/jblocks-icon.png")).getImage());

            final JScriptPane pane = new JScriptPane();
            pane.add(JScriptPane.createBlock("hat", "When %{gf} clicked"));
            pane.cleanup();

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

    public static JSpriteChooser createTestSpriteChooser() {
        JSpriteChooser ch = new JSpriteChooser();
        for (int i = 0; i < 5; i++) {
            ch.addSpriteView(null,
                    "Sprite " + (i + 1),
                    new ImageIcon(JBlocks.class.getResource("res/jblocks-icon.png")).getImage());
        }
        return ch;
    }

    public static void main(String[] args) {
        JBlocksPane.setLaF();

        JFrame frm = new JFrame("JSpriteChooser : Test");
        frm.setLocationByPlatform(true);
        frm.setLayout(new BorderLayout());
        frm.setSize(500, 400);

        JSpriteChooser ch = createTestSpriteChooser();
        
        frm.add(ch, BorderLayout.CENTER);
        frm.setVisible(true);
    }
}
