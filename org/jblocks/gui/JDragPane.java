package org.jblocks.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
 *
 * This class is needed by the JBlockEditor for dragging blocks over the <br />
 * whole Main-GUI. <br />
 * 
 * @author ZeroLuck
 */
public class JDragPane extends JPanel {

    private final Component view;
    private final DragGlassPane glassPane;
    private Component drag;
    private Point dragOff;

    public static interface DragFinishedHandler {

        public void dragFinished(JDragPane pane, Component c, Point location);
    }

    public JDragPane(Component c) {
        super(null);
        glassPane = new DragGlassPane();
        view = c;
        add(c);

        glassPane.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                handleEvent(me, 0, 0);
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                handleEvent(me, 0, 0);
            }
        });
    }
    
    /**
     * Returns the displayed component in the background. <br />
     * (Setted in the constructor) <br />
     * <br />
     * Note: The <i>view</i> isn't the current drag! <br />
     * 
     */
    public Component getView() {
        return view;
    }

    /**
     * @param evt the MouseEvent
     * @param dx delta-X to MouseEvent.getPoint()
     * @param dy delta-Y to MouseEvent.getPoint()
     */
    private void handleEvent(MouseEvent evt, int dx, int dy) {
        drag.setLocation(evt.getX() - dragOff.x + dx, evt.getY() - dragOff.y + dy);
        if (drag instanceof org.jblocks.editor.Puzzle) {
            ((org.jblocks.editor.Puzzle) drag).layoutPuzzle();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return view.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            if (c == glassPane || c == view) {
                c.setSize(getSize());
            }
        }
    }

    /**
     * Finds the JDragPane for the component 'c'. <br />
     * 
     * @param c - the component.
     * @return - the JDragPane for this component, or null.
     */
    public static JDragPane getDragPane(Component c) {
        Container cont = c.getParent();
        while (cont != null) {
            if (cont instanceof JDragPane) {
                return (JDragPane) cont;
            }

            cont = cont.getParent();
        }
        return null;
    }

    private static Point getLocationOnDragPane(Component c) {
        Point p = new Point(c.getLocation());
        Container cont = c.getParent();
        while (cont != null) {
            p.x += cont.getX();
            p.y += cont.getY();
            cont = cont.getParent();
            if (cont instanceof JDragPane) {
                break;
            }
        }
        return p;
    }
    
    /**
     * Sets the JDragPane's curent drag. <br />
     * 
     * @throws NullPointerException - if one parameter is null.
     * @param c - the component which to drag.
     * @param cont - the container, used for getting the absolute location of
     *      'relLocation'.
     * @param relLocation - the relative location this component should have to 'cont'.
     * @param off - should be MouseEvent.getPoint()
     * @param handler - the handler which will be called when dragging is finished.
     */
    public void setDrag(Component c, Container cont, Point relLocation, Point off,
            final DragFinishedHandler handler) {
        if (drag != null) {
            return;
        }
        
        Point loc = getLocationOnDragPane(cont);
        loc.translate(relLocation.x, relLocation.y);

        drag = c;
        dragOff = off;
        add(glassPane, 0);

        add(c, 0);
        c.setLocation(loc);
        c.setSize(c.getPreferredSize());
        
        final MouseMotionListener motion = new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                handleEvent(me, drag.getX(), drag.getY());
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                handleEvent(me, drag.getX(), drag.getY());
            }
        };
        final MouseListener mouse = new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent evt) {
                stopDrag(motion, this, handler);
            }
        };
        glassPane.addMouseListener(mouse);
        drag.addMouseMotionListener(motion);
        drag.addMouseListener(mouse);
        doLayout();
    }

    private void stopDrag(MouseMotionListener motion, MouseListener mouse, DragFinishedHandler handler) {
        drag.removeMouseMotionListener(motion);
        drag.removeMouseListener(mouse);
        glassPane.removeMouseListener(mouse);
        Point p = drag.getLocation();
        remove(drag);
        remove(glassPane);
        handler.dragFinished(this, drag, p);
        drag = null;
        repaint();
    }

    private class DragGlassPane extends JPanel {

        public DragGlassPane() {
            super(null);
            setOpaque(false);
        }
    }
}
