package org.jblocks.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ZeroLuck
 */
public class XMLCompressor {

    // elements
    private static final byte BEGINN_OF_ELEMENT = 0;
    private static final byte END_OF_ELEMENT = 1;
    private static final byte EMPTY_ELEMENT = 3;
    private static final byte TEXT = 6;
    private static final byte ATTRIBUTE = 7;
    // headers
    private static final byte TEXT_ATTRIBUTE = 0;
    private static final byte LONG_ATTRIBUTE = 1;
    private static final byte BYTE_ATTRIBUTE = 2;
    private static final byte INT_ATTRIBUTE = 3;
    private static final byte EMPTY_ATTRIBUTE = 4;

    private static byte[] compressText(String t) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        if (t.isEmpty()) {
            out.write(EMPTY_ATTRIBUTE);
        } else {
            try {
                byte val = Byte.valueOf(t);
                out.write(BYTE_ATTRIBUTE);
                out.write(val);
            } catch (NumberFormatException ex1) {
                try {
                    int val = Integer.valueOf(t);
                    out.write(INT_ATTRIBUTE);
                    out.writeInt(val);
                } catch (NumberFormatException ex2) {
                    try {
                        long val = Long.valueOf(t);
                        out.write(LONG_ATTRIBUTE);
                        out.writeLong(val);
                    } catch (NumberFormatException ex3) {
                        out.write(TEXT_ATTRIBUTE);
                        out.writeUTF(t);
                    }
                }
            }
        }
        out.flush();
        return bout.toByteArray();
    }

    private static void compressNode(Node n, DataOutputStream out) throws IOException {
        NamedNodeMap attributes = n.getAttributes();
        NodeList children = n.getChildNodes();
        short type = n.getNodeType();
        String name = n.getNodeName();
        String text = n.getTextContent();

        if (type == Node.ATTRIBUTE_NODE) {
            out.write(ATTRIBUTE);
            out.writeUTF(name);
            out.write(compressText(text));
        } else if (type == Node.TEXT_NODE) {
            if (!text.trim().isEmpty()) {
                out.write(TEXT);
                out.write(compressText(text));
            }
        } else if (type == Node.ELEMENT_NODE || type == Node.ENTITY_NODE) {
            if (children.getLength() == 0 && attributes.getLength() == 0) {
                out.write(EMPTY_ELEMENT);
                out.writeUTF(name);
            } else {
                out.write(BEGINN_OF_ELEMENT);
                out.writeUTF(name);
                for (int i = 0; i < attributes.getLength(); i++) {
                    compressNode(attributes.item(i), out);
                }
                for (int i = 0; i < children.getLength(); i++) {
                    compressNode(children.item(i), out);
                }
                out.write(END_OF_ELEMENT);
            }
        }
    }

    private static String uncompressText(DataInputStream in) throws IOException {
        byte header = in.readByte();
        switch (header) {
            case EMPTY_ATTRIBUTE:
                return "";
            case BYTE_ATTRIBUTE:
                return "" + in.readByte();
            case INT_ATTRIBUTE:
                return "" + in.readInt();
            case LONG_ATTRIBUTE:
                return "" + in.readLong();
            case TEXT_ATTRIBUTE:
                return in.readUTF();
        }
        throw new IOException();
    }

    private static void uncompressNode(byte h, DataInputStream in, XMLOutputStream xml) throws IOException {
        if (h == ATTRIBUTE) {
            xml.writeAttribute(in.readUTF(), uncompressText(in));
        } else if (h == TEXT) {
            xml.writeText(uncompressText(in));
        } else if (h == EMPTY_ELEMENT) {
            xml.writeStartElement(in.readUTF());
            xml.writeEndElement();
        } else if (h == BEGINN_OF_ELEMENT) {
            xml.writeStartElement(in.readUTF());
            byte header;
            while ((header = in.readByte()) != END_OF_ELEMENT) {
                uncompressNode(header, in, xml);
            }
            xml.writeEndElement();
        } else {
            throw new IOException();
        }
    }

    public static void uncompress(InputStream is, OutputStream os) throws IOException {
        InflaterInputStream def = new InflaterInputStream(is);
        DataInputStream in = new DataInputStream(def);
        XMLOutputStream out = new XMLOutputStream(os, true);
        in.readByte();
        byte header;
        while ((header = in.readByte()) != END_OF_ELEMENT) {
            uncompressNode(header, in, out);
        }
        out.flush();
    }

    public static void compress(InputStream in, OutputStream os) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);

        NodeList children = doc.getChildNodes();

        DeflaterOutputStream def = new DeflaterOutputStream(os);
        DataOutputStream out = new DataOutputStream(def);
        out.write(BEGINN_OF_ELEMENT);
        for (int i = 0; i < children.getLength(); i++) {
            compressNode(children.item(i), out);
        }
        out.write(END_OF_ELEMENT);
        def.finish();
    }
}
