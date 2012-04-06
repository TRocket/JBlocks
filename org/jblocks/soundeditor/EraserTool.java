package org.jblocks.soundeditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;

/**
 *
 * @author ZeroLuck
 */
class EraserTool extends SoundEditorTool {

    private ImageIcon eraser;
    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent evt) {
            Container parent = evt.getComponent().getParent();
            parent.remove(evt.getComponent());
            parent.repaint();
        }
    };

    public EraserTool(JSoundEditor e, ImageIcon img) {
        super(e);
        eraser = img;
    }

    @Override
    public void install(JTrackPane p) {
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(eraser.getImage(), new Point(0, eraser.getIconHeight() - 1), "Eraser");
        p.setCursor(cursor);

        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.addMouseListener(mouseListener);
            }
        }
    }

    @Override
    public void uninstall(JTrackPane p) {
        p.setCursor(Cursor.getDefaultCursor());

        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.removeMouseListener(mouseListener);
            }
        }
    }
}
