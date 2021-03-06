package org.jblocks.gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.undo.UndoManager;

/**
 * The {@code JMenuBar} of the {@link JBlocksPane}. <br />
 * 
 * @author ZeroLuck
 */
class JBlocksMenu extends JMenuBar {

    private final JBlocksPane jblocks;

    public JBlocksMenu(JBlocksPane pane) {
        // This is just a test menu.
        // TODO: 

        this.jblocks = pane;

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = fileMenu.add("Save");
        saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.saveProject(false);
            }
        });
        fileMenu.add("Save As...").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.saveProject(true);
            }
        });
        JMenuItem newProject = fileMenu.add("New Project");
        newProject.setAccelerator(KeyStroke.getKeyStroke("control shift N"));
        newProject.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.newProject();
            }
        });

        JMenu toolMenu = new JMenu("Tools");
        toolMenu.add("Paint-Editor").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.openPaintEditor();
            }
        });
        toolMenu.add("Sound-Editor").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.openSoundEditor();
            }
        });
        toolMenu.add("BYOB-Editor").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.openByobEditor();
            }
        });
        toolMenu.add("Block-Library").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.openBlockStore();
            }
        });
        toolMenu.add("CYOB-Editor").addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.openCyobEditor();
            }
        });
        toolMenu.addSeparator();
        toolMenu.add("Plugins");

        JMenu settingsMenu = new JMenu("Settings");
        final JCheckBoxMenuItem toolbarVisible = new JCheckBoxMenuItem("Toolbar", true);
        toolbarVisible.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.setToolbarVisible(toolbarVisible.isSelected());
            }
        });
        settingsMenu.add(toolbarVisible);

        JMenu lookAndFeels = new JMenu("Change LookAndFeel");
        for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            lookAndFeels.add(info.getName()).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                        for (Frame frm : Frame.getFrames()) {
                            SwingUtilities.updateComponentTreeUI(frm);
                        }
                    } catch (Exception t) {
                        // ignore this kind of exceptions...
                    }
                }
            });
        }

        JMenu helpMenu = new JMenu("Help");

        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = editMenu.add("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        undoItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                UndoManager mng = jblocks.getEditor().getUndoManager();
                if (mng != null) {
                    if (mng.canUndo()) {
                        mng.undo();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        });
        JMenuItem redoItem = editMenu.add("Redo");
        redoItem.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        redoItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                UndoManager mng = jblocks.getEditor().getUndoManager();
                if (mng != null) {
                    if (mng.canRedo()) {
                        mng.redo();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                    }
                }
            }
        });
        editMenu.addSeparator();
        JMenuItem cleanupItem = editMenu.add("Cleanup All");
        cleanupItem.setAccelerator(KeyStroke.getKeyStroke("control shift C"));
        cleanupItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                jblocks.getEditor().cleanup();
            }
        });
        settingsMenu.add(lookAndFeels);

        fileMenu.setMnemonic('F');
        toolMenu.setMnemonic('T');
        settingsMenu.setMnemonic('S');
        helpMenu.setMnemonic('H');
        editMenu.setMnemonic('E');

        add(fileMenu);
        add(editMenu);
        add(toolMenu);
        add(settingsMenu);
        add(helpMenu);

        // creates short cuts for the JMenu-s
        int len = getMenuCount();
        for (int i = 0; i < len; i++) {
            doMnemonic(getMenu(i));
        }
    }

    private void doMnemonic(JMenu menu) {
        Set<Character> set = new HashSet<Character>(50);

        int len = menu.getItemCount();
        for (int i = 0; i < len; i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null) {
                String txt = item.getText();
                for (char c : txt.toCharArray()) {
                    if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c) != -1
                            && !set.contains(c)) {
                        item.setMnemonic(c);
                        set.add(c);
                        break;
                    }
                }
            }
        }
    }
}
