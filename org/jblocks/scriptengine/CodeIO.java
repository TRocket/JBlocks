/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.scriptengine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jblocks.JBlocks;
import org.jblocks.editor.BlockIO;
import org.jblocks.editor.BlockModel;
import org.jblocks.utils.XMLOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author ZeroLuck
 */
public class CodeIO {
    
    private static void writeModel(XMLOutputStream writer, BlockModel model) throws IOException {
        writer.writeEntry("syntax", model.getSyntax());
        writer.writeEntry("category", model.getCategory());
        if (model.getContent() != null) {
            writer.writeEntry("content", model.getContent());
        }
        writer.writeEntry("type", model.getType());
        writer.writeEntry("id", Long.toString(model.getID()));
    }
    
    private static List<Block> getBlocksForSequence(List<Block> list, Object[] p) {
        for (Object obj : p) {
            if (obj instanceof Block) {
                getBlocksFor(list, (Block) obj);
            } else if (obj instanceof Object[]) {
                getBlocksForSequence(list, (Object[]) obj);
            }
        }
        return list;
    }
    
    private static List<Block> getBlocksFor(List<Block> list, Block b) {
        if (!list.contains(b)) {
            list.add(b);
        }
        for (Object param : b.getParameters()) {
            if (param instanceof Block) {
                getBlocksFor(list, (Block) param);
            } else if (param instanceof Object[]) {
                getBlocksForSequence(list, (Object[]) param);
            }
        }
        return list;
    }
    
    private static void writeDepending(XMLOutputStream writer, JBlocks ctx, ByobBlock byob) throws IOException {
        List<Block> blocks = getBlocksForSequence(new ArrayList<Block>(), byob.getSequence());
        for (Block b : blocks) {
            long ID = b.getID();
            if (!ctx.isDefaultBlock(ID)) {
                writer.writeEntry("id", Long.toString(ID));
            }
        }
    }

    /**
     * Writes a block to an OutputStream. <br />
     * 
     * @param ctx the context of JBlocks
     * @param out the OutputStream to which to write
     * @param b the block
     * @throws IOException 
     */
    public static void writeBlock(JBlocks ctx, OutputStream out, Block b) throws IOException {
        BlockModel model = ctx.getInstalledBlocks().get(b.getID());
        
        XMLOutputStream writer = new XMLOutputStream(out, true);
        writer.writeStartElement("block");
        writer.writeAttribute("type", b instanceof NativeBlock ? "native" : "byob");
        writer.writeAttribute("parameters", Integer.toString(b.getParameterCount()));
        
        writer.writeStartElement("model");
        writeModel(writer, model);
        writer.writeEndElement();
        
        if (b instanceof ByobBlock) {
            writer.writeStartElement("depending");
            writeDepending(writer, ctx, (ByobBlock) b);
            writer.writeEndElement();
        }
        
        writer.writeStartElement("code");
        if (b instanceof NativeBlock) {
            NativeBlock nb = (NativeBlock) b;
            // TODO
            throw new UnsupportedOperationException("not implemented yet");
        } else {
            ByobBlock byob = (ByobBlock) b;
            BlockIO.writeToXML(writer, byob.getSequence());
        }
        writer.writeEndElement();
        
        writer.writeEndElement();
        writer.flush();
    }
    
    private static Node nodeForName(NodeList list, String name) {
        int len = list.getLength();
        for (int i = 0; i < len; i++) {
            Node n = list.item(i);
            if (n.getNodeName().equalsIgnoreCase(name)) {
                return n;
            }
        }
        return null;
    }
    
    private static BlockModel readModel(Node n) {
        NodeList children = n.getChildNodes();
        String syntax = nodeForName(children, "syntax").getTextContent();
        String category = nodeForName(children, "category").getTextContent();
        String type = nodeForName(children, "type").getTextContent();
        String id = nodeForName(children, "id").getTextContent();
        
        BlockModel model = BlockModel.createModel(type, category, syntax, Long.valueOf(id));

        // 'content' is an optional entry
        Node content = nodeForName(children, "content");
        if (content != null) {
            model.setContent(content.getTextContent());
        }
        
        return model;
    }
    
    public static BlockModel readBlock(JBlocks ctx, InputStream in) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);
        
        Node root = doc.getFirstChild();
        String type = root.getAttributes().getNamedItem("type").getTextContent();
        int parameters = Integer.parseInt(root.getAttributes().getNamedItem("parameters").getTextContent());
        
        Block b = null;
        BlockModel model = readModel(nodeForName(root.getChildNodes(), "model"));
        
        if (type.equals("byob")) {
            b = new ByobBlock(parameters, model.getID(), BlockIO.readFromXML(ctx, nodeForName(root.getChildNodes(), "code")));
        } else if (type.equals("native")) {
            b = new NativeBlock(parameters, model.getID());
        } else {
            throw new IOException("block type '" + type + "' isn't supported!");
        }
        model.setCode(b);
        
        return model;
    }
}
