package org.jblocks.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.jblocks.JBlocks;
import org.jblocks.gui.JDragPane;
import org.jblocks.utils.StreamUtils;
import org.jblocks.utils.SwingUtils;

/**
 * * A <code>JPopupBlockMenu</code> has these standard <code>JMenuItem</code>s:
 * <ul>
 *    <li>"duplicate" - makes a new instanceof the script/block</li>
 *    <li>"delete" - deletes the script/block</li>
 *    <li>"share" - shares the script/block in the block-store.</li>
 *    <li>"save picture of script" - saves a pictute of the script/block to the hard drive</li>
 *    <li>"save script" - saves the script/block to the hard drive</li>
 * </ul>
 * 
 * @author ZeroLuck    
 */
public class JPopupBlockMenu extends JPopupMenu implements ActionListener {

    private AbstrBlock parent;

    /**
     * Creates a new <code>JPopupBlockMenu</code> for a specified block. <br />
     * A <code>JPopupBlockMenu</code> has these standard <code>JMenuItem</code>s:
     * <ul>
     *    <li>"duplicate" - makes a new instanceof the script/block</li>
     *    <li>"delete" - deletes the script/block</li>
     *    <li>"share" - shares the script/block in the block-store.</li>
     *    <li>"save picture of script" - saves a pictute of the script/block to the hard drive</li>
     *    <li>"save script" - saves the script/block to the hard drive</li>
     * </ul>
     * 
     * @param b the block
     * @throws IllegalArgumentException if 'b' is null
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public JPopupBlockMenu(AbstrBlock b) {
        if (b == null) {
            throw new IllegalArgumentException("'b' is null");
        }
        parent = b;

        this.add("duplicate").addActionListener(this);
        if (parent instanceof JHatBlock) {
            this.add("delete").addActionListener(this);
        }
        this.addSeparator();

        if (!JBlocks.getContextForComponent(parent).isDefaultBlock(parent.getModel().getID())) {
            this.add("share").addActionListener(this);
        }

        this.add("save picture of script").addActionListener(this);

        this.addSeparator();
        this.add("save script").addActionListener(this);
    }

    private void deleteBlock() {
        JScriptPane scripts = parent.getScriptPane();
        if (scripts != null) {
            scripts.removeScript((Puzzle) parent);
            scripts.repaint();
        }
    }

    private void duplicateBlock() {
        JBlocks ctx = JBlocks.getContextForComponent(parent);
        // cheat:
        AbstrBlock[] block = BlockIO.createScript(ctx, BlockIO.createCode(ctx, parent));

        if (block[0] instanceof JHatBlock) {
            for (AbstrBlock b : block) {
                b.setSize(b.getPreferredSize());
            }
            Drag.dragPuzzle(JDragPane.getDragPane(parent), parent, new Point(10, 10), block[0]);
        } else {
            Drag.drag(JDragPane.getDragPane(parent), parent, new Point(10, 10), block[0]);
        }
    }

    private JFileChooser showFileChooser(String title) {
        JBlocks ctx = JBlocks.getContextForComponent(parent);
        JFileChooser ch = new JFileChooser();
        ch.setMultiSelectionEnabled(false);
        ch.setDialogType(JFileChooser.SAVE_DIALOG);
        ch.setDialogTitle(title);
        SwingUtils.showInternalFileChooser(ctx.getDesktop(), ch);
        return ch;
    }

    private void shareBlock() {
        // TODO
        try {
            org.jblocks.blockstore.BlockStoreServer.uploadBlock(JBlocks.getContextForComponent(parent), parent.getModel());

            JOptionPane.showInternalMessageDialog(JBlocks.getContextForComponent(parent).getDesktop(),
                    "The block was uploaded successfull!");

        } catch (Exception io) {
            JOptionPane.showInternalMessageDialog(JBlocks.getContextForComponent(parent).getDesktop(),
                    "The block couldn't be uploaded!\n"
                    + "The error can be: You tried to upload an already existing block.\n\n" + io, "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Dimension calculateSize(Puzzle p) {
        AbstrBlock[] pieces = JBlockSequence.getPuzzlePieces(p, PuzzleAdapter.TYPE_DOWN);
        int minY = pieces[0].getY();
        int maxW = 0;
        int maxH = 0;
        for (AbstrBlock b : pieces) {
            Dimension size = b.getSize();
            if (size.width > maxW) {
                maxW = size.width;
            }
            if (size.height + b.getY() - minY > maxH) {
                maxH = size.height + b.getY() - minY;
            }
        }
        return new Dimension(maxW, maxH);
    }

    private void savePicture() {
        final JBlocks ctx = JBlocks.getContextForComponent(parent);
        final JFileChooser ch = showFileChooser("Save picture of script");

        BufferedImage img = null;

        try {
            Dimension dim;
            if (parent instanceof Puzzle) {
                dim = calculateSize((Puzzle) parent);
            } else {
                dim = parent.getSize();
            }
            img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            AbstrBlock[] pieces;
            if (parent instanceof Puzzle) {
                pieces = JBlockSequence.getPuzzlePieces((Puzzle) parent, PuzzleAdapter.TYPE_DOWN);
            } else {
                pieces = new AbstrBlock[]{parent};
            }
            for (AbstrBlock b : pieces) {
                g.translate(0, b.getY() - pieces[0].getY());
                int h = b.getHeight();
                int w = b.getWidth();
                g.setClip(0, 0, w, h);
                b.paintAll(g);
                g.translate(0, -(b.getY() - pieces[0].getY()));
            }
        } catch (OutOfMemoryError out) {
            JOptionPane.showInternalMessageDialog(ctx.getDesktop(),
                    "Error: Not enough memory to create the picture!\n" + out, "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        final BufferedImage finalImg = img;
        ch.setFileFilter(SwingUtils.createFilter(new String[]{"png"}, "Portable-network-graphics (PNG) files"));
        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (SwingUtils.isApproveSelection(ae)) {
                    File f = ch.getSelectedFile();
                    if (f != null) {
                        try {
                            ImageIO.write(finalImg, "png", new File(StreamUtils.addFileExtension(f.getAbsolutePath(), "png")));
                        } catch (Exception io) {
                            JOptionPane.showInternalMessageDialog(ctx.getDesktop(),
                                    "Error: Can't save the picture!\n" + io, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void saveScript() {
        final JBlocks ctx = JBlocks.getContextForComponent(parent);
        final JFileChooser ch = showFileChooser("Save script");
        ch.setFileFilter(SwingUtils.createFilter(new String[]{"jbs"}, "JBlocks script files (JBS)"));
        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (SwingUtils.isApproveSelection(ae)) {
                    File f = ch.getSelectedFile();
                    if (f != null) {
                        try {
                            BlockIO.writeToXML(new FileOutputStream(
                                    StreamUtils.addFileExtension(f.getAbsolutePath(), "jbs")),
                                    BlockIO.createCode(ctx, parent));
                        } catch (Exception io) {
                            JOptionPane.showInternalMessageDialog(ctx.getDesktop(),
                                    "Error: Can't save the script!\n" + io, "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals("delete")) {
            deleteBlock();
        } else if (command.equals("duplicate")) {
            duplicateBlock();
        } else if (command.equals("share")) {
            shareBlock();
        } else if (command.equals("save picture of script")) {
            savePicture();
        } else if (command.equals("save script")) {
            saveScript();
        }
    }
}
