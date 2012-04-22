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
import javax.swing.JToolBar;
import org.jblocks.JBlocks;
import org.jblocks.byob.JByobEditor;
import org.jblocks.editor.BlockFactory;
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
        this.context = ctx;
        
        app = new JPanel();
        tools = new JToolBar();

        editor = createBlockEditor();

        JButton openButton = new JButton(JBlocks.getIcon("open.png"));
        openButton.setToolTipText("Open project");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //    openFileChooser(JBlocksPane.this, new JFileChooser());
            }
        });

        JButton saveButton = new JButton(JBlocks.getIcon("save.png"));
        saveButton.setToolTipText("Save project");
        tools.add(saveButton);
        tools.add(openButton);

        JButton runButton = new JButton(JBlocks.getIcon("run-build.png"));
        runButton.setToolTipText("Run project");
        tools.add(runButton);
        tools.add(new JSeparator(JSeparator.VERTICAL));
        JButton blockstoreButton = new JButton(JBlocks.getIcon("download-folder.png"));
        blockstoreButton.setToolTipText("Open Block-Store");
        tools.add(blockstoreButton);

        JButton openPaint = new JButton(JBlocks.getIcon("paint-editor.png"));
        openPaint.setToolTipText("Open Paint-Editor");
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

        JButton openSound = new JButton(JBlocks.getIcon("speaker.png"));
        openSound.setToolTipText("Open Sound-Editor");
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

        JButton openByob = new JButton(JBlocks.getIcon("block-editor.png"));
        openByob.setToolTipText("Make a block");
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

        spriteChooser = SpriteChooserTest.createTestSpriteChooser2(editor);

        JPanel east = new JPanel(new BorderLayout());
        
        JScrollPane chScroll = new JScrollPane(spriteChooser);
        chScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        progress = new JProgressBar();
        JPopupMenu menu = new JPopupMenu("Hello");
        ActionListener stopScripts = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent ae) {
                context.stopScripts();
            }
        };
        menu.add("Stop all scripts").addActionListener(stopScripts);
        progress.setComponentPopupMenu(menu);
        
        JPanel eastNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        eastNorth.add(progress);
        AbstractButton startButton = new JImageButton(JBlocks.getImage("play.png"));
        eastNorth.add(startButton);
        AbstractButton stopButton = new JImageButton(JBlocks.getImage("stop.png"));
        stopButton.addActionListener(stopScripts);
       
        eastNorth.add(stopButton);        
        
        east.add(chScroll, BorderLayout.CENTER);
        east.add(eastNorth, BorderLayout.NORTH);
        
        app.add(editor, BorderLayout.CENTER);
        app.add(east, BorderLayout.EAST);

        
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
        
        // standard blocks
        
        edt.addCategory("Control", new Color(0xD6900A + 0x111111));
       // edt.addCategory("Control", new Color(0xD6900A));
        edt.addBlock("Control", BlockFactory.createBlock("hat", "When %{gf} clicked"));
        edt.addBlock("Control", BlockFactory.createBlock("cap", "return %{r}"));
        edt.addBlock("Control", BlockFactory.createBlock("command", "while %{b}%{br}%{s}"));
        edt.addBlock("Control", BlockFactory.createBlock("command", "if %{b}%{br}%{s}"));
        edt.addBlock("Control", BlockFactory.createBlock("command", "if %{b}%{br}%{s}%{br}else%{s}"));
        edt.addBlock("Control", BlockFactory.createBlock("command", "repeat %{r}%{br}%{s}"));
        // not implemented yet
        
        edt.addCategory("Motion", new Color(0xff4a6cd6));
        edt.addCategory("Operators", new Color(0xff62c213));
        edt.addCategory("Variables", new Color(0xf3761d));
        edt.addCategory("Sprites", Color.MAGENTA.darker());
        edt.addCategory("IO & Network", Color.CYAN);
        edt.addCategory("GUI & System", new Color(0xffD0D000));
        edt.addCategory("Sound", Color.MAGENTA);
        
        edt.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}+%{r}"));
        edt.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}-%{r}"));
        edt.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}*%{r}"));
        edt.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}/%{r}"));
        edt.addBlock("Operators", BlockFactory.createBlock("reporter", "%{r}mod%{r}"));
        
        edt.addBlock("Operators", BlockFactory.createBlock("boolean", "true"));
        edt.addBlock("Operators", BlockFactory.createBlock("boolean", "false"));
        
        edt.addComponent("Variables", new JButton("Make a variable"));
        edt.addComponent("Variables", new JButton("Delete a variable"));
        
        edt.addBlock("Variables", BlockFactory.createBlock("reporter", "test-variable"));
        
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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            System.err.println("Exception while setting LaF.");
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
