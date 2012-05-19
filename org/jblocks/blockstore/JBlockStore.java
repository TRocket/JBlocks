package org.jblocks.blockstore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import javax.swing.table.DefaultTableModel;
import org.jblocks.JBlocks;
import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.gui.JHintingTextField;
import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.utils.SwingUtils;
import org.jblocks.utils.XMLSwingLoader;

/**
 *
 * @author ZeroLuck
 */
public class JBlockStore extends JPanel {

    private final JPaneSwitcher swt;
    private final JProgressBar progress;
    private final List<Long> blocks;
    private final JSimpleComboBox<JComponent> blockFilter;

    public JBlockStore() {
        setLayout(new BorderLayout());
        swt = new JPaneSwitcher();
        JPanel intro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progress = new JProgressBar();
        blocks = new ArrayList<Long>();
        blockFilter = new JSimpleComboBox<JComponent>();// new JComboBox<JComponent>();

        intro.add(new JLabel(JBlocks.getIcon("welcome-to-the-blockstore.png")));
        intro.setBackground(Color.WHITE);
        swt.setView(intro);

      //  blockFilter.setRenderer(new ComponentCellRenderer<JComponent>(true));

        AbstrBlock reporterBlock = BlockFactory.createPreviewBlock("reporter", "reporter");
        AbstrBlock booleanBlock = BlockFactory.createPreviewBlock("boolean", "boolean");
        AbstrBlock commandBlock = BlockFactory.createPreviewBlock("command", "command");
        AbstrBlock capBlock = BlockFactory.createPreviewBlock("cap", "cap block");

        blockFilter.addItem(new JLabel("any block"));
        blockFilter.addItem(commandBlock);
        blockFilter.addItem(reporterBlock);
        blockFilter.addItem(booleanBlock);
        blockFilter.addItem(capBlock);
        blockFilter.setFocusable(false);

        final JToolBar tools = new JToolBar();
        tools.setFloatable(false);

        final JButton newest = new JButton(JBlocks.getIcon("new.png"));
        tools.add(newest);

        tools.add(blockFilter);

        JPanel top = new JPanel(new BorderLayout());
        top.add(tools, BorderLayout.CENTER);
        
        final JHintingTextField searchField = new JHintingTextField(20);
        searchField.setEmptyTextHint("Search a block");
        searchField.setLayout(new BorderLayout());
        searchField.add(new JLabel(JBlocks.getIcon("search.png")), BorderLayout.EAST);
        
        JPanel centeringPanel = new JPanel(new FlowLayout());
        centeringPanel.add(searchField);
        top.add(centeringPanel, BorderLayout.EAST);


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

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(progress);

        add(swt, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
    }

    private void openDownloader(long id) {
        final JBlocks jblocks = JBlocks.getContextForComponent(this);
        if (id == BlockModel.NOT_AN_ID) {
            // the block is obsolete
            return;
        }

        try {
            final BlockModel model = BlockStoreServer.downloadBlock(JBlocks.getContextForComponent(this), id);
            
            JPanel root = new JPanel(new BorderLayout());
            root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton downloadButton = new JButton("Download");

            south.add(downloadButton);
            JButton cancelButton = new JButton("Cancel");

            south.add(cancelButton);

            DefaultTableModel table = new DefaultTableModel(new Object[][]{
                        {"Syntax", model.getSyntax()},
                        {"ID", model.getID()},
                        {"Type", model.getType()},
                        {"Native", (model.getCode() instanceof NativeBlock)},
                        {"Category", model.getCategory()},
                        {"Description", model.getDescription()}
                    }, new String[]{"Key", "Value"}) {

                @Override
                public boolean isCellEditable(int x, int y) {
                    return false;
                }
            };

            JLabel info = new JLabel("<html>"
                    + "<h3>Do you really want to download this block?</h3>"
                    + (jblocks.getInstalledBlocks().containsKey(id)
                    ? ("<b>Warning:</b> <font color='red'>A block with this ID is already installed. <br />"
                    + "Your project may not be compatible with the new block.</font>") : "")
                    + "</html>");
            
            root.add(info, BorderLayout.NORTH);
            root.add(new JTable(table), BorderLayout.CENTER);
            root.add(south, BorderLayout.SOUTH);

            final JInternalFrame frm = SwingUtils.showInternalFrame(SwingUtils.getDesktop(this), root, "Block Downloader");
            cancelButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    frm.dispose();
                }
            });
            downloadButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    jblocks.installBlock(model);
                    frm.dispose();
                }
            });
        } catch (IOException io) {
            JOptionPane.showInternalMessageDialog(this, "The block couldn't be downloaded!\n" + io, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayNewest() {
        if (progress.isIndeterminate()) {
            return;
        }
        new Thread() {

            @Override
            public void run() {
                DefaultListModel<JComponent> model = new DefaultListModel<JComponent>();
                try {
                    progress.setIndeterminate(true);
                    addToModel(BlockStoreServer.newestBlocks(), model);
                } catch (IOException io) {
                    model.addElement(new JLabel("<html><h3>An error occurred!</h3><i>" + io + "</i><br /></html>"));
                } finally {
                    progress.setIndeterminate(false);
                }
                displayList(model);
            }
        }.start();
    }

    private void displayList(final ListModel<JComponent> model) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JList<JComponent> list = new JList<JComponent>(model);
                list.setSelectedIndex(0);
                list.setCellRenderer(new ComponentCellRenderer<JComponent>(true));
                list.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() >= 2) {
                            int selected = list.getSelectedIndex();
                            if (selected >= 0 && selected < blocks.size()) {
                                openDownloader(blocks.get(selected));
                            }
                        }
                    }
                });
                swt.setView(list);
            }
        });
    }

    private void displaySearch(final String keywords) {
        if (progress.isIndeterminate()) {
            return;
        }

        new Thread() {

            @Override
            public void run() {
                DefaultListModel<JComponent> model = new DefaultListModel<JComponent>();
                try {
                    String filter = null;
                    if (blockFilter.getSelectedIndex() == 1) {
                        filter = BlockFactory.TYPE_COMMAND_BLOCK;
                    } else if (blockFilter.getSelectedIndex() == 2) {
                        filter = BlockFactory.TYPE_REPORTER_BLOCK;
                    } else if (blockFilter.getSelectedIndex() == 3) {
                        filter = BlockFactory.TYPE_BOOLEAN_BLOCK;
                    } else if (blockFilter.getSelectedIndex() == 4) {
                        filter = BlockFactory.TYPE_CAP_BLOCK;
                    }

                    progress.setIndeterminate(true);
                    addToModel(BlockStoreServer.searchBlocks(keywords, null, filter), model);
                } catch (Exception io) {
                    model.addElement(new JLabel("<html><h3>An error occurred!</h3><i>" + io + "</i><br /></html>"));
                } finally {
                    progress.setIndeterminate(false);
                }
                displayList(model);
            }
        }.start();
    }

    private void addToModel(BlockModel[] m, DefaultListModel<JComponent> model) {
        blocks.clear();
        if (m.length > 0) {
            for (BlockModel bm : m) {
                AbstrBlock block = BlockFactory.createBlock(bm);
                block.setDraggable(false);
                block.setBackground(JBlocks.getContextForComponent(this).getCategoryColor(bm.getCategory()));
                if (bm.getID() == BlockModel.NOT_AN_ID) {
                    block.setBackground(Color.RED);
                }
                model.addElement(block);
                blocks.add(bm.getID());
            }
        } else {
            model.addElement(new JLabel("<html><h3>The search returned without results!</h3></html>"));
        }
    }

    public static class ComponentCellRenderer<E extends JComponent> extends JPanel implements ListCellRenderer {

        private static final Color HIGHLIGHT_COLOR = new Color(175, 175, 255);
        private static final int GAP = 2;
        private boolean leftLayout = false;

        public ComponentCellRenderer(boolean left) {
            leftLayout = left;
        }

        @Override
        public void doLayout() {
            Dimension mySize = getSize();
            for (Component c : getComponents()) {
                Dimension size = c.getPreferredSize();
                c.setLocation(leftLayout ? GAP : mySize.width / 2 - size.width / 2,
                        mySize.height / 2 - size.height / 2);
                c.setSize(size);
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
            max.width += GAP * 2;
            max.height += GAP * 2;
            return max;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            if (value instanceof JComponent) {
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
            } else {
                return (Component) value;
            }
        }
    }

    public static void openBlockStore(JDesktopPane desktop, Icon icon) {
        JInternalFrame frm = SwingUtils.showInternalFrame(desktop, new JBlockStore(), "Block-Store (by ZeroLuck)", new Dimension(600, 400));
        if (icon != null) {
            frm.setFrameIcon(icon);
        }
        frm.setResizable(true);
    }
}
