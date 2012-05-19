package org.jblocks.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ZeroLuck
 */
public class SwingUtils {

    /**
     * Ensures to run the Runnable in the EDT. <br />
     * 
     * @param r the Runnable to run in the EDT
     */
    public static void run(Runnable r) {
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

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
    public static void showInternalFileChooser(JDesktopPane desktop, final JFileChooser ch) {
        final JInternalFrame frm = new JInternalFrame(ch.getDialogTitle());
        frm.setClosable(true);
        frm.setResizable(true);
        frm.setLayout(new BorderLayout());
        frm.add(ch, BorderLayout.CENTER);
        frm.setVisible(true);

        frm.pack();

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);

        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                frm.dispose();
                ch.removeActionListener(this);
            }
        });

        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    /**
     * Shows a specified JInternalFrame in the middle of the specified JDesktopPane. <br />
     * The size of the JInternalFrame will be <code>frameSize</code>. <br />
     * The frame is <i>just</i> closeable. <br />
     * 
     * @param desktop the JDesktopPane
     * @param frm the JInternalFrame
     * @param content the component which will be added to the JInternalFrame
     * @param frameSize the size of the JInternalFrame
     */
    public static void showInternalFrame(JDesktopPane desktop, JInternalFrame frm, JComponent content,  Dimension frameSize) {
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(content, BorderLayout.CENTER);
        frm.setSize(frameSize);
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

    /**
     * Creates (and displays) a JInternalFrame for a specified component. <br />
     * The size of the JInternalFrame will be <code>frameSize</code>. <br />
     * The frame is <i>just</i> closeable. <br />
     * 
     * @param desktop the JDesktopPane
     * @param comp the component to display in the created frame
     * @param title the title of the frame
     * @param frameSize the size of the frame
     * @return the created and displayed frame
     */
    public static JInternalFrame showInternalFrame(JDesktopPane desktop, JComponent comp, String title, Dimension frameSize) {
        JInternalFrame frm = new JInternalFrame(title);
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(comp, BorderLayout.CENTER);
        frm.setSize(frameSize);
        frm.setVisible(true);

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);

        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        return frm;
    }

    /**
     * Creates (and displays) a JInternalFrame for a specified component. <br />
     * The size of the JInternalFrame will be setted with calling <code>pack()</code>. <br />
     * 
     * @param desktop the JDesktopPane
     * @param comp the component to display in the created frame
     * @param title the title of the frame
     * @param frameSize the size of the frame
     * @return the created and displayed frame
     */
    public static JInternalFrame showInternalFrame(JDesktopPane desktop, JComponent comp, String title) {
        JInternalFrame frm = new JInternalFrame(title);
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(comp, BorderLayout.CENTER);

        desktop.add(frm, 0);
        frm.pack();
        frm.setVisible(true);

        Dimension size = frm.getSize();
        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);

        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }

        return frm;
    }

    /**
     * Returns the JDesktopPane of the specified Component. <br />
     * 
     * @param c the component
     * @return the JDesktopPane for the component
     */
    public static JDesktopPane getDesktop(Component c) {
        return (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, c);
    }
}
