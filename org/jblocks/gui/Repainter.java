package org.jblocks.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * This is a custom RepaintManager for JBlocks. <br />
 * Repainting can be delayed with {@link #disable(java.awt.Component) }
 * until {@link #enable(java.awt.Component) } is called. <br />
 * <br />
 * The RepaintManager can be setted with {@link #install()}.
 * 
 * @author ZeroLuck
 */
public class Repainter extends RepaintManager {

    private boolean enabled = true;
    private List<Dirty> dirty = new ArrayList<Dirty>();

    private static class Dirty {

        final int x, y, w, h;
        final JComponent c;

        public Dirty(JComponent c, int x, int y, int w, int h) {
            this.c = c;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        if (enabled) {
            super.addDirtyRegion(c, x, y, w, h);
        } else {
            dirty.add(new Dirty(c, x, y, w, h));
        }
    }

    /**
     * Enables/Disables painting. <br />
     * <ul>
     *     <li>
     *          If <code>b == true</code>: <br />
     *          If painting is disabled, all dirty-regions which were added since
     *          painting was disabled will be repainted immediately.
     *     </li>
     *     <li>
     *          If <code>b == false</code>: <br />
     *          All <code>addDirtyRegion()</code> calls will be delayed until the
     *          <code>setEnabled(true)</code> is called. <br />
     *     </li>
     * </ul>
     */
    public void setEnabled(boolean b) {
        if (!enabled) {
            for (Dirty c : dirty) {
                super.addDirtyRegion(c.c, c.x, c.y, c.w, c.h);
            }
            dirty.clear();
            super.paintDirtyRegions();
        }
        enabled = b;
        if (!enabled) {
            super.paintDirtyRegions();
        }
    }

    private static Repainter getFor(Component c) {
        RepaintManager mng = RepaintManager.currentManager(c);
        if (mng == null || !(mng instanceof Repainter)) {
            throw new IllegalStateException("the current RepaintManager isn't an instance of Repainter");
        }
        return (Repainter) mng;
    }

    /**
     * Enables painting. <br />
     * For more informations please see {@link #setEnabled(boolean) }
     * 
     * @throws IllegalStateException if the current RepaintManager isn't an instance of Repainter.
     * @see #setEnabled(boolean) 
     * @see javax.swing.RepaintManager#currentManager(java.awt.Component)
     */
    public static void enable(Component c) {
        Repainter r = getFor(c);
        r.setEnabled(true);
    }

    /**
     * Disables painting. <br />
     * For more informations please see {@link #setEnabled(boolean) }
     * 
     * @throws IllegalStateException if the current RepaintManager isn't an instance of Repainter.
     * @see #setEnabled(boolean) 
     * @see javax.swing.RepaintManager#currentManager(java.awt.Component)  
     */
    public static void disable(Component c) {
        Repainter r = getFor(c);
        r.setEnabled(false);
    }

    /**
     * Sets the RepaintManager to this one. <br />
     * @see javax.swing.RepaintManager#setCurrentManager(javax.swing.RepaintManager) 
     */
    public static void install() {
        RepaintManager.setCurrentManager(new Repainter());
    }
}
