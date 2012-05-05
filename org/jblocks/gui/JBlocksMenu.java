package org.jblocks.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
        fileMenu.add("Save").addActionListener(new ActionListener() {

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
        fileMenu.add("New Project").addActionListener(new ActionListener() {

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

        settingsMenu.add(lookAndFeels);

        add(fileMenu);
        add(toolMenu);
        add(settingsMenu);

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
                    if (Character.isAlphabetic(c) && Character.isUpperCase(c)
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
