package org.jblocks.byob;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.gui.JCategoryChooser;
import org.jblocks.gui.JBlocksPane;

/**
 *
 * @author ZeroLuck
 */
public class BlockTypeChooser extends JPanel {

    // <global>
    public static String TYPE_REPORTER = "reporter";
    public static String TYPE_COMMAND = "command";
    public static String TYPE_BOOLEAN = "boolean";
    // <member>
    private JCategoryChooser categories;
    private AbstrBlock command;
    private AbstrBlock reporter;
    private AbstrBlock bool;
    private Color currColor;
    private AbstrBlock selected;
    private String currCategory;
    private JTextField blockName;
    private List<BlockTypeChooserListener> listeners;

    public static interface BlockTypeChooserListener {

        /**
         * Called when the "cancel" button was pressed. <br />
         */
        public void cancel();

        /**
         * Called when the "OK" button was pressed. <br />
         * 
         * @see #TYPE_COMMAND
         * @see #TYPE_REPORTER
         * @see #TYPE_BOOLEAN
         * @param type - the type of the new block.
         * @param category - the category of the new block.
         * @param label - the name of the block.
         */
        public void finished(String type, String category, String label, Color c);
    }

    public BlockTypeChooser() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        categories = new JCategoryChooser();
        command = BlockFactory.createBlock("command", "command");
        reporter = BlockFactory.createBlock("reporter", "reporter");
        bool = BlockFactory.createBlock("boolean", "boolean");
        selected = command;
        listeners = new ArrayList<BlockTypeChooserListener>();

        MouseListener ma = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                Component src = evt.getComponent();
                if (src != null && src instanceof AbstrBlock) {
                    if (selected != null) {
                        selected.setBorder(null);
                    }
                    selected = (AbstrBlock) src;
                    resetColors();
                }
            }
        };
        bool.addMouseListener(ma);
        reporter.addMouseListener(ma);
        command.addMouseListener(ma);

        JPanel north = new JPanel(new FlowLayout());
        // <test>
        addCategory("Control", new Color(0xD6900A));
        addCategory("Motion", new Color(0xff4a6cd6));
        addCategory("Operators", new Color(0xff62c213));
        addCategory("Lists", Color.RED);

        addCategory("Touching", Color.CYAN);
        addCategory("Pen", Color.GREEN.darker());
        addCategory("Sound", Color.MAGENTA);
        addCategory("Looking", Color.MAGENTA.darker());

        currColor = new Color(0xD6900A);
        currCategory = "Control";
        // </test>


        JPanel ctg = new JPanel(new BorderLayout());
        ctg.add(categories, BorderLayout.CENTER);
        ctg.setBorder(BorderFactory.createTitledBorder("Category"));

        north.add(ctg);
        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        JPanel centerFlow = new JPanel(new FlowLayout());
        centerFlow.add(command);
        centerFlow.add(reporter);
        centerFlow.add(bool);

        center.add(centerFlow, BorderLayout.CENTER);

        blockName = new JTextField(15);

        center.add(blockName, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton OK = new JButton("OK");
        OK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (blockName.getText().trim().isEmpty()) {
                    cancel();
                    return;
                }
                finished();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cancel();
            }
        });
        south.add(OK);
        south.add(cancel);

        add(south, BorderLayout.SOUTH);
    }

    private void resetColors() {
        reporter.setBackground(currColor);
        command.setBackground(currColor);
        bool.setBackground(currColor);

        selected.setBackground(currColor.brighter());
        selected.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        reporter.repaint();
        command.repaint();
        bool.repaint();
    }

    private void cancel() {
        for (BlockTypeChooserListener m : listeners) {
            m.cancel();
        }
    }

    private void finished() {
        String type = TYPE_COMMAND;
        if (selected == bool) {
            type = TYPE_BOOLEAN;
        } else if (selected == reporter) {
            type = TYPE_REPORTER;
        }
        String name = blockName.getText();
        for (BlockTypeChooserListener m : listeners) {
            m.finished(type, currCategory, name, currColor);
        }
    }

    /**
     * Adds the specified BlockTypeChooserListener. <br />
     * 
     * @throws IllegalArgumentException - if 'm' is null.
     * @param m - the listener
     */
    public void addBlockTypeListener(BlockTypeChooserListener m) {
        if (m == null) {
            throw new IllegalArgumentException("'m' is null!");
        }
        listeners.add(m);
    }

    /**
     * Removes the specified BlockTypeChooserListener. <br />
     * 
     * @param m - the listener
     */
    public void removeBlockTypeListener(BlockTypeChooserListener m) {
        listeners.remove(m);
    }

    private void changeBlockCategory(String name, Color c) {
        currColor = c;
        currCategory = name;
        resetColors();
    }

    private void addCategory(final String name, final Color c) {
        JComponent comp = categories.addCategory(name, c);
        comp.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                changeBlockCategory(name, c);
            }
        });
    }

    public static void main(String[] args) {
        JBlocksPane.setLaF();

        JFrame frm = new JFrame("JByobEditor : Test");
        frm.setLayout(new BorderLayout());
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationByPlatform(true);
        frm.add(new BlockTypeChooser(), BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);
    }
}
