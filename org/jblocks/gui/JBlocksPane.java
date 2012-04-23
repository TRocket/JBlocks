package org.jblocks.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import org.jblocks.JBlocks;
import org.jblocks.byob.JByobEditor;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.painteditor2.JPaintEditor;
import org.jblocks.soundeditor.JSoundEditor;

/**
 *
 * The main GUI class for JBlocks. <br />
 * This component have to be enclosed in an JDragPane. <br />
 * 
 * @see org.jblocks.gui.JDragPane
 * @author ZeroLuck
 */
public class JBlocksPane extends JDesktopPane {

    // <member>
    private final JToolBar tools;
    private final JBlockEditor editor;
    private final JPanel app;
    private final JSpriteChooser spriteChooser;
    private final JBlocks context;
    private final JProgressBar progress;

    public JBlocksPane(JBlocks ctx) {
        // initialise final variables...
        this.context = ctx;
        this.app = new JPanel();
        this.progress = new JProgressBar();
        this.tools = new JToolBar();
        this.editor = createBlockEditor();
        this.spriteChooser = SpriteChooserTest.createTestSpriteChooser2(editor);

        // build up the GUI...
        final JButton saveButton = new JButton(JBlocks.getIcon("save.png"));
        saveButton.setToolTipText("<HTML><b>Save project</b><ul><li>Save your project</li></ul></HTML>");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // not implemented yet
            }
        });
        tools.add(saveButton);

        final JButton openButton = new JButton(JBlocks.getIcon("open.png"));
        openButton.setToolTipText("<HTML><b>Open project</b><ul><li>Open your saved project</li></ul></HTML></HTML>");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                // not implemented yet
            }
        });
        tools.add(openButton);

        final JButton runButton = new JButton(JBlocks.getIcon("run-build.png"));
        runButton.setToolTipText("<HTML><b>Run project</b><ul>"
                + "<li>Runs your project</li>"
                + "<li>You can test how your project will look out of JBlocks</li>"
                + "</ul></HTML>");
        tools.add(runButton);

        tools.add(new JSeparator(JSeparator.VERTICAL));

        final JButton blockstoreButton = new JButton(JBlocks.getIcon("download-folder.png"));
        blockstoreButton.setToolTipText("<HTML><b>Block-Store</b><ul><li>Download blocks</li><li>Share blocks</li></ul></HTML>");
        tools.add(blockstoreButton);

        final JButton openPaint = new JButton(JBlocks.getIcon("paint-editor.png"));
        openPaint.setToolTipText("<HTML><b>Paint-Editor</b><ul><li>Draw images</li></ul></HTML>");
        openPaint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                final JInternalFrame frm = new JInternalFrame("ZeroLuck's Paint-Editor");
                frm.setResizable(false);
                frm.setClosable(true);
                frm.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                frm.setLayout(new BorderLayout());

                JPaintEditor edt = new JPaintEditor();
                edt.addPaintEditorListener(new JPaintEditor.PaintEditorListener() {

                    @Override
                    public void cancelSelected(BufferedImage img) {
                        frm.dispose();
                    }

                    @Override
                    public void finishSelected(BufferedImage img) {
                        spriteChooser.addSpriteView(null, "Test", img);
                        frm.dispose();
                    }
                });
                frm.add(edt, BorderLayout.CENTER);


                add(frm, 0);
                frm.setVisible(true);
                frm.pack();

                int w = frm.getWidth();
                int h = frm.getHeight();

                frm.setFrameIcon(JBlocks.getIcon("paint-editor.png"));
                frm.setLocation(getWidth() / 2 - w / 2, getHeight() / 2 - h / 2);

                try {
                    frm.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                }
            }
        });
        tools.add(openPaint);

        final JButton openSound = new JButton(JBlocks.getIcon("speaker.png"));
        openSound.setToolTipText("<HTML><b>Sound-Editor</b><ul><li>Import sounds</li><li>Modify sound tracks</li><li>Export sounds</li></ul></HTML>");
        openSound.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                final JInternalFrame frm = new JInternalFrame("ZeroLuck's Sound-Editor");
                frm.setResizable(true);
                frm.setClosable(true);
                frm.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                frm.setLayout(new BorderLayout());

                Container edt = new JSoundEditor();
                frm.add(edt, BorderLayout.CENTER);
                frm.setVisible(true);

                frm.setSize((int) (getWidth() / 1.3), (int) (getHeight() / 1.3));

                int w = frm.getWidth();
                int h = frm.getHeight();

                frm.setFrameIcon(JBlocks.getIcon("speaker.png"));
                frm.setLocation(getWidth() / 2 - w / 2, getHeight() / 2 - h / 2);

                add(frm, 0);
                try {
                    frm.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                }
            }
        });

        tools.add(openSound);

        final JButton openByob = new JButton(JBlocks.getIcon("block-editor.png"));
        openByob.setToolTipText("<HTML><b>Make a block</b><ul><li>Create your own blocks without Java</li></ul></HTML>");
        openByob.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JByobEditor.createEditor(JBlocksPane.this, JBlocks.getIcon("block-editor.png"));
            }
        });
        tools.add(openByob);

        // add components to 'app'
        app.setLayout(new BorderLayout());
        app.add(tools, BorderLayout.NORTH);

        final JScrollPane chScroll = new JScrollPane(spriteChooser);
        chScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPopupMenu menu = new JPopupMenu();
        ActionListener stopScripts = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                context.stopScripts();
            }
        };
        menu.add("Stop all scripts").addActionListener(stopScripts);
        progress.setComponentPopupMenu(menu);

        tools.addSeparator();

        AbstractButton startButton = new JImageButton(JBlocks.getImage("play.png"));
        tools.add(startButton);
        AbstractButton stopButton = new JImageButton(JBlocks.getImage("stop.png"));
        stopButton.addActionListener(stopScripts);
        tools.add(stopButton);

        tools.add(progress, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();

        JPanel scripts = new JPanel(new BorderLayout());
        scripts.add(editor, BorderLayout.CENTER);
        scripts.add(spriteChooser, BorderLayout.EAST);
        tabs.addTab("Scripts", scripts);

        JPanel stage = new JPanel(new FlowLayout());
        stage.add(new JLabel("Not finished yet!"));
        tabs.addTab("Stage", stage);

        app.add(tabs, BorderLayout.CENTER);

        // add app to the desktop-pane
        add(app);
    }

    public JProgressBar getProgress() {
        return progress;
    }

    /**
     * Returns the context of this JBlocksPane. <br />
     * 
     * @see org.jblocks.JBlocks
     */
    public JBlocks getContext() {
        return context;
    }

    private JBlockEditor createBlockEditor() {
        JBlockEditor edt = new JBlockEditor();

        // standard categories

        edt.addCategory("Control", new Color(0xD6900A + 0x111111));
        edt.addCategory("Motion", new Color(0xff4a6cd6));
        edt.addCategory("Operators", new Color(0xff62c213));
        edt.addCategory("Variables", new Color(0xf3761d));
        edt.addCategory("Sprites", Color.MAGENTA.darker());
        edt.addCategory("IO & Network", Color.CYAN);
        edt.addCategory("GUI & System", new Color(0xffD0D000));
        edt.addCategory("Sound", Color.MAGENTA);
        edt.addComponent("Variables", new JButton("Make a variable"));
        edt.addComponent("Variables", new JButton("Delete a variable"));

        edt.cleanup();
        return edt;
    }

    private static JBlocksPane getJBlocksPane(Component c) {
        if (c instanceof JBlocksPane) {
            return (JBlocksPane) c;
        }
        Container cont = c.getParent();
        while (cont != null) {
            if (cont instanceof JBlocksPane) {
                return (JBlocksPane) cont;
            }

            cont = cont.getParent();
        }
        return null;
    }

    // will be removed later
    public static void openFileChooserRead(Component c, String text) {
        JBlocksPane jblocks = getJBlocksPane(c);
        JFileChooser chooser = new JFileChooser();
        JInternalFrame frm = new JInternalFrame("File Chooser");
        frm.setClosable(true);
        frm.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frm.setResizable(true);
        frm.add(chooser);
        frm.setVisible(true);
        frm.pack();
        frm.setLocation(jblocks.getWidth() / 2 - frm.getWidth() / 2,
                jblocks.getHeight() / 2 - frm.getHeight() / 2);
        jblocks.add(frm, 0);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        app.setBounds(0, 0, getWidth(), getHeight());
    }

    public static void setLaF() {
        /* Sets the Nimbus look and feel */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
                    defaults.put("info", new Color(0xF0F0F0));
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            System.err.println("Exception while setting LaF. (" + t + ")");
            // we don't want that our application crashs just because of this LaF.
            // (older Java versions may not support the nimbus LaF.)
        }
    }

    /**
     * Returns the JBlockEditor of this JBlocksPane. <br />
     * @see org.jblocks.JBlocks#getEditor() 
     */
    public JBlockEditor getEditor() {
        return editor;
    }
}
