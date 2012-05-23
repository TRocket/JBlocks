package org.jblocks.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.MutableAttributeSet;
import org.jblocks.JBlocks;
import org.jblocks.cyob.JCodePane;
import org.jblocks.cyob.JCyobEditor;
import org.jblocks.cyob.MultiSyntaxDocument;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.JDragPane;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.ByobBlock;
import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.scriptengine.StorableNativeBlock;
import org.jblocks.scriptengine.js.impl.JsBeautifier;
import org.jblocks.scriptengine.js.impl.JsScriptEngine;
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

    private final AbstrBlock parent;

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
        this.add("save script").addActionListener(this);
        this.addSeparator();
        this.add("view JavaScript").addActionListener(this);

        BlockModel model = parent.getModel();
        if (model != null) {
            Block code = model.getCode();
            if (code instanceof ByobBlock || code instanceof StorableNativeBlock) {
                this.addSeparator();
                this.add("edit").addActionListener(this);
            }
        }
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

    private void viewJavaScript() {
        try {
            final JsScriptEngine tmpEngine = new JsScriptEngine(false); // <- not for executing, just compiling
            final Block[] blocks;
            if (parent instanceof Puzzle) {
                blocks = Script2Code.getCodeFromScript(parent);
            } else {
                blocks = new Block[]{Script2Code.getCodeFromBlock(parent)};
            }
            final String code = JsBeautifier.work(tmpEngine.compileToJavaScriptCode(blocks));
            final JInternalFrame frm = new JInternalFrame("JavaScript-Viewer (by ZeroLuck)");
            final JPanel pane = new JPanel(new BorderLayout());

            Map<String, MutableAttributeSet> jsKeywords = new HashMap<String, MutableAttributeSet>();
            jsKeywords.putAll(JCodePane.javaKeywords);
            jsKeywords.put("function", MultiSyntaxDocument.DEFAULT_KEYWORD);

            final JCodePane codePane = new JCodePane(jsKeywords, false);
            final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            final JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    frm.dispose();
                }
            });
            frm.setResizable(true);
            frm.setMaximizable(true);
            frm.setIconifiable(true);
            codePane.setText(code);
            bottom.add(cancel);
            pane.add(codePane, BorderLayout.CENTER);
            pane.add(bottom, BorderLayout.SOUTH);

            SwingUtils.showInternalFrame(JBlocks.getContextForComponent(parent).getDesktop(), frm, pane, new Dimension(620, 360));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showInternalMessageDialog(JBlocks.getContextForComponent(parent).getDesktop(),
                    "Couldn't compile to JavaScript: \n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void shareBlock() {
        final BlockModel model = parent.getModel();
        final JPanel p = new JPanel(new BorderLayout());
        final JTextArea description = new JTextArea(
                "I am a " + (model.getCode() instanceof NativeBlock ? "native" : "BYOB") + " " + model.getType() + " block "
                + "made by " + System.getProperty("user.name") + ".\n");
        description.setFont(new Font(Font.MONOSPACED, Font.PLAIN, description.getFont().getSize()));

        p.add(new JScrollPane(description), BorderLayout.CENTER);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton uploadButton = new JButton("Upload");
        JButton cancelButton = new JButton("Cancel");
        south.add(uploadButton);
        south.add(cancelButton);
        p.add(south, BorderLayout.SOUTH);
        p.add(new JLabel("Description: "), BorderLayout.NORTH);

        final JInternalFrame frm = SwingUtils.showInternalFrame(SwingUtils.getDesktop(parent), p, "Share a block", new Dimension(340, 240));

        uploadButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    frm.dispose();
                    model.setDescription(description.getText());
                    org.jblocks.blockstore.BlockStoreServer.uploadBlock(JBlocks.getContextForComponent(parent), model);
                    JOptionPane.showInternalMessageDialog(JBlocks.getContextForComponent(parent).getDesktop(),
                            "The block is uploaded!");

                } catch (Exception io) {
                    JOptionPane.showInternalMessageDialog(JBlocks.getContextForComponent(parent).getDesktop(),
                            "The block couldn't be uploaded!\n"
                            + "The error can be: You tried to upload an already existing block.\n\n" + io, "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frm.dispose();
            }
        });
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
    
    private void editBlock() {
        BlockModel model = parent.getModel();
        Block code = model.getCode();
        JDesktopPane desktop = JBlocks.getContextForComponent(parent).getDesktop();
        
        if (code instanceof ByobBlock) {
            
        } else if (code instanceof StorableNativeBlock) {
            StorableNativeBlock st = (StorableNativeBlock) code;
            JCyobEditor edt = JCyobEditor.createEditEditor(model.getSyntax(), st);
            if (edt == null) {
                JOptionPane.showMessageDialog(desktop, "Error: Couldn't open block!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JBlocksPane.showCyobEditorFrame(desktop, edt);
            }
        }
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
        } else if (command.equals("view JavaScript")) {
            viewJavaScript();
        } else if (command.equals("edit")) {
            editBlock();
        }
    }
}
