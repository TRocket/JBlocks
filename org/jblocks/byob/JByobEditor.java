package org.jblocks.byob;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.jblocks.JBlocks;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.editor.JBlockSequence;
import org.jblocks.editor.JScriptPane;
import org.jblocks.editor.JVariableInput;
import org.jblocks.editor.Puzzle;
import org.jblocks.editor.PuzzleAdapter;
import org.jblocks.editor.ScriptGrabber;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.ByobBlock;

/**
 *
 * @author ZeroLuck
 */
public class JByobEditor extends JPanel {

    // <member>
    private final JScriptPane script;
    private final AbstrBlock hat;
    private final AbstrBlock block;
    private final List<ByobEditorListener> listeners;
    private final Icon editorIcon;

    public static interface ByobEditorListener {

        /**
         * Called when the "cancel" button was pressed. <br />
         */
        public void cancel();

        /**
         * Called when the "OK" button was pressed. <br />
         */
        public void finished(AbstrBlock sctipts);
    }

    /**
     * @param type - the type of the block.
     * @param startLabel - the name of the block.
     * @param category - the category of the block.
     * @param c - the color of the category.
     */
    protected JByobEditor(final String type, final String startLabel,
            final String category, Color c, Icon icn) {

        super(new BorderLayout());
        this.listeners = new ArrayList<ByobEditorListener>();
        this.script = new JScriptPane();
        this.editorIcon = icn;
        this.script.setComponentPopupMenu(null);

        add(script, BorderLayout.CENTER);

        final JPanel south = new JPanel();
        south.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                fireCancelEvent();
            }
        });
        south.add(cancel);

        final JButton OK = new JButton("OK");
        OK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                finished();
            }
        });
        south.add(OK);
        add(south, BorderLayout.SOUTH);

        hat = BlockFactory.createBlock("hat", "");
        block = BlockFactory.createBlock(BlockModel.createModel(type, category, ""));
        block.add(createInput(InputTypeChooser.TYPE_TEXT, startLabel));
        block.setBackground(c);
        block.add(createPlus());
        hat.setDraggable(false);
        block.setDraggable(false);
        hat.add(block);

        script.add(hat);
        script.cleanup();

        block.addContainerListener(new ContainerListener() {

            @Override
            public void componentAdded(ContainerEvent ce) {
            }

            @Override
            public void componentRemoved(ContainerEvent ce) {
                plusCheck();
            }
        });
    }

    private void plusCheck() {
        Plus last = null;
        for (Component c : block.getComponents()) {
            if (c instanceof Plus) {
                if (last != null) {
                    block.remove(last);
                }
                last = (Plus) c;
            } else {
                last = null;
            }
        }
        validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 350);
    }

    private void finished() {
        fireFinishEvent();
    }

    private void fireCancelEvent() {
        for (ByobEditorListener m : listeners) {
            m.cancel();
        }
    }

    /**
     * Returns the Plus's JDesktopPane or null if it hasn't one. <br />
     */
    public JDesktopPane getDesktop() {
        Container cont = this;
        while ((cont = cont.getParent()) != null) {
            if (cont instanceof JDesktopPane) {
                return (JDesktopPane) cont;
            }
        }
        return null;
    }

    private Component createInput(String type, String label) {
        Component newc = null;
        if (type.equals(InputTypeChooser.TYPE_TEXT)) {
            newc = new JLabel(label);
        } else {
            newc = new JVariableInput(label);
        }
        if (newc instanceof JComponent) {
            final JComponent comp = (JComponent) newc;
            JPopupMenu menu = new JPopupMenu();
            JMenuItem itemDelete = new JMenuItem("delete");
            itemDelete.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    Container parent = comp.getParent();
                    if (parent != null) {
                        parent.remove(comp);
                        parent.validate();
                    }
                }
            });
            menu.add(itemDelete);
            comp.setComponentPopupMenu(menu);
        }
        return newc;
    }

    private void replacePlus(Plus p, String type, String label) {
        Component insert = createInput(type, label);
        int counter = 0;
        for (Component c : block.getComponents()) {
            if (c == p) {
                break;
            }
            counter++;
        }

        block.add(insert, counter);
        block.add(createPlus(), counter);
    }

    private Plus createPlus() {
        final Plus p = new Plus();
        p.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                final JInternalFrame frm = new JInternalFrame("Select an input type");
                frm.setFrameIcon(editorIcon);
                frm.setClosable(true);
                frm.setLayout(new BorderLayout());
                InputTypeChooser ch = new InputTypeChooser();
                ch.addInputTypeChooserListener(new InputTypeChooser.InputTypeChooserListener() {

                    @Override
                    public void cancel() {
                        frm.dispose();
                    }

                    @Override
                    public void finished(String type, String label) {
                        frm.dispose();
                        replacePlus(p, type, label);
                    }
                });
                frm.add(ch, BorderLayout.CENTER);
                frm.pack();

                JDesktopPane desktop = getDesktop();
                Point loc = SwingUtilities.convertPoint(JByobEditor.this, getLocation(), desktop);

                frm.setLocation(loc.x + getWidth() / 2 - frm.getWidth() / 2, loc.y + getHeight() / 2 - frm.getHeight() / 2);
                frm.setVisible(true);

                desktop.add(frm, 0);
                try {
                    frm.setSelected(true);
                } catch (PropertyVetoException ex) {
                }
            }
        });
        return p;
    }

    private void fireFinishEvent() {
        AbstrBlock[] pieces = JBlockSequence.getPuzzlePieces((Puzzle) hat, PuzzleAdapter.TYPE_DOWN);
        if (pieces.length <= 1) {
            Toolkit.getDefaultToolkit().beep();
            fireCancelEvent();
            return;
        }
        for (ByobEditorListener m : listeners) {
            m.finished(pieces[1]);
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

    private static String getSyntax(AbstrBlock b) {
        StringBuilder sb = new StringBuilder();
        for (Component c : b.getComponents()) {
            if (c instanceof JLabel) {
                sb.append(BlockFactory.enquote(((JLabel) c).getText()));
            } else if (c instanceof JVariableInput) {
                sb.append("%{r}");
            } else if (c instanceof Plus) {
                // do nothing
            } else {
                System.out.println("Warning: JByobEditor.getSyntax(): what to do with: " + c + "?");
            }
        }
        return sb.toString();
    }
    
    private static void createByobEditor(final JDesktopPane desktop,
            final Icon editorIcon, final String type,
            final String text, final String category,
            final Color c) {

        final JInternalFrame frm = new JInternalFrame("Make a block");
        if (editorIcon != null) {
            frm.setFrameIcon(editorIcon);
        }
        final JByobEditor edt = new JByobEditor(type, text, category, c, editorIcon);
        edt.addByobEditorListener(new ByobEditorListener() {

            @Override
            public void cancel() {
                frm.dispose();
            }

            @Override
            public void finished(final AbstrBlock script) {
                try {
                    final Block[] code = ScriptGrabber.getCodeFromScript(script);
                    final BlockModel bm = edt.block.getModel();
                    final BlockModel model = BlockModel.createModel(bm.getType(), bm.getCategory(), getSyntax(edt.block));
                    JBlocks ctx = JBlocks.getContextForComponent(edt);
                    model.setCode(new ByobBlock(count(model.getSyntax()), code));
                    ctx.installBlock(model);
                } catch (IllegalStateException ex) {
                    JOptionPane.showInternalMessageDialog(edt, "Couldn't create the block: \n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
                frm.dispose();
            }
        });
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(edt, BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);

        final Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    private static int count(String s) {
        int cnt = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '%' && (i + 1 >= s.length() || s.charAt(i + 1) != '%')) {
                cnt++;
            }
        }

        return cnt;
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
            public void finished(final String type, final String category, final String label, final Color c) {
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
