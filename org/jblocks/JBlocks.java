package org.jblocks;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JProgressBar;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.JDragPane;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.IScriptEngine.ScriptEngineListener;
import org.jblocks.scriptengine.IScriptThread;
import org.jblocks.scriptengine.NativeBlock;

/**
 * The context of JBlocks: <br />
 * 
 * <p />
 * This class contains a cache for the images of JBlock. <br />
 * The ScriptEngine and the blocks are handled by this class to. <br />
 * JBlocksPane is the GUI of JBlocks. <br />
 * 
 * <p />
 * In future this class may managing multiple languages too. <br />
 * You should create just <i>one</i> instance of this class. <br />
 * 
 * @see org.jblocks.gui.JBlocksPane
 * @author ZeroLuck
 */
public final class JBlocks {

    private final JBlocksPane gui;
    private final JDragPane drag;
    private final Map<String, Block> blockLib;
    private IScriptEngine scriptEngine;

    /**
     * Creates a new JBlocks context with a GUI, a blockLib and a ScriptEngine. <br />
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public JBlocks() {
        gui = new JBlocksPane(this);
        drag = new JDragPane(gui);
        blockLib = new HashMap<String, Block>(200);

        // create the standard ScriptEngine...
        scriptEngine = new org.jblocks.scriptengine.impl.DefaultScriptEngine();

        scriptEngine.addListener(new ScriptEngineListener() {

            @Override
            public void finished(IScriptThread t, Throwable error) {
                if (scriptEngine.getThreads().length == 0) {
                    JProgressBar prg = gui.getProgress();
                    prg.setIndeterminate(false);
                }
            }

            @Override
            public void started(IScriptThread t) {
                JProgressBar prg = gui.getProgress();
                if (!prg.isIndeterminate()) {
                    prg.setIndeterminate(true);
                }
            }
        });

        initDefaultBlocks();
    }

    /**
     * Initialises the default blocks of the current ScriptEngine. <br />
     * (the blocks will be installed) <br />
     */
    private void initDefaultBlocks() {
        // this may be changed in future:
        // the block-spec shouldn't be the key for the blocks...

        blockLib.put("return %{r}", scriptEngine.getDefaultBlock(Default.RETURN));
        blockLib.put("while %{b}%{br}%{s}", scriptEngine.getDefaultBlock(Default.WHILE));
        blockLib.put("if %{b}%{br}%{s}", scriptEngine.getDefaultBlock(Default.IF));
        blockLib.put("if %{b}%{br}%{s}%{br}else%{s}", scriptEngine.getDefaultBlock(Default.IF_ELSE));
        blockLib.put("repeat %{r}%{br}%{s}", scriptEngine.getDefaultBlock(Default.FOR));
        blockLib.put("true", new NativeBlock(0) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return true;
            }
        });
        blockLib.put("false", new NativeBlock(0) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return false;
            }
        });
    }

    /**
     * Returns the ScriptEngine of JBlocks. <br />
     * The ScriptEngine can change! <br />
     */
    public IScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    /**
     * Returns the GUI of JBlocks. <br />
     * The returned container can be displayed in a JFrame/JApplet etc... <br />
     * You don't have to use a {@link org.jblocks.gui.JDragPane} as a parent for this.
     * 
     * @see org.jblocks.gui.JBlocksPane
     */
    public Container getContentPane() {
        return drag;
    }

    /**
     * Adds the specified block to JBlocks. <br />
     * The JBlockEditor will contain the block after this. <br />
     * <p />
     * If the block does already exists (or the block-spec)
     * the old block will be uninstalled. <br />
     * 
     * @see #uninstallBlock(java.lang.String) 
     * @param spec the spec of the block to install
     * @param b the block
     */
    public void installBlock(String spec, Block b) {
        if (blockLib.containsKey(spec)) {
            uninstallBlock(spec);
        }
        blockLib.put(spec, b);
    }

    /**
     * Removes the specified block from JBlocks. <br />
     * The JBlockEditor won't contain the block anymore, too. <br />
     * 
     * @see #installBlock(java.lang.String, org.jblocks.scriptengine.Block) 
     * @param spec the spec of the block to uninstall
     */
    public void uninstallBlock(String spec) {
        blockLib.remove(spec);
    }

    /**
     * Returns all installed blocks. <br />
     * The returned Map<String, Block> will be unmodifiable. <br />
     * 
     * @see #installBlock(java.lang.String, org.jblocks.scriptengine.Block) 
     * @see #uninstallBlock(java.lang.String) 
     */
    public Map<String, Block> getInstalledBlocks() {
        return Collections.unmodifiableMap(blockLib);
    }
    /*
     * (static) image cache
     */
    private static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>(100);

    /**
     * Loads the image with the specified name. <br />
     * The name is relative to <i>org/jblocks/res/</i>. <br />
     * If the image is already in a cache, the instance from 
     * the cache is used.
     * <p />
     * 
     * The image is loaded with {@link javax.imageio.ImageIO}.
     * <p />
     * 
     * @throws RuntimeException if an IO-Error occurs or the image doesn't exists
     * @see #getIcon(java.lang.String) 
     * @param name the name of the image.
     * @return the image
     */
    public static BufferedImage getImage(String name) {
        if (!images.containsKey(name)) {
            try {
                InputStream in = JBlocks.class.getResourceAsStream("res/" + name);
                if (in == null) {
                    throw new RuntimeException("Couldn't find the image with the name '" + name + "'");
                }
                images.put(name, ImageIO.read(in));
            } catch (IOException io) {
                throw new RuntimeException("Couldn't load the image with the name '" + name + "'", io);
            }
        }
        return images.get(name);
    }

    /**
     * Loads the specified image. <br />
     * After this, the image is wrapped to an ImageIcon. <br />
     * This method uses the {@link #getImage(java.lang.String)} method.
     * <p />
     * 
     * This method is the same like: <br />
     * <code>
     * new ImageIcon(getImage(name));
     * </code>
     * 
     * @see #getImage(java.lang.String) 
     * @param name the name of the image
     * @return the image
     */
    public static ImageIcon getIcon(String name) {
        return new ImageIcon(getImage(name));
    }

    /**
     * Returns the context for this component. <br />
     * (This method tries to find a JBlocksPane root) <br />
     * 
     * @see org.jblocks.gui.JBlocksPane#getContext() 
     * @param c the component
     * @return the context for this component, or null
     */
    public static JBlocks getContextForComponent(Component c) {
        Container cont = c.getParent();
        while (cont != null) {
            if (cont instanceof JBlocksPane) {
                return ((JBlocksPane) cont).getContext();
            }
            cont = cont.getParent();
        }
        return null;
    }
}
