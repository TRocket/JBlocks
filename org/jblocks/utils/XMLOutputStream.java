package org.jblocks.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This XMLOutputStream supports indenting. <br />
 * 
 * @author ZeroLuck
 */
public class XMLOutputStream {

    private final boolean indent;
    private final OutputStreamWriter writer;
    private final List<Element> elements;
    private boolean firstLine = true;
    private boolean attributes;
    private String spaceFill = "  ";

    private static class Element {

        final String name;
        int children;
        boolean justAttributes = true;

        public Element(String name) {
            this.name = name;
        }
    }

    public XMLOutputStream(OutputStream out, boolean indent) {
        this.indent = indent;
        this.writer = new OutputStreamWriter(out);
        this.elements = new ArrayList<Element>();
    }

    private void write(String text) throws IOException {
        writer.write(text);
    }

    private void indent(boolean newLine) throws IOException {
        if (newLine && indent) {
            if (!firstLine) {
                write("\n");

                int size = elements.size();
                if (size > 0) {
                    StringBuilder space = new StringBuilder(size * spaceFill.length());
                    for (int i = 0; i < size; i++) {
                        space.append(spaceFill);
                    }
                    write(space.toString());
                }
            }
            firstLine = false;
        }
    }

    private String encode(String text) throws IOException {
        int size = text.length();
        StringBuilder sb = new StringBuilder(size + (size / 10));
        for (int i = 0; i < size; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private void markEndAttributes(boolean add) throws IOException {
        if (elements.size() > 0) {
            if (attributes) {
                write(">");
                attributes = false;
            }
            attributes = false;
            Element curr = elements.get(elements.size() - 1);
            curr.justAttributes = false;
            if (add) {
                curr.children++;
            }
        }
    }

    /**
     * Returns the filler, which is used to fill the indent. <br />
     * This can be a tabulator or some spaces for example. <br />
     * The filler is ignored when indenting is not activated. <br />
     * 
     * @see #setIndentFiller(java.lang.String) 
     */
    public String getIndentFiller() {
        return spaceFill;
    }

    /**
     * Sets the filler, which should be used to fill the indent. <br />
     * This can be a tabulator or some spaces for example. <br />
     * The filler is ignored when indenting is not activated. <br />
     * 
     * @param filler the new filler for indenting
     */
    public void setIndentFiller(String filler) {
        spaceFill = filler;
    }

    /**
     * Writes the start of an element. <br />
     * The element mustn't contain the XML-quotes. <br />
     * 
     * @see #writeEndElement() 
     * @param name the name of the element
     * @throws IOException
     */
    public void writeStartElement(String name) throws IOException {
        markEndAttributes(true);
        indent(true);
        attributes = true;
        write("<" + name);
        elements.add(new Element(name));
    }

    /**
     * Writes an attribute to the stream. <br />
     * This has to be called before starting writing {@link #writeStartElement(java.lang.String)}. <br />
     * The value will be enquoted. <br />
     * 
     * @param key the key of the attribute. (the name)
     * @param value the value of the attribute.
     * @throws IOException 
     */
    public void writeAttribute(String key, String value) throws IOException {
        write(" " + key + "=\"" + encode(value) + "\"");
    }

    /**
     * Writes text to the stream. <br />
     * The text will be enquoted. <br />
     * 
     * @param text the text to write
     * @throws IOException 
     */
    public void writeText(String text) throws IOException {
        markEndAttributes(false);
        write(encode(text));
    }

    public void writeEntry(String name, String value) throws IOException {
        writeStartElement(name);
        writeText(value);
        writeEndElement();
    }

    /**
     * Writes the end of the current element. <br />
     * This will throw an exception if there is no element to close. <br/>
     * 
     * @see #writeStartElement(java.lang.String) 
     * @throws IOException 
     */
    public void writeEndElement() throws IOException {
        if (elements.isEmpty()) {
            throw new IOException("There is no element to close!");
        }
        int size = elements.size();
        Element element = elements.get(size - 1);
        elements.remove(size - 1);

        if (element.justAttributes) {
            write(" />");
        } else {
            if (attributes) {
                write(">");
            }
            if (element.children > 0) {
                indent(true);
            }
            write("</" + element.name + ">");
        }
        attributes = false;
    }

    /**
     * Flushes the stream. <br />
     * 
     * @throws IOException 
     */
    public void flush() throws IOException {
        writer.flush();
    }

    /**
     * Writes a new line to the stream, and closes the parent stream. <br />
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        write("\n");
        writer.close();
    }
}
