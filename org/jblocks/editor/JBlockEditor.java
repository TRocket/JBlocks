package org.jblocks.editor;

import org.jblocks.gui.JCategoryChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jblocks.JBlocks;
import org.jblocks.gui.JDragPane;

/**
 * <p>
 * A JBlockEditor can switch between several {@link org.jblocks.editor.JScriptPane}s. <br />
 * The editor has a build-in {@link org.jblocks.gui.JCategoryChooser} for
 * choosing blocks and dragging them on the current JScriptPane. <br />
 * </p>
 * 
 * <p>
 * The JBlockEditor has to be a (indirect) children of a {@link org.jblocks.gui.JDragPane}. <br />
 * Dragging from this JBlockEditor to another JBlockEditor is possible
 * if the other JBlockEditor is a (indirect) children of the same JDragPane.
 * </p>
 * 
 * @see org.jblocks.gui.JCategoryChooser
 * @see org.jblocks.gui.JDragPane
 * @author ZeroLuck
 */
public class JBlockEditor extends JPanel {
    
    // <member>
    private JPanel ctgPanel;
    private JScriptPane pane;
    private JScrollPane paneScroll;
    private Category curr;
    private JScrollPane currScroll;
    private JCategoryChooser chooser;
    private HashMap<String, Category> ctgs = new HashMap<String, Category>(30);

    public static class Category {

        private final String name;
        private final JScriptPane blocks;
        private final Color color;

        public Category(String n, JScriptPane p, Color c) {
            this.name = n;
            this.blocks = p;
            this.color = c;
        }

        public Color getColor() {
            return color;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Creates a block-editor with an <b>empty</b> category-chooser. <br />
     * 
     * @see #addCategory(java.lang.String, java.awt.Color) 
     * @see #setScriptPane(org.jblocks.editor.JScriptPane) 
     */
    public JBlockEditor() {
        chooser = new JCategoryChooser(2);
        ctgPanel = new JPanel();
        ctgPanel.setLayout(new BorderLayout());
        ctgPanel.setBorder(BorderFactory.createBevelBorder(4, Color.yellow, Color.black));

        setLayout(new BorderLayout());
        add(ctgPanel, BorderLayout.WEST);
        ctgPanel.add(chooser, BorderLayout.NORTH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNotify() {
        super.addNotify();
        cleanup();
    }

    /**
     * switches between script-panes. <br />
     * 
     * @see #getScriptPane() 
     * @param p - the new script pane to display.
     */
    public void setScriptPane(JScriptPane p) {
        if (paneScroll != null) {
            remove(paneScroll);
        }
        pane = p;
        paneScroll = null;
        if (p != null) {
            paneScroll = new JScrollPane(pane);
            add(paneScroll, BorderLayout.CENTER);
            invalidate();
            validate();
            repaint();
        }
    }

    /**
     * Switchs to a category. <br />
     * This is usually called from the block-chooser itself. <br />
     * 
     * @param name - the name of the category
     */
    public void switchCategory(String name) {
        Category bck = curr;
        curr = ctgs.get(name);
        if (curr != null) {
            if (bck != null) {
                ctgPanel.remove(currScroll);
            }

            curr.blocks.setMaximumSize(new Dimension(chooser.getPreferredSize().width, Integer.MAX_VALUE));
            currScroll = new JScrollPane(curr.blocks);
            ctgPanel.add(currScroll, BorderLayout.CENTER);
            ctgPanel.invalidate();
            ctgPanel.validate();
        }
    }
    private boolean firstCtg = true;

    /**
     * Adds a new category to the block-chooser. <br />
     * 
     * @see JCategoryChooser#addCategory(java.lang.String, java.awt.Color)
     * @param name - the name of the category.
     * @param c - the color of the category.
     */
    public void addCategory(final String name, Color c) {
        if (ctgs.containsKey(name)) {
            return;
        }
        JComponent comp = chooser.addCategory(name, c);
        comp.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                switchCategory(name);
            }
        });
        JScriptPane p = new JScriptPane(false);
        p.setDragEnabled(false);
        p.setScriptPaneImage(JBlocks.getImage("blockchooser.png"));
        ctgs.put(name, new Category(name, p, c));

        if (firstCtg) {
            switchCategory(name);
            firstCtg = false;
        }
    }

    /**
     * Adds a new block to the JBlockEditor. <br />
     * 
     * @throws NullPointerException if 'b' has no BlockModel
     * @param b - the block model
     */
    public void addBlock(final AbstrBlock b) {
        final BlockModel model = b.getModel();
        final Category c = ctgs.get(model.getCategory());
        
        if (c != null) {
            b.setBackground(c.color);
            b.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    if (model != null) {
                        try {
                            AbstrBlock block = BlockFactory.createBlock(model);
                            block.setBackground(b.getBackground());
                            block.setLocation(b.getLocation());
                            Drag.drag(JDragPane.getDragPane(JBlockEditor.this), c.blocks, evt.getPoint(), block);
                        } catch (RuntimeException ex) {
                            // syntax error in BlockFactory...
                            ex.printStackTrace(System.err);
                        }
                    }
                }
            });
            b.setLocation(0, Integer.MAX_VALUE);
            c.blocks.add(b);
            c.blocks.cleanup();
        }
    }

    /**
     * Adds a new JComponent to the category 'name'.
     * 
     * @param name - the name of the category
     * @param c - the JComponent
     */
    public void addComponent(String name, JComponent c) {
        final Category cg = ctgs.get(name);
        if (cg != null) {
            cg.blocks.add(c);
        }
    }

    /**
     * @param name - the name of the category.
     * @return the category with the specified name or null. 
     */
    public Category getCategory(String name) {
        return ctgs.get(name);
    }

    /**
     * 
     * @return - the current script-pane.
     */
    public JScriptPane getScriptPane() {
        return pane;
    }

    /**
     * "Layouts" the JBlockEditor. <br />
     */
    public void cleanup() {
        for (Category c : ctgs.values()) {
            c.blocks.cleanup();
        }
    }
}
