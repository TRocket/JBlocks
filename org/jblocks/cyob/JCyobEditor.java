package org.jblocks.cyob;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import org.jblocks.JBlocks;
import org.jblocks.editor.BlockModel;
import org.jblocks.utils.TestUtils;

/**
 *
 * @author ZeroLuck
 */
public class JCyobEditor extends JPanel {

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
                compile();
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
        split.setResizeWeight(0.75);

        add(tools, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    private void out(String s) {
        output.append(s + "\n");
    }
    private int blockCounter = 1;

    private void newBlock() {
        JCodePane code = new JCodePane();
        code.setText(getDefaultText());

        tabs.addTab("Block " + (blockCounter++), new JScrollPane(code));
    }

    private JCodePane currentPane() {
        int current = tabs.getSelectedIndex();
        if (current >= 0) {
            return (JCodePane) (((JScrollPane) tabs.getComponentAt(current)).getViewport().getView());
        }
        return null;
    }

    private void compile() {
        if (!CodeCompiler.compilerAvailable())
            return;
        
        JCodePane pane = currentPane();
        if (pane != null) {
            String text = pane.getText();
            long ID = BlockModel.createID();
            text = text.replace("${block.id}", Long.toString(ID) + "L");
            text = "package org.jblocks.ext.block" + Math.abs(ID) + ";" + text;
            try {
                String main = "";
                StreamTokenizer tok = new StreamTokenizer(new StringReader(text));
                tok.slashSlashComments(true);
                tok.slashStarComments(true);

                while (tok.nextToken() != StreamTokenizer.TT_EOF) {
                    if (tok.sval != null && tok.sval.equals("class")) {
                        tok.nextToken();
                        main = tok.sval;
                    }
                }

                out("Compiling: " + main + " [");
                StringBuilder sb = new StringBuilder();
                Map<String, byte[]> files = CodeCompiler.compile(main, text, sb);
                output.append(sb.toString());
            } catch (Throwable ex) {
                out("ERROR: Exception occured: " + ex);
            } finally {
                out("] finished.");
            }

        }
    }

    private static String getDefaultText() {
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

            return sb.toString().replace("${user.name}", userName);
        } catch (Exception io) {
            return "// Couldn't load the default template.";
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                TestUtils.displayWithSize(new JCyobEditor(), "Cyob Editor", new Dimension(600, 400));
            }
        });
    }
}
