/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.scriptengine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jblocks.JBlocks;
import org.jblocks.editor.BlockIO;
import org.jblocks.editor.BlockModel;
import org.jblocks.utils.XMLOutputStream;

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
}
