package org.jblocks.painteditor2;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author ZeroLuck
 */
class LineLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String string, Component cmpnt) {
    }

    @Override
    public void removeLayoutComponent(Component cmpnt) {
    }
    
    private static final int ROW_GAP = 5;

    @Override
    public Dimension preferredLayoutSize(Container cntnr) {
        Insets border = cntnr.getInsets();
        int maxWidth = border.left + border.right;
        int maxHeight = border.top + border.bottom;
        for (Component c : cntnr.getComponents()) {
            Dimension pref = c.getPreferredSize();
            maxWidth = Math.max(maxWidth, pref.width + border.left + border.right);
            maxHeight += pref.height + ROW_GAP;
        }
        return new Dimension(maxWidth, maxHeight);
    }

    @Override
    public Dimension minimumLayoutSize(Container cntnr) {
        return preferredLayoutSize(cntnr);
    }

    @Override
    public void layoutContainer(Container cntnr) {
        Insets border = cntnr.getInsets();
        int yoff = border.top;
        int xoff = border.left;
        for (Component c : cntnr.getComponents()) {
            Dimension pref = c.getPreferredSize();
            c.setSize(pref);
            c.setLocation(xoff, yoff);
            yoff += pref.height + ROW_GAP;
        }
    }
    
}
