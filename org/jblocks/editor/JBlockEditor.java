package org.jblocks.editor;

import org.jblocks.gui.JCategoryChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.jblocks.JBlocks;
import org.jblocks.gui.JDragPane;

/**
 *
 * A block-editor class. <br />
 * The block-editor can switch between script-panes, <br />
 * and has a block-category system. <br />
 * 
 * @version 0.1
 * @author ZeroLuck
 */
public class JBlockEditor extends JPanel {

    // <global>
    private static BufferedImage scriptpane;

    static {
        try {
            scriptpane = ImageIO.read(JBlocks.class.getResourceAsStream("res/blockchooser.png"));
        } catch (IOException ex) {
            throw new java.lang.ExceptionInInitializerError(ex);
        }
    }
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
     * creates a block-editor with an <b>empty</b> category-chooser. <br />
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
        p.setScriptPaneImage(scriptpane);
        ctgs.put(name, new Category(name, p, c));

        if (firstCtg) {
            switchCategory(name);
            firstCtg = false;
        }
    }

    private static void getDragTargets0(List<JScriptPane> panes, Container cont) {
        for (Component c : cont.getComponents()) {
            if (c instanceof JScriptPane) {
                panes.add((JScriptPane) c);
            }
            if (c instanceof Container) {
                getDragTargets0(panes, (Container) c);
            }
        }
    }

    /**
     * Returns all JScriptPanes on the editor's JDragPane. <br />
     */
    private JScriptPane[] getDragTargets() {
        List<JScriptPane> panes = new ArrayList<JScriptPane>();
        JDragPane root = JDragPane.getDragPane(this);
        getDragTargets0(panes, root);
        return panes.toArray(new JScriptPane[]{});
    }

    /**
     * Adds a new block to a category. <br />
     * 
     * 
     * @param name - the name of the category
     * @param b - the block
     */
    public void addBlock(String name, final AbstrBlock b) {
        final Category c = ctgs.get(name);
        if (c != null) {
            b.setBackground(c.color);
            b.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    String syntax = b.getBlockSyntax();
                    String type = b.getBlockType();
                    if (syntax != null) {
                        try {
                            AbstrBlock block = BlockFactory.createBlock(type, syntax);
                            block.setBackground(b.getBackground());
                            JDragPane.DragFinishedHandler handler = new JDragPane.DragFinishedHandler() {

                                @Override
                                public void dragFinished(JDragPane jdrag, Component c, Point location) {
                                    JScriptPane[] allTargets = getDragTargets();
                                    JScriptPane target = null;
                                    for (JScriptPane p : allTargets) {
                                        Rectangle rect = SwingUtilities.convertRectangle(p, p.getVisibleRect(), jdrag);
                                        if (p.isDragEnabled() && rect.contains(location)) {
                                            target = p;
                                            break;
                                        }
                                    }
                                    if (target != null) {
                                        Point p = SwingUtilities.convertPoint(jdrag, location, target);

                                        AbstrBlock dragBlock = (AbstrBlock) c;

                                        target.add(c);
                                        c.setLocation(p);
                                        target.invalidate();
                                        target.validate();
                                        target.repaint();

                                        // this can be a problem in future.
                                        // <fixme>
                                        dragBlock.releasedEvent(null);
                                        // </fixme>

                                        dragBlock.toFront();
                                    }
                                }
                            };
                            JDragPane.getDragPane(JBlockEditor.this).
                                    setDrag(block, c.blocks, b.getLocation(), evt.getPoint(), handler);

                        } catch (RuntimeException ex) {
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
