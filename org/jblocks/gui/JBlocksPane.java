package org.jblocks.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import org.jblocks.JBlocks;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.painteditor.PaintEditor;

/**
 *
 * The main GUI class for JBlocks. <br />
 * 
 * @author ZeroLuck
 */
public class JBlocksPane extends JDesktopPane {

    // <global>
    private static ImageIcon icon_run_build;
    private static ImageIcon icon_download_folder;
    private static ImageIcon icon_save;
    private static ImageIcon icon_open;
    private static ImageIcon icon_paint_editor;
    // <member>
    private JToolBar tools;
    private JBlockEditor editor;
    private JPanel app;

    static {
        icon_run_build = new ImageIcon(JBlocks.class.getResource("res/run-build.png"));
        icon_download_folder = new ImageIcon(JBlocks.class.getResource("res/download-folder.png"));
        icon_save = new ImageIcon(JBlocks.class.getResource("res/save.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_paint_editor = new ImageIcon(JBlocks.class.getResource("res/paint-editor.png"));
    }

    public JBlocksPane() {
        app = new JPanel();
        tools = new JToolBar();

        editor = org.jblocks.editor.BlockEditorTest.createTestEditor();

        tools.add(new JButton(icon_save));
        tools.add(new JButton(icon_open));
        tools.add(new JButton(icon_run_build));
        tools.add(new JButton(icon_download_folder));

        JButton openPaint = new JButton(icon_paint_editor);

        openPaint.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                PaintEditor edt =
                        new org.jblocks.painteditor.PaintEditor();

                int w = 500;
                int h = 400;

                edt.setResizable(true);
                edt.setClosable(true);
                edt.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
                edt.setLocation(getWidth() / 2 - w / 2, getHeight() / 2 - h / 2);

                edt.setSize(w, h);
                edt.setVisible(true);
                add(edt, 0);
            }
        });

        tools.add(openPaint);

        // add components to 'app'
        app.setLayout(new BorderLayout());
        app.add(tools, BorderLayout.NORTH);

        JScrollPane chScroll = new JScrollPane(SpriteChooserTest.createTestSpriteChooser2(editor));
        chScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        app.add(editor, BorderLayout.CENTER);
        app.add(chScroll, BorderLayout.EAST);
        
        // add app to the desktop-pane
        add(app);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        app.setBounds(0, 0, getWidth(), getHeight());
    }

    public static void setLaF() {
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Throwable t) {
            System.err.println("The Nímbus-LaF isn't supported!");
            // we don't want that our application crashs just because of this LaF.
            // (older Java versions doesn't support the nimbus LaF.)
        }
    }
}
