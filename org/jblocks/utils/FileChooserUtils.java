package org.jblocks.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ZeroLuck
 */
public class FileChooserUtils {

    /**
     * Creates a FileFilter for a specified description
     * and an array of allowed extensions. <br />
     * 
     * @param extensions the allowed extensions without a dot
     * @param description the displayed description
     * @return the created FileFilter
     */
    public static FileFilter createFilter(final String[] extensions, final String description) {
        return new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                String name = file.getName().toLowerCase();
                for (String e : extensions) {
                    if (name.endsWith("." + e.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return description;
            }
        };
    }

    /**
     * Returns true if the Event is a JFileChooser.APPROVE_SELECTION. <br />
     * <code>null</code> is handled too! <br />
     * 
     * @param evt the ActionEvent which to test
     * @return true if the Event is a JFileChooser.APPROVE_SELECTION
     */
    public static boolean isApproveSelection(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        return cmd != null && cmd.equals(JFileChooser.APPROVE_SELECTION);
    }

    /**
     * Displays a specified <code>JFileChooser</code> in a JInternalFrame. <br />
     * The JInternalFrame will close when the dialog is closed. <br />
     * 
     * @param desktop the JDesktopPane on which to display the JFileChooser
     * @param ch the JFileChooser to display
     */
    public static void showInternalFileChooser(JDesktopPane desktop, JFileChooser ch) {
        final JInternalFrame frm = new JInternalFrame(ch.getDialogTitle());
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(ch, BorderLayout.CENTER);
        frm.pack();
        frm.setVisible(true);

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);

        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frm.dispose();
            }
        });

        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }
}
