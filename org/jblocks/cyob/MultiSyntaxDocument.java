package org.jblocks.cyob;

import java.awt.*;
import java.util.*;
import javax.swing.text.*;

/**
 * Highlights syntax in a DefaultStyledDocument.  Allows any number of keywords.
 * @author camickr (primary author; java sun forums user)
 * @author David Underhill
 * @author ZeroLuck
 */
public class MultiSyntaxDocument extends DefaultStyledDocument {

    public static final String DEFAULT_FONT_FAMILY = "Courier New";
    public static final int DEFAULT_FONT_SIZE = 12;
    
    public static final SimpleAttributeSet DEFAULT_NORMAL;
    public static final SimpleAttributeSet DEFAULT_COMMENT;
    public static final SimpleAttributeSet DEFAULT_STRING;
    public static final SimpleAttributeSet DEFAULT_KEYWORD;

    static {
        DEFAULT_NORMAL = new SimpleAttributeSet();
        StyleConstants.setForeground(DEFAULT_NORMAL, Color.BLACK);
        StyleConstants.setFontFamily(DEFAULT_NORMAL, DEFAULT_FONT_FAMILY);
        StyleConstants.setFontSize(DEFAULT_NORMAL, DEFAULT_FONT_SIZE);

        DEFAULT_COMMENT = new SimpleAttributeSet();
        StyleConstants.setForeground(DEFAULT_COMMENT, new java.awt.Color(150, 150, 150));
        StyleConstants.setFontFamily(DEFAULT_COMMENT, DEFAULT_FONT_FAMILY);
        StyleConstants.setFontSize(DEFAULT_COMMENT, DEFAULT_FONT_SIZE);

        DEFAULT_STRING = new SimpleAttributeSet();
        StyleConstants.setForeground(DEFAULT_STRING, new java.awt.Color(206, 123, 0));
        StyleConstants.setFontFamily(DEFAULT_STRING, DEFAULT_FONT_FAMILY);
        StyleConstants.setFontSize(DEFAULT_STRING, DEFAULT_FONT_SIZE);

        //default style for new keyword types
        DEFAULT_KEYWORD = new SimpleAttributeSet();
        StyleConstants.setForeground(DEFAULT_KEYWORD, new java.awt.Color(0, 0, 230));
        StyleConstants.setBold(DEFAULT_KEYWORD, true);
        StyleConstants.setFontFamily(DEFAULT_KEYWORD, DEFAULT_FONT_FAMILY);
        StyleConstants.setFontSize(DEFAULT_KEYWORD, DEFAULT_FONT_SIZE);
    }
    
    private DefaultStyledDocument doc;
    private Element rootElement;
    private boolean multiLineComment;
    private MutableAttributeSet normal = DEFAULT_NORMAL;
    private MutableAttributeSet comment = DEFAULT_COMMENT;
    private MutableAttributeSet quote = DEFAULT_STRING;
    private Map<String, MutableAttributeSet> keywords;
    private int fontSize = DEFAULT_FONT_SIZE;
    private String fontName = DEFAULT_FONT_FAMILY;

    @SuppressWarnings("LeakingThisInConstructor")
    public MultiSyntaxDocument(final Map<String, MutableAttributeSet> keywords) {
        doc = this;
        rootElement = doc.getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
        this.keywords = keywords;
    }

    @SuppressWarnings("PublicInnerClass")
    public enum ATTR_TYPE {

        Normal, Comment, Quote;
    }

    /**
     * Sets the font of the specified attribute
     * @param attr   the attribute to apply this font to (normal, comment, string)
     * @param style  font style (Font.BOLD, Font.ITALIC, Font.PLAIN)
     */
    public void setAttributeFont(ATTR_TYPE attr, int style) {
        Font f = new Font(fontName, style, fontSize);
        if (attr == ATTR_TYPE.Comment) {
            setAttributeFont(comment, f);
        } else if (attr == ATTR_TYPE.Quote) {
            setAttributeFont(quote, f);
        } else {
            setAttributeFont(normal, f);
        }
    }

    /**
     * Sets the font of the specified attribute
     * @param attr  attribute to apply this font to
     * @param f     the font to use 
     */
    public static void setAttributeFont(MutableAttributeSet attr, Font f) {
        StyleConstants.setBold(attr, f.isBold());
        StyleConstants.setItalic(attr, f.isItalic());
        StyleConstants.setFontFamily(attr, f.getFamily());
        StyleConstants.setFontSize(attr, f.getSize());
    }

    /**
     * Sets the foreground (font) color of the specified attribute
     * @param attr  the attribute to apply this font to (normal, comment, string)
     * @param c     the color to use 
     */
    public void setAttributeColor(ATTR_TYPE attr, Color c) {
        if (attr == ATTR_TYPE.Comment) {
            setAttributeColor(comment, c);
        } else if (attr == ATTR_TYPE.Quote) {
            setAttributeColor(quote, c);
        } else {
            setAttributeColor(normal, c);
        }
    }

    /**
     * Sets the foreground (font) color of the specified attribute
     * @param attr  attribute to apply this color to
     * @param c  the color to use 
     */
    public static void setAttributeColor(MutableAttributeSet attr, Color c) {
        StyleConstants.setForeground(attr, c);
    }

    /**
     * Associates a keyword with a particular formatting style
     * @param keyword  the token or word to format
     * @param attr     how to format keyword
     */
    public void addKeyword(String keyword, MutableAttributeSet attr) {
        keywords.put(keyword, attr);
    }

    /**
     * Gets the formatting for a keyword
     *
     * @param keyword  the token or word to stop formatting
     * @return how keyword is formatted, or null if no formatting is applied to it
     */
    public MutableAttributeSet getKeywordFormatting(String keyword) {
        return keywords.get(keyword);
    }

    /**
     * Removes an association between a keyword with a particular formatting style
     * @param keyword  the token or word to stop formatting
     */
    public void removeKeyword(String keyword) {
        keywords.remove(keyword);
    }

    /** sets the number of characters per tab */
    @SuppressWarnings("deprecation")
    public void setTabs(int charactersPerTab) {
        Font f = new Font(fontName, Font.PLAIN, fontSize);
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * charactersPerTab;
        TabStop[] tabs = new TabStop[35];
        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }
        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = this.getLength();
        this.setParagraphAttributes(0, length, attributes, false);
    }

    @Override
    @SuppressWarnings("AssignmentToMethodParameter")
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (str.equals("{")) {
            str = addMatchingBrace(offset);
        }
        super.insertString(offset, str, a);
        processChangedLines(offset, str.length());
    }

    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }

    /**
     *  Determine how many lines have been changed,
     *  then apply highlighting to each line
     */
    public void processChangedLines(int offset, int length)
            throws BadLocationException {
        String content = doc.getText(0, doc.getLength());
        //  The lines affected by the latest document update
        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);
        //  Make sure all comment lines prior to the start line are commented
        //  and determine if the start line is still in a multi line comment
        setMultiLineComment(commentLinesBefore(content, startLine));
        //  Do the actual highlighting
        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(content, i);
        }
        //  Resolve highlighting to the next end multi line delimiter
        if (isMultiLineComment()) {
            commentLinesAfter(content, endLine);
        } else {
            highlightLinesAfter(content, endLine);
        }
    }

    private boolean commentLinesBefore(String content, int line) {
        int offset = rootElement.getElement(line).getStartOffset();
        //  Start of comment not found, nothing to do
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);
        if (startDelimiter < 0) {
            return false;
        }
        //  Matching start/end of comment found, nothing to do
        int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);
        if (endDelimiter < offset & endDelimiter != -1) {
            return false;
        }
        //  End of comment not found, highlight the lines
        doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
        return true;
    }

    private void commentLinesAfter(String content, int line) {
        int offset = rootElement.getElement(line).getEndOffset();
        //  End of comment not found, nothing to do
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (endDelimiter < 0) {
            return;
        }
        //  Matching start/end of comment found, comment the lines
        int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);
        if (startDelimiter < 0 || startDelimiter <= offset) {
            doc.setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
        }
    }

    private void highlightLinesAfter(String content, int line)
            throws BadLocationException {
        int offset = rootElement.getElement(line).getEndOffset();
        //  Start/End delimiter not found, nothing to do
        int startDelimiter = indexOf(content, getStartDelimiter(), offset);
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);
        if (startDelimiter < 0) {
            startDelimiter = content.length();
        }
        if (endDelimiter < 0) {
            endDelimiter = content.length();
        }
        int delimiter = Math.min(startDelimiter, endDelimiter);
        if (delimiter < offset) {
            return;
        }
        //  Start/End delimiter found, reapply highlighting
        int endLine = rootElement.getElementIndex(delimiter);
        for (int i = line + 1; i < endLine; i++) {
            Element branch = rootElement.getElement(i);
            Element leaf = doc.getCharacterElement(branch.getStartOffset());
            AttributeSet as = leaf.getAttributes();
            if (as.isEqual(comment)) {
                applyHighlighting(content, i);
            }
        }
    }

    private void applyHighlighting(String content, int line)
            throws BadLocationException {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset() - 1;
        int lineLength = endOffset - startOffset;
        int contentLength = content.length();
        if (endOffset >= contentLength) {
            endOffset = contentLength - 1;
        }
        if (endingMultiLineComment(content, startOffset, endOffset)
                || isMultiLineComment()
                || startingMultiLineComment(content, startOffset, endOffset)) {
            doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
            return;
        }
        doc.setCharacterAttributes(startOffset, lineLength, normal, true);
        int index = content.indexOf(getSingleLineDelimiter(), startOffset);
        if ((index > -1) && (index < endOffset)) {
            doc.setCharacterAttributes(index, endOffset - index + 1, comment, false);
            endOffset = index - 1;
        }
        checkForTokens(content, startOffset, endOffset);
    }

    private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
            throws BadLocationException {
        int index = indexOf(content, getStartDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset)) {
            return false;
        } else {
            setMultiLineComment(true);
            return true;
        }
    }

    private boolean endingMultiLineComment(String content, int startOffset, int endOffset)
            throws BadLocationException {
        int index = indexOf(content, getEndDelimiter(), startOffset);
        if ((index < 0) || (index > endOffset)) {
            return false;
        } else {
            setMultiLineComment(false);
            return true;
        }
    }

    private boolean isMultiLineComment() {
        return multiLineComment;
    }

    private void setMultiLineComment(boolean value) {
        multiLineComment = value;
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    private void checkForTokens(String content, int startOffset, int endOffset) {
        while (startOffset <= endOffset) {
            //  skip the delimiters to find the start of a new token
            while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                if (startOffset < endOffset) {
                    startOffset++;
                } else {
                    return;
                }
            }
            //  Extract and process the entire token
            if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1))) {
                startOffset = getQuoteToken(content, startOffset, endOffset);
            } else {
                startOffset = getOtherToken(content, startOffset, endOffset);
            }
        }
    }

    private int getQuoteToken(String content, int startOffset, int endOffset) {
        String quoteDelimiter = content.substring(startOffset, startOffset + 1);
        String escapeString = getEscapeString(quoteDelimiter);
        int index;
        int endOfQuote = startOffset;
        index = content.indexOf(escapeString, endOfQuote + 1);
        while ((index > -1) && (index < endOffset)) {
            endOfQuote = index + 1;
            index = content.indexOf(escapeString, endOfQuote);
        }
        index = content.indexOf(quoteDelimiter, endOfQuote + 1);
        if ((index < 0) || (index > endOffset)) {
            endOfQuote = endOffset;
        } else {
            endOfQuote = index;
        }
        doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);
        return endOfQuote + 1;
    }

    private int getOtherToken(String content, int startOffset, int endOffset) {
        int endOfToken = startOffset + 1;
        while (endOfToken <= endOffset) {
            if (isDelimiter(content.substring(endOfToken, endOfToken + 1))) {
                break;
            }
            endOfToken++;
        }
        String token = content.substring(startOffset, endOfToken);
        MutableAttributeSet attr = keywords.get(token);
        if (attr != null) {
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, attr, false);
        }
        return endOfToken + 1;
    }

    @SuppressWarnings({"AssignmentToMethodParameter", "NestedAssignment"})
    private int indexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.indexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle)) {
                break;
            } else {
                offset = index + 1;
            }
        }

        return index;
    }

    @SuppressWarnings({"AssignmentToMethodParameter", "NestedAssignment"})
    private int lastIndexOf(String content, String needle, int offset) {
        int index;
        while ((index = content.lastIndexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();
            if (text.startsWith(needle) || text.endsWith(needle)) {
                break;
            } else {
                offset = index - 1;
            }
        }

        return index;
    }

    private String getLine(String content, int offset) {
        int line = rootElement.getElementIndex(offset);
        Element lineElement = rootElement.getElement(line);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset();
        return content.substring(start, end - 1);
    }

    /*
     *  Override for other languages
     */
    protected boolean isDelimiter(String character) {
        String operands = ";:{}()[]+-/%<=>!&|^~*";
        if (Character.isWhitespace(character.charAt(0))
                || operands.indexOf(character) != -1) {
            return true;
        } else {
            return false;
        }
    }

    /*
     *  Override for other languages
     */
    protected boolean isQuoteDelimiter(String character) {
        String quoteDelimiters = "\"'";
        if (quoteDelimiters.indexOf(character) < 0) {
            return false;
        } else {
            return true;
        }
    }

    /*
     *  Override for other languages
     */
    protected String getStartDelimiter() {
        return "/*";
    }

    /*
     *  Override for other languages
     */
    protected String getEndDelimiter() {
        return "*/";
    }

    /*
     *  Override for other languages
     */
    protected String getSingleLineDelimiter() {
        return "//";
    }

    /*
     *  Override for other languages
     */
    protected String getEscapeString(String quoteDelimiter) {
        return "\\" + quoteDelimiter;
    }

    protected String addMatchingBrace(int offset) throws BadLocationException {
        StringBuilder whiteSpace = new StringBuilder(16);
        int line = rootElement.getElementIndex(offset);
        int i = rootElement.getElement(line).getStartOffset();
        while (true) {
            String temp = doc.getText(i, 1);
            if (temp.equals(" ") || temp.equals("\t")) {
                whiteSpace.append(temp);
                i++;
            } else {
                break;
            }
        }
        return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
    }

    /** gets the current font size */
    public int getFontSize() {
        return fontSize;
    }

    /** sets the current font size (affects all built-in styles) */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        StyleConstants.setFontSize(normal, fontSize);
        StyleConstants.setFontSize(quote, fontSize);
        StyleConstants.setFontSize(comment, fontSize);
    }

    /** gets the current font family */
    public String getFontName() {
        return fontName;
    }

    /** sets the current font family (affects all built-in styles) */
    public void setFontName(String fontName) {
        this.fontName = fontName;
        StyleConstants.setFontFamily(normal, fontName);
        StyleConstants.setFontFamily(quote, fontName);
        StyleConstants.setFontFamily(comment, fontName);
    }
}
