package org.jblocks.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jblocks.JBlocks;
import org.jblocks.blockstore.JBlockStore;
import org.jblocks.byob.JByobEditor;
import org.jblocks.cyob.JCyobEditor;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.painteditor2.JPaintEditor;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.soundeditor.JSoundEditor;
import org.jblocks.utils.SwingUtils;

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
    private final JRootPane root;
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
                saveProject(false);
            }
        });
        tools.add(saveButton);

        final JButton openButton = new JButton(JBlocks.getIcon("open.png"));
        openButton.setToolTipText("<HTML><b>Open project</b><ul><li>Open your saved project</li></ul></HTML></HTML>");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openProject();
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
        blockstoreButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openBlockStore();
            }
        });
        blockstoreButton.setToolTipText("<HTML><b>Block-Store</b><ul><li>Download blocks</li><li>Share blocks</li></ul></HTML>");
        tools.add(blockstoreButton);

        final JButton openPaint = new JButton(JBlocks.getIcon("paint-editor.png"));
        openPaint.setToolTipText("<HTML><b>Paint-Editor</b><ul><li>Draw images</li></ul></HTML>");
        openPaint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openPaintEditor();
            }
        });
        tools.add(openPaint);

        final JButton openSound = new JButton(JBlocks.getIcon("speaker.png"));
        openSound.setToolTipText("<HTML><b>Sound-Editor</b><ul><li>Import sounds</li><li>Modify sound tracks</li><li>Export sounds</li></ul></HTML>");
        openSound.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openSoundEditor();
            }
        });

        tools.add(openSound);

        final JButton openByob = new JButton(JBlocks.getIcon("block-editor.png"));
        openByob.setToolTipText("<HTML><b>Make a block</b><ul><li>Create your own blocks without Java</li></ul></HTML>");
        openByob.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openByobEditor();
            }
        });
        tools.add(openByob);

        final JButton openCyob = new JButton(JBlocks.getIcon("cyob.png"));
        openCyob.setToolTipText("<HTML><b>Code a block</b><ul><li>Create your own blocks in Java</li></ul></HTML>");
        openCyob.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openCyobEditor();
            }
        });
        tools.add(openCyob);



        // add components to 'app'
        app.setLayout(new BorderLayout());
        app.add(tools, BorderLayout.NORTH);

        final JScrollPane chScroll = new JScrollPane(spriteChooser);
        chScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPopupMenu progressMenu = new JPopupMenu();
        ActionListener stopScripts = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                context.stopScripts();
            }
        };
        progressMenu.add("Stop all scripts").addActionListener(stopScripts);
        progress.setComponentPopupMenu(progressMenu);

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

        root = new JRootPane();
        root.setContentPane(app);
        root.setJMenuBar(new JBlocksMenu(this));

        // add app to the desktop-pane
        add(root);
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

    /****************************************************************
     *******************  Menu and Toolbar **************************
     ****************************************************************/
    void openProject() {
        // TODO
        JOptionPane.showInternalMessageDialog(this, "Not yet implemented!");
    }

    void saveProject(boolean saveAs) {
        // TODO
        JOptionPane.showInternalMessageDialog(this, "Not yet implemented!");
    }

    void newProject() {
        // TODO
        JOptionPane.showInternalMessageDialog(this, "Not yet implemented!");
    }

    void openBlockStore() {
        JBlockStore.openBlockStore(JBlocksPane.this, JBlocks.getIcon("download-folder.png"));
    }

    void openPaintEditor() {
        JPaintEditor edt = new JPaintEditor();
        final JInternalFrame frm = SwingUtils.showInternalFrame(JBlocksPane.this, edt, "ZeroLuck's Paint-Editor");
        frm.setFrameIcon(JBlocks.getIcon("paint-editor.png"));
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
    }

    void openSoundEditor() {
        JSoundEditor edt = new JSoundEditor();
        JInternalFrame frm = SwingUtils.showInternalFrame(JBlocksPane.this, edt, "ZeroLuck's Sound-Editor",
                new Dimension((int) (getWidth() / 1.3), (int) (getHeight() / 1.3)));
        frm.setResizable(true);
        frm.setFrameIcon(JBlocks.getIcon("speaker.png"));
    }

    void openByobEditor() {
        JByobEditor.createEditor(JBlocksPane.this, JBlocks.getIcon("block-editor.png"));
    }

    void openCyobEditor() {
        Icon icn = JBlocks.getIcon("cyob.png");
        JInternalFrame frm = SwingUtils.showInternalFrame(JBlocksPane.this, new JCyobEditor(), "ZeroLuck's Cyob-Editor", 
                new Dimension((int) (getWidth() * 0.8), (int) (getHeight() * 0.8)));
        frm.setFrameIcon(icn);
        frm.setResizable(true);
    }

    /***********************************************************/
    private JBlockEditor createBlockEditor() {
        final JBlockEditor edt = new JBlockEditor();

        // standard categories

        edt.addCategory("Control", new Color(0xD6900A + 0x111111));
        edt.addCategory("Motion", new Color(0xff4a6cd6));
        edt.addCategory("Operators", new Color(0xff62c213));
        edt.addCategory("Variables", new Color(0xf3761d));
        edt.addCategory("Sprites", Color.MAGENTA.darker());
        edt.addCategory("IO & Network", Color.CYAN);
        edt.addCategory("GUI & System", new Color(0xffD0D000));
        edt.addCategory("Sound", Color.MAGENTA);

        // standard components
        final JButton makeVariable = new JButton("Make a variable");
        final JButton deleteVariable = new JButton("Delete a variable");

        deleteVariable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                final JPopupMenu menu = new JPopupMenu("Variablen");
                menu.setBorder(javax.swing.BorderFactory.createTitledBorder("Variable?"));
                int count = 0;
                final Map global = context.getScriptEngine().getGlobalVariables();
                for (final Object key : global.keySet()) {
                    if (key instanceof String) {
                        menu.add((String) key).addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent ae) {
                                removeVariable((String) key);
                            }
                        });
                        count++;
                    }
                }
                if (count > 0) {
                    menu.show(deleteVariable, 10, 10);
                }
            }
        });
        makeVariable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                final Map global = context.getScriptEngine().getGlobalVariables();
                final String name = JOptionPane.showInternalInputDialog(JBlocksPane.this, "Variable name?", "Create a variable",
                        JOptionPane.PLAIN_MESSAGE);
                if (name != null && !global.containsKey(name)) {
                    addVariable(name);
                }
            }
        });

        edt.addComponent("Variables", makeVariable);
        edt.addComponent("Variables", deleteVariable);

        // layout the editor
        edt.cleanup();
        return edt;
    }

    public void setToolbarVisible(boolean b) {
        if (b && tools.getParent() == null) {
            app.add(tools, BorderLayout.NORTH);
        } else if (!b) {
            app.remove(tools);
        }
        app.invalidate();
        app.validate();
        app.repaint();
    }

    private void removeVariable(final String name) {
        final IScriptEngine engine = context.getScriptEngine();
        final Map variables = engine.getGlobalVariables();
        final Map<Long, BlockModel> blocks = context.getInstalledBlocks();
        final Block READ_GLOBAL_VAR = engine.getDefaultBlock(Default.READ_GLOBAL_VARIABLE).clone();

        for (final BlockModel model : blocks.values()) {
            final String content = model.getContent();
            final Block code = model.getCode();
            if (content != null && code != null && code.equals(READ_GLOBAL_VAR) && content.equals(name)) {
                context.uninstallBlock(model);
                break;
            }
        }

        variables.remove(name);
        repaint();
    }

    private void addVariable(String name) {
        final IScriptEngine engine = context.getScriptEngine();
        final Block readVar = engine.getDefaultBlock(Default.READ_GLOBAL_VARIABLE).clone();
        readVar.setParameter(0, name);

        final BlockModel var = BlockModel.createModel("reporter", "Variables", BlockFactory.enquote(name), readVar);
        var.setContent(name);
        context.installBlock(var);

        engine.getGlobalVariables().put(name, 0);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        root.setBounds(0, 0, getWidth(), getHeight());
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
