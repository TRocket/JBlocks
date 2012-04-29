package org.jblocks.blockstore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import org.jblocks.JBlocks;
import org.jblocks.blockstore.BlockStoreServer.SearchResult;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.JHintingTextField;

/**
 *
 * @author ZeroLuck
 */
public class JBlockStore extends JPanel {

    private final JPanel intro;
    private final JPaneSwitcher swt;
    private final JProgressBar progress;

    public JBlockStore() {
        setLayout(new BorderLayout());
        swt = new JPaneSwitcher();
        intro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progress = new JProgressBar();
        intro.add(new JLabel(JBlocks.getIcon("welcome-to-the-blockstore.png")));
        intro.setBackground(Color.WHITE);


        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        JButton newest = new JButton(JBlocks.getIcon("new.png"));
        tools.add(newest);

        final JHintingTextField searchField = new JHintingTextField(20);
        searchField.setEmptyTextHint("Search blocks");

        searchField.setLayout(new BorderLayout());
        tools.add(searchField);
        searchField.add(new JLabel(JBlocks.getIcon("search.png")), BorderLayout.EAST);

        ActionListener onSearch = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String text = searchField.getText();
                searchField.setText("");
                if (!text.trim().isEmpty()) {
                    displaySearch(text);
                }
            }
        };
        ActionListener onNewest = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                displayNewest();
            }
        };
        newest.addActionListener(onNewest);
        searchField.addActionListener(onSearch);

        JPanel south = new JPanel(new BorderLayout());
        JPanel southEast = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southEast.add(new JButton("OK"));
        southEast.add(new JButton("Camcel"));
        south.add(southEast, BorderLayout.EAST);
        JPanel southWest = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southWest.add(progress);
        south.add(southWest, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        swt.setView(intro);
        center.add(swt);

        JPanel left = new JPanel(new BorderLayout());

        add(left, BorderLayout.EAST);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        add(tools, BorderLayout.NORTH);

    }

    private void displayNewest() {
        if (progress.isIndeterminate()) {
            return;
        }

        final DefaultListModel<JComponent> model = new DefaultListModel<JComponent>();

        new Thread() {

            @Override
            public void run() {
                try {
                    progress.setIndeterminate(true);
                    SearchResult[] res = BlockStoreServer.newest();
                    if (res.length > 0) {
                        for (SearchResult r : res) {
                            model.addElement(createView(r.blockspec));
                        }
                    } else {
                        model.addElement(new JLabel("<html><h3>The search returned no results!</h3></html>"));
                    }
                } catch (IOException io) {
                    model.addElement(new JLabel("<html><h3>An error occurred!</h3><i>" + io + "</i><br /></html>"));
                } finally {
                    progress.setIndeterminate(false);
                }
                // this may be a problem:
                // the code should be run in the EDT

                JList list = new JList(model);
                list.setSelectedIndex(0);
                list.setCellRenderer(new ComponentCellRenderer());
                swt.setView(new JScrollPane(list));
            }
        }.start();
    }

    private void displaySearch(String keywords) {
        if (progress.isIndeterminate()) {
            return;
        }

        String[] split = keywords.replace("%", "").split(" ");
        String search = "%";
        for (String s : split) {
            if (!s.trim().isEmpty()) {
                search += s + "%";
            }
        }

        final String finalSearch = search;
        if (finalSearch.length() >= 255) {
            return;
        }

        final DefaultListModel<JComponent> model = new DefaultListModel<JComponent>();

        new Thread() {

            @Override
            public void run() {
                try {
                    progress.setIndeterminate(true);
                    SearchResult[] res = BlockStoreServer.search(finalSearch);
                    if (res.length > 0) {
                        for (SearchResult r : res) {
                            model.addElement(createView(r.blockspec));
                        }
                    } else {
                        model.addElement(new JLabel("<html><h3>The search returned no results!</h3></html>"));
                    }

                } catch (IOException io) {
                    model.addElement(new JLabel("<html><h3>An error occurred!</h3><i>" + io + "</i><br /></html>"));
                } finally {
                    progress.setIndeterminate(false);
                }
                // this may be a problem:
                // the code should be runned int the EDT

                JList list = new JList(model);
                list.setSelectedIndex(0);
                list.setCellRenderer(new ComponentCellRenderer());
                swt.setView(new JScrollPane(list));
            }
        }.start();

    }

    private JComponent createView(String syntax) {
        AbstrBlock block = BlockFactory.createBlock(BlockModel.createPreviewModel("command", syntax));
        block.setDraggable(false);
        return block;
    }

    private static class ComponentCellRenderer extends JPanel implements ListCellRenderer {

        private static final Color HIGHLIGHT_COLOR = new Color(175, 175, 255);

        @Override
        public void doLayout() {
            for (Component c : getComponents()) {
                c.setLocation(0, 0);
                c.setSize(c.getPreferredSize());
            }
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension max = new Dimension(0, 0);
            for (Component c : getComponents()) {
                Dimension dim = c.getPreferredSize();
                max.width = Math.max(max.width, dim.width);
                max.height = Math.max(max.height, dim.height);
            }
            return max;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JComponent comp = (JComponent) value;
            if (isSelected) {
                setBackground(HIGHLIGHT_COLOR);
            } else {
                setBackground(Color.WHITE);
            }
            removeAll();
            add(comp);
            doLayout();
            setSize(getPreferredSize());

            return this;
        }
    }

    public static void openBlockStore(JDesktopPane desktop, Icon icon) {
        JInternalFrame frm = new JInternalFrame("ZeroLuck's Block-Store");
        if (icon != null) {
            frm.setFrameIcon(icon);
        }
        frm.setClosable(true);
        frm.setResizable(true);
        frm.setLayout(new BorderLayout());
        frm.add(new JBlockStore(), BorderLayout.CENTER);
        final Dimension size = new Dimension(600, 400);
        frm.setSize(size);
        frm.setVisible(true);

        frm.setLocation(desktop.getWidth() / 2 - size.width / 2,
                desktop.getHeight() / 2 - size.height / 2);
        desktop.add(frm, 0);
        try {
            frm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
        }
    }

    public static void main(String[] args) {
        JBlocksPane.setLaF();

        JFrame frm = new JFrame("Test : JBlockStore");
        frm.setLocationByPlatform(true);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLayout(new BorderLayout());
        frm.add(new JBlockStore(), BorderLayout.CENTER);
        frm.setSize(600, 400);
        frm.setVisible(true);
    }
}
