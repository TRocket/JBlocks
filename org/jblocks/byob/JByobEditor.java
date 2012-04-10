package org.jblocks.byob;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.JScriptPane;

/**
 *
 * @author ZeroLuck
 */
public class JByobEditor extends JPanel {

    // <member>
    private final JScriptPane script;
    private final AbstrBlock hat;
    private final AbstrBlock block;
    private final String category;
    private final List<ByobEditorListener> listeners;

    public static interface ByobEditorListener {

        /**
         * Called when the "cancel" button was pressed. <br />
         */
        public void cancel();

        /**
         * Called when the "OK" button was pressed. <br />
         */
        public void finished(String syntax, String category);
    }

    /**
     * @param type - the type of the block.
     * @param startLabel - the name of the block.
     * @param category - the category of the block.
     * @param c - the color of the category.
     */
    protected JByobEditor(String type, String startLabel, String category, Color c) {
        super(new BorderLayout());
        this.listeners = new ArrayList<ByobEditorListener>();
        this.script = new JScriptPane();
        this.category = category;
        script.setComponentPopupMenu(null);
        add(script, BorderLayout.CENTER);
        JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireCancelEvent();
            }
        });
        south.add(cancel);
        JButton OK = new JButton("OK");
        OK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                finished();
            }
        });
        south.add(OK);
        add(south, BorderLayout.SOUTH);

        hat = BlockFactory.createBlock("hat", "");
        block = BlockFactory.createBlock(type, "");
        block.add(new JLabel(startLabel));
        block.setBackground(c);
        block.add(new Plus());
        hat.setDraggable(false);
        block.setDraggable(false);
        hat.add(block);

        script.add(hat);
        script.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 350);
    }

    private void finished() {
        fireFinishEvent("hello", category);
    }

    private void fireCancelEvent() {
        for (ByobEditorListener m : listeners) {
            m.cancel();
        }
    }

    private void fireFinishEvent(final String syntax, final String category) {
        for (ByobEditorListener m : listeners) {
            m.finished(syntax, category);
        }
    }

    /**
     * Adds the specified ByobEditorListener. <br />
     * 
     * @throws IllegalArgumentException - if the listener is null.
     * @param m - the listener
     */
    public void addByobEditorListener(ByobEditorListener m) {
        if (m == null) {
            throw new IllegalArgumentException("'m' is null");
        }
        listeners.add(m);
    }

    /**
     * Removes the specified ByobEditorListener. <br />
     * 
     * @param m - the listener
     */
    public void removeByobEditorListener(ByobEditorListener m) {
        listeners.remove(m);
    }

    private static void createByobEditor(JDesktopPane desktop, Icon editorIcon, String type, String text, String category, Color c) {
        final JInternalFrame frm = new JInternalFrame("Make a block");
        if (editorIcon != null) {
            frm.setFrameIcon(editorIcon);
        }
        JByobEditor edt = new JByobEditor(type, text, category, c);
        edt.addByobEditorListener(new ByobEditorListener() {

            @Override
            public void cancel() {
                frm.dispose();
            }

            @Override
            public void finished(String syntax, String category) {
                frm.dispose();
            }
        });
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(edt, BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    /**
     * Creates a new JByobEditor and displays it on the specified JDesktopPane. <br />
     * 
     * @param desktop - the JDesktopPane
     * @param editorIcon - the icon for the created frames. (can be null)
     */
    public static void createEditor(final JDesktopPane desktop, final Icon editorIcon) {
        final JInternalFrame frm = new JInternalFrame("Make a block");
        frm.setClosable(true);
        if (editorIcon != null) {
            frm.setFrameIcon(editorIcon);
        }
        BlockTypeChooser chooser = new BlockTypeChooser();
        chooser.addBlockTypeListener(new BlockTypeChooser.BlockTypeChooserListener() {

            @Override
            public void cancel() {
                frm.dispose();
            }

            @Override
            public void finished(String type, String category, String label, Color c) {
                frm.dispose();
                createByobEditor(desktop, editorIcon, type, label, category, c);
            }
        });
        frm.setLayout(new BorderLayout());
        frm.add(chooser, BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

    }
}
