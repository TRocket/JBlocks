package org.jblocks.cyob;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A {@code JTabbedPane} which has a close icon on each tab.
 * To add a tab, use the method {@link #addTab(java.lang.String, java.awt.Component) }
 * <p />
 * To have an extra icon on each tab (e.g. like in Netbeans, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
class CloseableJTabbedPane extends JTabbedPane implements MouseListener {

    @SuppressWarnings("LeakingThisInConstructor")
    public CloseableJTabbedPane() {
        super();
        addMouseListener(this);
    }

    @Override
    public void addTab(String title, Component component) {
        this.addTab(title, component, null);
    }

    public void addTab(String title, Component component, Icon extraIcon) {
        super.addTab(title + " ", new CloseTabIcon(extraIcon), component);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber < 0) {
            return;
        }
        Rectangle rect = ((CloseTabIcon) getIconAt(tabNumber)).getBounds();
        if (rect.contains(e.getX(), e.getY())) {
            this.removeTabAt(tabNumber);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    private class CloseTabIcon implements Icon {

        private int x_pos;
        private int y_pos;
        private int width;
        private int height;
        private Icon fileIcon;

        public CloseTabIcon(Icon fileIcon) {
            this.fileIcon = fileIcon;
            width = 16;
            height = 16;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            this.x_pos = x;
            this.y_pos = y;

            Color col = g.getColor();

            g.setColor(Color.black);
            int y_p = y + 2;
            g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
            g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
            g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
            g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
            g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
            g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
            g.setColor(col);
            if (fileIcon != null) {
                fileIcon.paintIcon(c, g, x + width, y_p);
            }
        }

        @Override
        public int getIconWidth() {
            return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        public Rectangle getBounds() {
            return new Rectangle(x_pos, y_pos, width, height);
        }
    }
}
