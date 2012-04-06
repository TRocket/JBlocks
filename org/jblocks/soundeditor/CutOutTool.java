package org.jblocks.soundeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author ZeroLuck
 */
public class CutOutTool extends SoundEditorTool {

    private JSoundTrack currentCut;
    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent evt) {
            Component c = evt.getComponent();
            if (c instanceof JSoundTrack) {
                JSoundTrack track = (JSoundTrack) c;
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    if (currentCut != null && currentCut != c) {
                        resetCurrentCut();
                    }
                    currentCut = track;
                    Cutter cut = new Cutter(JSoundEditor.TRACK_HEIGHT);
                    cut.setLocation(evt.getX() - Cutter.ARC_DIM / 2, 0);
                    cut.setSize(cut.getPreferredSize());
                    track.add(cut, 0);
                    track.repaint();

                } else if (SwingUtilities.isRightMouseButton(evt)) {
                    Cutter before = null;
                    Cutter after = null;
                    int pos = evt.getX();
                    for (Component comp : track.getComponents()) {
                        if (comp instanceof Cutter) {
                            int compPos = ((Cutter) comp).getPos();
                            int bpos = 0;
                            int apos = track.getWidth();
                            if (after != null) {
                                apos = after.getPos();
                            }
                            if (before != null) {
                                bpos = before.getPos();
                            }
                            if (compPos < pos) {
                                if (pos - compPos < pos - bpos) {
                                    before = (Cutter) comp;
                                }
                            } else {
                                if (compPos - pos < apos - pos) {
                                    after = (Cutter) comp;
                                }
                            }
                        }
                    }
                    if (before != null && after != null) {
                        cutTrack(track, before.getPos(), after.getPos());
                    }
                }
            }
        }
    };
    
    private void cutTrack(JSoundTrack src, int start, int end) {
        if (start == end)
            return;
        
        if (end < start)
            throw new Error();
        
        Track t = src.getTrack();
        int spp = t.getSamplesPerPixel();
        Track trackA = new Track(t, 0        , start * spp);
        Track trackB = new Track(t, end * spp, t.getEnd() - t.getOffset());
        
        JSoundTrack a = new JSoundTrack(trackA, src.getHeight());
        JSoundTrack b = new JSoundTrack(trackB, src.getHeight());
        a.setLocation(src.getX(), src.getY());
        b.setLocation(src.getX() + end, src.getY());
        JTrackPane parent = (JTrackPane) src.getParent();
        parent.remove(src);
        parent.addTrack(a);
        parent.addTrack(b);
        parent.repaint();
    }

    public CutOutTool(JSoundEditor e) {
        super(e);
    }

    @Override
    public void install(JTrackPane p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.addMouseListener(mouseListener);
            }
        }
    }

    private void resetCurrentCut() {
        if (currentCut != null) {
            currentCut.removeAll();
            currentCut.repaint();
        }
    }

    @Override
    public void uninstall(JTrackPane p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.removeMouseListener(mouseListener);
            }
        }
        resetCurrentCut();
    }

    private static final class Cutter extends JComponent {

        // <global>
        private static final int ARC_DIM = 10;
        // <member>
        private final int height;

        public Cutter(int h) {
            height = h;
            addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent evt) {
                    setLocation(getX() - ARC_DIM / 2 + evt.getX(), getY());

                    getParent().repaint();
                }
            });
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent evt) {
                    Container parent = getParent();
                    parent.remove(Cutter.this);
                    parent.repaint();
                }
            });
            setOpaque(false);
        }

        public int getPos() {
            return getX() + ARC_DIM / 2;
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(ARC_DIM, height);
        }

        @Override
        public void paintComponent(Graphics grp) {
            Graphics2D g = (Graphics2D) grp;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g.setColor(Color.BLACK);
            Dimension size = getSize();
            g.drawLine(size.width / 2, 0, size.width / 2, size.height);
            g.setPaint(new GradientPaint(0, size.height / 2 - ARC_DIM / 2, Color.GRAY, 0, size.height / 2 + ARC_DIM / 2, Color.DARK_GRAY));
            g.fillOval(0, size.height / 2 - ARC_DIM / 2, ARC_DIM - 1, ARC_DIM);
            g.setColor(Color.BLACK);
            g.drawOval(0, size.height / 2 - ARC_DIM / 2, ARC_DIM - 1, ARC_DIM);


            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }
}
