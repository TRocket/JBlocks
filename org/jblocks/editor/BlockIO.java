package org.jblocks.editor;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jblocks.JBlocks;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.NativeBlock;
import org.jblocks.utils.XMLOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * The BlockIO class provides methods to write and read blocks or scripts. <br />
 * You can use this class for making a deep copy of scripts too. <br />
 * Just the ID of the {@link BlockModel} is stored, so
 * use {@link org.jblocks.scriptengine.CodeIO} to store created {@link org.jblocks.scriptengine.ByobBlock}s and {@link NativeBlock}s. <br />
 * The output format is XML.
 * </p>
 *
 * @see ScriptGrabber 
 * @author ZeroLuck
 */
public class BlockIO {

    /*
     * ===========================================
     * =========== Writing blocks  ===============
     * ===========================================
     */
    private static void writeSequence(XMLOutputStream writer, Object[] params) throws IOException {
        for (Object param : params) {
            if (param instanceof Block) {
                writeBlock(writer, (Block) param);
            } else if (param instanceof Block[]) {
                writer.writeStartElement("sequence");
                writeSequence(writer, (Block[]) param);
                writer.writeEndElement();
            } else if (param == null) {
                writer.writeStartElement("null");
                writer.writeEndElement();
            } else if (param instanceof String) {
                writer.writeEntry("string", "" + param);
            } else if (param instanceof Integer) {
                writer.writeEntry("int", "" + param);
            } else if (param instanceof Long) {
                writer.writeEntry("long", "" + param);
            } else if (param instanceof Short) {
                writer.writeEntry("short", "" + param);
            } else if (param instanceof Byte) {
                writer.writeEntry("byte", "" + param);
            } else if (param instanceof Float) {
                writer.writeEntry("float", "" + param);
            } else if (param instanceof Double) {
                writer.writeEntry("double", "" + param);
            } else {
                writer.writeStartElement("obj");
                writer.writeText("" + param);
                writer.writeEndElement();
            }
        }
    }

    private static void writeBlock(XMLOutputStream writer, Block code) throws IOException {
        final boolean isNative = code instanceof NativeBlock;

        writer.writeStartElement("block");
        writer.writeAttribute("id", Long.toString(code.getID()));
        writer.writeAttribute("native", Boolean.toString(isNative));

        if (code.getParameterCount() > 0) {
            writeSequence(writer, code.getParameters());
        }

        writer.writeEndElement();
    }

    /**
     * Writes a script to an <b>OutputStream</b>. <br />
     * See {@link #createCode(org.jblocks.JBlocks, org.jblocks.editor.AbstrBlock)} 
     * and {@link #readFromXML(org.jblocks.JBlocks, java.io.InputStream) } <br />
     * <br />
     * The OutputStream won't be closed after this. <br />
     * 
     * @param out the OutputStream
     * @param script the script to write to the OutputStream
     * @throws IOException 
     */
    public static void writeToXML(OutputStream out, Block[] script) throws IOException {
        XMLOutputStream writer = new XMLOutputStream(out, true);
        writeToXML(writer, script);
        writer.flush();
    }

    /**
     * Writes a script to a <b>XMLOutputStream</b>. <br />
     * See {@link #createCode(org.jblocks.JBlocks, org.jblocks.editor.AbstrBlock)} 
     * and {@link #readFromXML(org.jblocks.JBlocks, java.io.InputStream) } <br />
     * <br />
     * The OutputStream won't be closed after this. <br />
     * 
     * @param writer the XMLOutputStream
     * @param script the script to write to the XMLOutputStream
     * @throws IOException 
     */
    public static void writeToXML(XMLOutputStream writer, Block[] script) throws IOException {
        writer.writeStartElement("script");
        writer.writeAttribute("version", "1");
        writeSequence(writer, script);
        writer.writeEndElement();
    }

    /*
     * ===========================================
     * ===========  Reading blocks ===============
     * ===========================================
     */
    private static Node getNodeForName(NodeList list, String name) {
        name = name.toLowerCase();
        int length = list.getLength();
        for (int i = 0; i < length; i++) {
            Node child = list.item(i);
            String childName = child.getNodeName().toLowerCase();
            if (name.equals(childName)) {
                return child;
            }
        }
        return null;
    }

    private static Block buildBlock(JBlocks ctx, Node n) {
        final long id = Long.valueOf(n.getAttributes().getNamedItem("id").getTextContent());
        final Node params = n;
        final BlockModel model = ctx.getInstalledBlocks().get(id);
        final Block instance = model.getCode().clone();

        if (params != null) {
            final Object[] seq = buildSequence(ctx, params);
            final Object[] dest = instance.getParameters();
            try {
                System.arraycopy(seq, 0, dest, 0, seq.length);
            } catch (Exception ex) {
                System.out.println(ex);
                System.out.println(Arrays.toString(seq) + ";  " + Arrays.toString(dest));
                throw new Error();
            }
        }

        return instance;
    }

    private static Object[] buildSequence(JBlocks ctx, Node n) {
        final NodeList nodes = n.getChildNodes();
        final int length = nodes.getLength();
        final List<Object> seq = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            final Node child = nodes.item(i);
            final String name = child.getNodeName().toLowerCase();
            if (name.equals("block")) {
                seq.add(buildBlock(ctx, child));
            } else if (name.equals("sequence")) {
                seq.add(buildSequence(ctx, child));
            } else if (name.equals("int")) {
                seq.add(Integer.valueOf(child.getTextContent()));
            } else if (name.equals("long")) {
                seq.add(Long.valueOf(child.getTextContent()));
            } else if (name.equals("byte")) {
                seq.add(Byte.valueOf(child.getTextContent()));
            } else if (name.equals("short")) {
                seq.add(Short.valueOf(child.getTextContent()));
            } else if (name.equals("float")) {
                seq.add(Float.valueOf(child.getTextContent()));
            } else if (name.equals("double")) {
                seq.add(Double.valueOf(child.getTextContent()));
            } else if (name.equals("string")) {
                seq.add(child.getTextContent());
            } else if (name.equals("null")) {
                seq.add(null);
            }
        }
        return seq.toArray();
    }

    private static Block[] toBlockSequence(Object[] o) {
        Block[] b = new Block[o.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (Block) o[i];
        }
        return b;
    }

    /**
     * Reads a script from an InputStream. <br />
     * The script can be converted to a "GUI script" with {@link #createScript(org.jblocks.JBlocks, org.jblocks.scriptengine.Block[])} <br />
     * 
     * @param ctx the context of JBlocks
     * @param in the InputStream from which to read the script
     * @return the script
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException 
     */
    public static Block[] readFromXML(JBlocks ctx, InputStream in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);

        Node scripts = doc.getFirstChild();

        return toBlockSequence(buildSequence(ctx, scripts));
    }

    private static void setValue(JBlocks ctx, Component c, Object val) {
        if (c instanceof AbstrInput) {
            if (val instanceof Block) {
                ((AbstrInput) c).setInput(buildAbstractBlock(ctx, (Block) val));
                ((AbstrInput) c).setBorderEnabled(false);
            } else {
                if (c instanceof JVariableInput) {
                    ((JVariableInput) c).setValue("" + val);
                } else {
                    Component inp = ((AbstrInput) c).getInput();
                    if (inp instanceof JTextField) {
                        ((JTextField) inp).setText("" + val);
                    }
                }
            }
        } else if (c instanceof JBlockSequence) {
            JBlockSequence seq = (JBlockSequence) c;
            if (val instanceof Object[]) {
                Block[] code = (Block[]) toBlockSequence((Object[]) val);
                if (code.length > 0) {
                    AbstrBlock[] blocks = new AbstrBlock[code.length];
                    for (int i = 0; i < blocks.length; i++) {
                        blocks[i] = buildAbstractBlock(ctx, code[i]);
                    }
                    AbstrBlock hat = createPuzzle(blocks);
                    seq.setStack(hat);
                }
            } else if (val instanceof Block) {
                seq.setStack(buildAbstractBlock(ctx, (Block) val));
            }
        }
    }

    private static AbstrBlock createPuzzle(AbstrBlock[] blocks) {
        AbstrBlock before = null;
        for (AbstrBlock b : blocks) {
            if (before != null) {
                for (PuzzleAdapter adp : ((Puzzle) before).getPuzzleAdapters()) {
                    if (adp.type == PuzzleAdapter.TYPE_DOWN) {
                        adp.neighbour = b;
                    }
                }
                for (PuzzleAdapter adp : ((Puzzle) b).getPuzzleAdapters()) {
                    if (adp.type == PuzzleAdapter.TYPE_TOP) {
                        adp.neighbour = before;
                    }
                }
            }
            before = b;
        }
        return blocks[0];
    }

    private static AbstrBlock buildAbstractBlock(JBlocks ctx, Block code) {
        final BlockModel model = ctx.getInstalledBlocks().get(code.getID());
        final AbstrBlock block = BlockFactory.createBlock(model);
        block.setBackground(ctx.getEditor().getCategory(model.getCategory()).getColor());
        int parameter = 0;
        for (Component c : block.getComponents()) {
            if (c instanceof AbstrInput || c instanceof JBlockSequence) {
                setValue(ctx, c, code.getParameter(parameter++));
            }
        }

        return block;
    }

    /**
     * Converts a script to "GUI blocks". <br />
     * The returned blocks will be a puzzle if the script is a 
     * sequence of command blocks. <br />
     * The opposite of this method is {@link #createCode(org.jblocks.JBlocks, org.jblocks.editor.AbstrBlock) }
     * 
     * @param ctx the context of JBlocks
     * @param code the script which should be converted
     * @return the converted blocks
     */
    public static AbstrBlock[] createScript(JBlocks ctx, Block[] code) {
        AbstrBlock[] block = new AbstrBlock[code.length];
        for (int i = 0; i < block.length; i++) {
            block[i] = buildAbstractBlock(ctx, code[i]);
        }
        if (block.length > 1) {
            return JBlockSequence.getPuzzlePieces((Puzzle) createPuzzle(block), PuzzleAdapter.TYPE_DOWN);
        } else {
            return block;
        }
    }

    /**
     * Converts "GUI blocks" to code. <br />
     * The opposite of this method is {@link #createScript(org.jblocks.JBlocks, org.jblocks.scriptengine.Block[]) }. <br />
     * 
     * @see ScriptGrabber
     * @param ctx the context of JBlocks
     * @param block the block (or the puzzle) which should be converted
     * @return the converted script
     */
    public static Block[] createCode(JBlocks ctx, AbstrBlock block) {
        if (block instanceof Puzzle) {
            return ScriptGrabber.getCodeFromScript(block);
        } else {
            return new Block[]{ScriptGrabber.getCodeFromBlock(block)};
        }
    }
}
