package org.jblocks.cyob;

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
class JCodePane extends JTextPane {

    public JCodePane() {
        final HashMap<String, MutableAttributeSet> javaKeywords = new HashMap<String, MutableAttributeSet>(16);
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
        EditorKit editorKit = new StyledEditorKit() {

            @Override
            public Document createDefaultDocument() {
                MultiSyntaxDocument doc = new MultiSyntaxDocument(javaKeywords);
                doc.setTabs(4);
                doc.addUndoableEditListener(new UndoListener());
                return doc;
            }
        };

        setEditorKitForContentType("text/java", editorKit);
        setContentType("text/java");
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

    private class UndoListener implements UndoableEditListener {

        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            mng.addEdit(e.getEdit());
        }
    }
}
