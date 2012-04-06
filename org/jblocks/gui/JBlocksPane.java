package org.jblocks.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.jblocks.JBlocks;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.painteditor2.JPaintEditor;
import org.jblocks.soundeditor.JSoundEditor;
import org.jblocks.soundeditor.JSoundRecorder;

/**
 *
 * The main GUI class for JBlocks. <br />
 * This component have to be enclosed in an JDragPane. <br />
 * 
 * @see org.jblocks.gui.JDragPane
 * @author ZeroLuck
 */
public class JBlocksPane extends JDesktopPane {

    // <global>
    private static ImageIcon icon_run_build;
    private static ImageIcon icon_download_folder;
    private static ImageIcon icon_save;
    private static ImageIcon icon_open;
    private static ImageIcon icon_paint_editor;
    private static ImageIcon icon_sound_editor;
    // <member>
    private JToolBar tools;
    private JBlockEditor editor;
    private JPanel app;
    private JSpriteChooser spriteChooser;

    static {
        icon_run_build = new ImageIcon(JBlocks.class.getResource("res/run-build.png"));
        icon_download_folder = new ImageIcon(JBlocks.class.getResource("res/download-folder.png"));
        icon_save = new ImageIcon(JBlocks.class.getResource("res/save.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_paint_editor = new ImageIcon(JBlocks.class.getResource("res/paint-editor.png"));
        icon_sound_editor = new ImageIcon(JBlocks.class.getResource("res/speaker.png"));
    }

    public JBlocksPane() {
        app = new JPanel();
        tools = new JToolBar();

        editor = org.jblocks.editor.BlockEditorTest.createTestEditor();

        JButton openButton = new JButton(icon_open);
        openButton.setToolTipText("Open project");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                //    openFileChooser(JBlocksPane.this, new JFileChooser());
            }
        });

        JButton saveButton = new JButton(icon_save);
        saveButton.setToolTipText("Save project");
        tools.add(saveButton);
        tools.add(openButton);
        
        JButton runButton = new JButton(icon_run_build);
        runButton.setToolTipText("Run project");
        tools.add(runButton);
        tools.add(new JSeparator(JSeparator.VERTICAL));
        JButton blockstoreButton = new JButton(icon_download_folder);
        blockstoreButton.setToolTipText("Open Block-Store");
        tools.add(blockstoreButton);

        JButton openPaint = new JButton(icon_paint_editor);
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
                frm.setVisible(true);
                add(frm, 0);

                frm.pack();

                int w = frm.getWidth();
                int h = frm.getHeight();

                frm.setLocation(getWidth() / 2 - w / 2, getHeight() / 2 - h / 2);

            }
        });

        tools.add(openPaint);

        JButton openSound = new JButton(icon_sound_editor);
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
                add(frm, 0);

                frm.setSize((int) (getWidth() / 1.3), (int) (getHeight() / 1.3));

                int w = frm.getWidth();
                int h = frm.getHeight();

                frm.setLocation(getWidth() / 2 - w / 2, getHeight() / 2 - h / 2);

            }
        });

        tools.add(openSound);

        // add components to 'app'
        app.setLayout(new BorderLayout());
        app.add(tools, BorderLayout.NORTH);

        spriteChooser = SpriteChooserTest.createTestSpriteChooser2(editor);

        JScrollPane chScroll = new JScrollPane(spriteChooser);
        chScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        app.add(editor, BorderLayout.CENTER);
        app.add(chScroll, BorderLayout.EAST);
        
        // add app to the desktop-pane
        add(app);
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

    public static void openFileChooserRead(Component c, String text) {
        JBlocksPane jblocks = getJBlocksPane(c);
        JFileChooser chooser = new JFileChooser();
        java.net.URL a;
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
}
