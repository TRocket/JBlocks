package org.jblocks.cyob;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import org.jblocks.JBlocks;
import org.jblocks.byob.BlockTypeChooser;
import org.jblocks.byob.BlockTypeChooser.BlockTypeChooserListener;
import org.jblocks.editor.BlockModel;
import org.jblocks.scriptengine.StoreableNativeBlock;
import org.jblocks.utils.StreamUtils;
import org.jblocks.utils.SwingUtils;

/**
 *
 * @author ZeroLuck
 */
public class JCyobEditor extends JPanel {

    private static final String PACKAGE_PREFIX = "org.jblocks.ext.block";
    private final CloseableJTabbedPane tabs;
    private final JToolBar tools;
    private final JTextArea output;

    public JCyobEditor() {
        super(new BorderLayout());
        tabs = new CloseableJTabbedPane();
        tools = new JToolBar();
        output = new JTextArea("Output:\n");
        if (!CodeCompiler.compilerAvailable()) {
            out("Sorry, you aren't able to compile your blocks! (JDK is missing)");
        }
        output.setFont(new Font(MultiSyntaxDocument.DEFAULT_FONT_FAMILY, Font.PLAIN, MultiSyntaxDocument.DEFAULT_FONT_SIZE));
        output.setEditable(false);

        JButton saveBlock = new JButton(JBlocks.getIcon("save.png"));
        saveBlock.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                saveBlock();
            }
        });
        tools.add(saveBlock);

        JButton openBlock = new JButton(JBlocks.getIcon("open.png"));
        openBlock.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openBlock();
            }
        });
        tools.add(openBlock);

        JButton newBlock = new JButton(JBlocks.getIcon("new.png"));
        newBlock.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                newBlock();
            }
        });
        tools.add(newBlock);
        JButton compile = new JButton(JBlocks.getIcon("run-build.png"));
        compile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                compile(BlockModel.createID());
            }
        });
        tools.add(compile);

        tools.addSeparator();

        final JButton undo = new JButton(JBlocks.getIcon("undo.png"));
        final JButton redo = new JButton(JBlocks.getIcon("redo.png"));

        undo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JCodePane p = currentPane();
                if (p != null) {
                    p.undo();
                }
            }
        });
        redo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                JCodePane p = currentPane();
                if (p != null) {
                    p.redo();
                }
            }
        });

        tools.add(undo);
        tools.add(redo);
        tools.addSeparator();


        newBlock();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setTopComponent(tabs);
        split.setBottomComponent(new JScrollPane(output));
        split.setResizeWeight(0.85);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton finish = new JButton("Finish selected block");
        finish.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                finishBlock();
            }
        });
        south.add(finish);

        add(south, BorderLayout.SOUTH);
        add(tools, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void out(String s) {
        output.append(s + "\n");
    }
    private int blockCounter = 1;
    private JFileChooser chooser;

    private void newBlock() {
        JCodePane code = new JCodePane();
        code.setText(getDefaultText());

        tabs.addTab("Block " + (blockCounter++), code);
    }

    private String getClassName(String text) throws IOException {
        String main = "";
        StreamTokenizer tok = new StreamTokenizer(new StringReader(text));
        tok.slashSlashComments(true);
        tok.slashStarComments(true);

        while (tok.nextToken() != StreamTokenizer.TT_EOF) {
            if (tok.sval != null && tok.sval.equals("class")) {
                tok.nextToken();
                main = tok.sval;
                return main;
            }
        }
        return null;
    }

    private void finishBlock() {
        JCodePane code = currentPane();
        if (code != null) {
            final String text = code.getText();
            final Map<String, byte[]> files;
            final long ID = BlockModel.createID();
            if ((files = compile(ID)) != null) {
                final BlockTypeChooser ch = new BlockTypeChooser();
                final JInternalFrame frm =
                        SwingUtils.showInternalFrame(JBlocks.getContextForComponent(this).getDesktop(), ch, "Select a block type");

                ch.addBlockTypeListener(new BlockTypeChooserListener() {

                    @Override
                    public void cancel() {
                        frm.dispose();
                    }

                    @Override
                    public void finished(String type, String category, String label, Color c) {
                        cancel();
                        try {
                            files.put("source.java", text.getBytes());
                            String block = StoreableNativeBlock.createData(files, getFullClassName(ID, getClassName(text)));
                            BlockModel model = BlockModel.createModel(type, category, label, ID);
                            model.setCode(StoreableNativeBlock.load(block));
                            JBlocks.getContextForComponent(JCyobEditor.this).installBlock(model);
                            tabs.removeTabAt(tabs.getSelectedIndex());
                            JOptionPane.showInternalMessageDialog(JCyobEditor.this, "The block is now finished!\n(Look in the Block-Editor)");
                        } catch (Exception io) {
                            JOptionPane.showInternalMessageDialog(JCyobEditor.this, "Error: \n" + io, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }
        }
    }

    private void open(File f) {
        try {
            FileReader r = new FileReader(f);
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int len;
            while ((len = r.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
            r.close();


            JCodePane code = new JCodePane();
            code.setText(sb.toString());

            tabs.addTab(f.getName(), code);
        } catch (IOException io) {
            JOptionPane.showInternalMessageDialog(this, "Error while saving file:\n" + io);
        }
    }

    private void save(File f) {
        JCodePane code = currentPane();
        if (code != null) {
            try {
                FileWriter w = new FileWriter(StreamUtils.addFileExtension(f.getAbsolutePath(), "java"));
                w.write(code.getText());
                w.close();
            } catch (IOException io) {
                JOptionPane.showInternalMessageDialog(this, "Error while saving file:\n" + io);
            }
        }
    }

    private void ensureChooserExists() {
        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setFileFilter(SwingUtils.createFilter(new String[]{"java"}, "Java source files (.java)"));
            chooser.setMultiSelectionEnabled(false);
            chooser.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (SwingUtils.isApproveSelection(ae) && chooser.getSelectedFile() != null) {
                        switch (chooser.getDialogType()) {
                            case JFileChooser.SAVE_DIALOG:
                                save(chooser.getSelectedFile());
                                break;
                            case JFileChooser.OPEN_DIALOG:
                                open(chooser.getSelectedFile());
                                break;
                        }
                    }
                }
            });
        }
    }

    private void saveBlock() {
        ensureChooserExists();
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Save a source file");
        SwingUtils.showInternalFileChooser(JBlocks.getContextForComponent(this).getDesktop(),
                chooser);
    }

    private void openBlock() {
        ensureChooserExists();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Open a source file");
        SwingUtils.showInternalFileChooser(JBlocks.getContextForComponent(this).getDesktop(),
                chooser);
    }

    private JCodePane currentPane() {
        int current = tabs.getSelectedIndex();
        if (current >= 0) {
            return (JCodePane) tabs.getComponentAt(current);
        }
        return null;
    }

    private String getFullClassName(long ID, String name) {
        return PACKAGE_PREFIX + Math.abs(ID) + "." + name;
    }

    private Map<String, byte[]> compile(long ID) {
        if (!CodeCompiler.compilerAvailable()) {
            return null;
        }

        JCodePane pane = currentPane();
        if (pane != null) {
            String text = pane.getText();
            text = text.replace("${block.id}", Long.toString(ID) + "L");
            text = "package " + PACKAGE_PREFIX + Math.abs(ID) + ";" + text;
            try {
                String main = getClassName(text);

                out("Compiling: " + tabs.getTitleAt(tabs.getSelectedIndex()) + ": " + main + " [");
                StringBuilder sb = new StringBuilder();
                Map<String, byte[]> files = CodeCompiler.compile(main, text, sb);
                output.append(sb.toString());
                return files;
            } catch (Throwable ex) {
                out("ERROR: Exception occured: " + ex);
            } finally {
                out("] finished.");
            }
        }
        return null;
    }
    private static String defaultText;

    private static String getDefaultText() {
        if (defaultText != null) {
            return defaultText;
        }
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(JCyobEditor.class.getResourceAsStream("default.txt")));
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line).append('\n');
            }

            String userName = System.getProperty("user.name");
            if (userName == null) {
                userName = "";
            }

            return defaultText = sb.toString().replace("${user.name}", userName);
        } catch (Exception io) {
            return "// Couldn't load the default template.";
        }
    }
}
