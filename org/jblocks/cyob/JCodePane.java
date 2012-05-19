package org.jblocks.cyob;

import java.util.Map;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import java.util.HashMap;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyledEditorKit;

import static org.jblocks.cyob.MultiSyntaxDocument.*;

/**
 *
 * @author ZeroLuck
 */
public class JCodePane extends JPanel {

    public static final Map<String, MutableAttributeSet> javaKeywords;

    static {
        javaKeywords = new HashMap<String, MutableAttributeSet>(100);
        javaKeywords.put("public", DEFAULT_KEYWORD);
        javaKeywords.put("private", DEFAULT_KEYWORD);
        javaKeywords.put("abstract", DEFAULT_KEYWORD);
        javaKeywords.put("boolean", DEFAULT_KEYWORD);
        javaKeywords.put("break", DEFAULT_KEYWORD);
        javaKeywords.put("byte", DEFAULT_KEYWORD);
        javaKeywords.put("byvalue", DEFAULT_KEYWORD);
        javaKeywords.put("case", DEFAULT_KEYWORD);
        javaKeywords.put("cast", DEFAULT_KEYWORD);
        javaKeywords.put("catch", DEFAULT_KEYWORD);
        javaKeywords.put("char", DEFAULT_KEYWORD);
        javaKeywords.put("class", DEFAULT_KEYWORD);
        javaKeywords.put("const", DEFAULT_KEYWORD);
        javaKeywords.put("continue", DEFAULT_KEYWORD);
        javaKeywords.put("default", DEFAULT_KEYWORD);
        javaKeywords.put("do", DEFAULT_KEYWORD);
        javaKeywords.put("double", DEFAULT_KEYWORD);
        javaKeywords.put("else", DEFAULT_KEYWORD);
        javaKeywords.put("extends", DEFAULT_KEYWORD);
        javaKeywords.put("false", DEFAULT_KEYWORD);
        javaKeywords.put("final", DEFAULT_KEYWORD);
        javaKeywords.put("finally", DEFAULT_KEYWORD);
        javaKeywords.put("float", DEFAULT_KEYWORD);
        javaKeywords.put("for", DEFAULT_KEYWORD);
        javaKeywords.put("future", DEFAULT_KEYWORD);
        javaKeywords.put("generic", DEFAULT_KEYWORD);
        javaKeywords.put("goto", DEFAULT_KEYWORD);
        javaKeywords.put("if", DEFAULT_KEYWORD);
        javaKeywords.put("implements", DEFAULT_KEYWORD);
        javaKeywords.put("import", DEFAULT_KEYWORD);
        javaKeywords.put("inner", DEFAULT_KEYWORD);
        javaKeywords.put("instanceof", DEFAULT_KEYWORD);
        javaKeywords.put("int", DEFAULT_KEYWORD);
        javaKeywords.put("interface", DEFAULT_KEYWORD);
        javaKeywords.put("long", DEFAULT_KEYWORD);
        javaKeywords.put("native", DEFAULT_KEYWORD);
        javaKeywords.put("new", DEFAULT_KEYWORD);
        javaKeywords.put("null", DEFAULT_KEYWORD);
        javaKeywords.put("operator", DEFAULT_KEYWORD);
        javaKeywords.put("outer", DEFAULT_KEYWORD);
        javaKeywords.put("package", DEFAULT_KEYWORD);
        javaKeywords.put("private", DEFAULT_KEYWORD);
        javaKeywords.put("protected", DEFAULT_KEYWORD);
        javaKeywords.put("public", DEFAULT_KEYWORD);
        javaKeywords.put("rest", DEFAULT_KEYWORD);
        javaKeywords.put("return", DEFAULT_KEYWORD);
        javaKeywords.put("short", DEFAULT_KEYWORD);
        javaKeywords.put("static", DEFAULT_KEYWORD);
        javaKeywords.put("super", DEFAULT_KEYWORD);
        javaKeywords.put("switch", DEFAULT_KEYWORD);
        javaKeywords.put("synchronized", DEFAULT_KEYWORD);
        javaKeywords.put("this", DEFAULT_KEYWORD);
        javaKeywords.put("throw", DEFAULT_KEYWORD);
        javaKeywords.put("throws", DEFAULT_KEYWORD);
        javaKeywords.put("transient", DEFAULT_KEYWORD);
        javaKeywords.put("true", DEFAULT_KEYWORD);
        javaKeywords.put("try", DEFAULT_KEYWORD);
        javaKeywords.put("var", DEFAULT_KEYWORD);
        javaKeywords.put("void", DEFAULT_KEYWORD);
        javaKeywords.put("volatile", DEFAULT_KEYWORD);
        javaKeywords.put("while", DEFAULT_KEYWORD);
    }
    private final JTextPane textPane;

    public JCodePane() {
        this(javaKeywords, true);
    }

    public JCodePane(final Map<String, MutableAttributeSet> keywords, boolean edit) {
        EditorKit editorKit = new StyledEditorKit() {

            @Override
            public Document createDefaultDocument() {
                MultiSyntaxDocument doc = new MultiSyntaxDocument(keywords);
                doc.setTabs(4);
                doc.addUndoableEditListener(new UndoListener());
                return doc;
            }
        };
        textPane = new JTextPane();
        textPane.setEditorKitForContentType("text/java", editorKit);
        textPane.setContentType("text/java");
        textPane.setEditable(edit);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setRowHeaderView(new TextLineNumber(textPane));

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
    }
    private UndoManager mng = new UndoManager();

    public boolean undo() {
        if (mng.canUndo()) {
            mng.undo();
        }

        return mng.canUndo();
    }

    public boolean redo() {
        if (mng.canRedo()) {
            mng.redo();
        }

        return mng.canRedo();
    }

    public void setText(String code) {
        textPane.setText(code);
    }

    public String getText() {
        return textPane.getText();
    }

    private class UndoListener implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            mng.addEdit(e.getEdit());
        }
    }
}
