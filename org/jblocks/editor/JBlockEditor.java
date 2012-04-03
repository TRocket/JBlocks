package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
    private JScriptPane curr;
    private JScrollPane currScroll;
    private JCategoryChooser chooser;
    private HashMap<String, JScriptPane> ctgs = new HashMap<String, JScriptPane>();

    /**
     * creates a block-editor with an <b>empty</b> category-chooser. <br />
     * 
     * @see #addCategory(java.lang.String, java.awt.Color) 
     * @see #setScriptPane(org.jblocks.editor.JScriptPane) 
     */
    public JBlockEditor() {
        chooser = new JCategoryChooser();
        ctgPanel = new JPanel();
        ctgPanel.setLayout(new BorderLayout());
        ctgPanel.setBorder(BorderFactory.createBevelBorder(4, Color.yellow, Color.black));

        setLayout(new BorderLayout());
        add(ctgPanel, BorderLayout.WEST);
        ctgPanel.add(chooser, BorderLayout.NORTH);
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

    public void switchCategory(String name) {
        JScriptPane bck = curr;
        curr = ctgs.get(name);
        if (curr != null) {
            if (bck != null) {
                ctgPanel.remove(currScroll);
            }
            currScroll = new JScrollPane(curr);
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
        ctgs.put(name, p);

        if (firstCtg) {
            switchCategory(name);
            firstCtg = false;
        }
    }

    /**
     * Adds a new block to a category. <br />
     * 
     * 
     * @param name - the name of the category
     * @param b - the block
     */
    public void addBlock(String name, final AbstrBlock b) {
        final JScriptPane p = ctgs.get(name);
        if (p != null) {
            b.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    String syntax = b.getBlockSyntax();
                    String type = b.getBlockType();
                    if (syntax != null) {
                        try {
                            AbstrBlock block = JScriptPane.createBlock(type, syntax);
                            block.setBackground(b.getBackground());
                            JDragPane.DragFinishedHandler handler = new JDragPane.DragFinishedHandler() {

                                @Override
                                public void dragFinished(JDragPane jdrag, Component c, Point location) {
                                    Point p = javax.swing.SwingUtilities.
                                            convertPoint(jdrag, location, pane);
                                    
                                    // FIXME: use JScrollPane.visibleRect() (or similar)
                                    if (p.x < 0 || p.y < 0)
                                        return;
                                    
                                    pane.add(c);
                                    c.setLocation(p);
                                    pane.invalidate();
                                    pane.validate();
                                    pane.repaint();

                                    // this can be a problem in future.
                                    // <bad-code>
                                    ((AbstrBlock) c).releasedEvent(null);
                                    // </bad-code>
                                }
                            };
                            JDragPane.getDragPane(JBlockEditor.this).
                                    setDrag(block, p, b.getLocation(), evt.getPoint(), handler);

                        } catch (RuntimeException ex) {
                            ex.printStackTrace(System.err);
                        }
                    }
                }
            });
            p.add(b);
            p.cleanup();
        }
    }

    /**
     * Adds a new JComponent to the category 'name'.
     * 
     * @param name - the name of the category
     * @param c - the JComponent
     */
    public void addComponent(String name, JComponent c) {
        final JScriptPane p = ctgs.get(name);
        if (p != null) {
            p.add(c);
            p.cleanup();
        }
    }

    /**
     * 
     * @return - the current script-pane.
     */
    public JScriptPane getScriptPane() {
        return pane;
    }
}
