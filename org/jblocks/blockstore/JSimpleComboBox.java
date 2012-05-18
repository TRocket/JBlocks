package org.jblocks.blockstore;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author ZeroLuck
 */
public class JSimpleComboBox<E extends JComponent> extends JComponent {

    private List<E> items = new ArrayList<E>();
    private int selected = -1;

    public JSimpleComboBox() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                selectNext();
            }
        });
    }

    public void selectNext() {
        select(++selected >= items.size() ? 0 : selected);
    }

    public void select(int i) {
        selected = i;
        removeAll();
        add(new NoContainsPanel(items.get(i)));
        invalidate();
        validate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = new Dimension(0, 0);
        for (Component c : getComponents()) {
            Dimension dim = c.getPreferredSize();
            size.width = Math.max(size.width, dim.width);
            size.height = Math.max(size.height, dim.height);
        }
        return size;
    }

    @Override
    public void doLayout() {
        Dimension size = getPreferredSize();
        for (Component c : getComponents()) {
            Dimension dim = c.getPreferredSize();

            c.setLocation(size.width / 2 - dim.width / 2, size.height / 2 - dim.height / 2);
            c.setSize(dim);
        }
        setSize(size);
        getParent().doLayout();
    }

    public void addItem(E e) {
        items.add(e);
        if (selected == -1) {
            select(0);
        }
    }

    public int getSelectedIndex() {
        return selected;
    }

    public E getSelectedItem() {
        if (selected < 0) {
            throw new IllegalStateException("No selected element available.");
        }
        return items.get(selected);
    }
    
    static class NoContainsPanel extends JPanel {
        
        public NoContainsPanel(JComponent c) {
            super(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
        
        @Override
        public boolean contains(int x, int y) {
            return false;
        }
    }
}
