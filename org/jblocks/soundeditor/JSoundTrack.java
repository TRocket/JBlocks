package org.jblocks.soundeditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JComponent;

/**
 *
 * @author ZeroLuck
 */
class JSoundTrack extends JComponent {

    // <member>
    private final Track track;
    private final int height;
    private Point dragOff;
    
    private Point backupLocation;

    public JSoundTrack(Track t, int h) {
        setOpaque(true);
        this.track = t;
        this.height = h;

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {

                backupLocation = getLocation();
                dragOff = evt.getPoint();
                Container parent = getParent();
                if (parent instanceof JTrackPane) {
                    ((JTrackPane) parent).toFront(JSoundTrack.this);
                }
                JSoundTrack.this.requestFocus();
            }

            @Override
            public void mouseReleased(MouseEvent evt) {

                Container parent = getParent();
                if (parent instanceof JTrackPane) {
                    if (((JTrackPane) parent).locate(JSoundTrack.this)) {
                        return;
                    }
                }
                setLocation(backupLocation);
            }
        });

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                    Container c = getParent();
                    c.remove(JSoundTrack.this);
                    c.repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent evt) {

                setLocation(getX() + evt.getX() - dragOff.x,
                        getY() + evt.getY() - dragOff.y);
            }
        });
        setToolTipText("<HTML><b>Track:</b> <i>" + t.getName() + "</i></HTML>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(track.createUserView().length, height);
    }

    public Track getTrack() {
        return track;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        Color c = getForeground();
        Rectangle clip = g.getClipBounds();
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        g.setColor(c);

        short[] samples = track.createUserView();

        int xoff = clip.x;
        int lastY = samples[xoff];
        int cnt = Math.min(clip.x + clip.width, samples.length);
        for (int i = xoff; i < cnt; i++) {
            int a = (int) -((double) lastY / Short.MAX_VALUE * (height / 2)) + height / 2;
            int b = (int) -((double) samples[i] / Short.MAX_VALUE * (height / 2)) + height / 2;
            lastY = samples[i];
            g.drawLine(i, a, i, b);
        }

        g.setColor(Color.BLACK);
        g.drawLine(xoff, height / 2, cnt, height / 2);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);


    }

}
