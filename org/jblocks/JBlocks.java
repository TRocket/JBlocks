package org.jblocks;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.jblocks.editor.AbstrBlock;
import org.jblocks.editor.BlockFactory;
import org.jblocks.editor.BlockModel;
import org.jblocks.editor.JBlockEditor;
import org.jblocks.editor.JBlockEditor.Category;
import org.jblocks.editor.JScriptPane;
import org.jblocks.editor.ScriptGrabber;
import org.jblocks.gui.JBlocksPane;
import org.jblocks.gui.JBubble;
import org.jblocks.gui.JDragPane;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.IScript;
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

    private static final int GREEN_FLAG_PRESSED_ID = 200 + 1;
    private final JBlocksPane gui;
    private final JDragPane drag;
    private final Map<Long, BlockModel> blockLib;
    private final Map<IScriptThread, AbstrBlock[]> blocksToReset;
    private IScriptEngine scriptEngine;

    /**
     * Creates a new JBlocks context with a GUI, a blockLib and a ScriptEngine. <br />
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public JBlocks() {
        gui = new JBlocksPane(this);
        drag = new JDragPane(gui);
        blockLib = new HashMap<Long, BlockModel>(200);
        blocksToReset = new HashMap<IScriptThread, AbstrBlock[]>(100);

        // create the standard ScriptEngine...
        scriptEngine = new org.jblocks.scriptengine.impl.DefaultScriptEngine();

        ScriptEngineListener defaultListener = new ScriptEngineListener() {

            @Override
            public void finished(IScriptThread t, Throwable error) {
                if (scriptEngine.getThreads().length == 0) {
                    JProgressBar prg = gui.getProgress();
                    prg.setIndeterminate(false);
                }
                AbstrBlock[] blocks = blocksToReset.get(t);
                if (error != null) {
                    JLabel errorLabel = new JLabel("Error!");
                    errorLabel.setForeground(Color.RED);
                    JBubble.showBubble(blocks[0], errorLabel);
                } else {
                    Object ret = t.getReturnValue();
                    if (ret != null) {
                        JLabel infoLabel = new JLabel("" + ret);
                        JBubble.showBubble(blocks[0], infoLabel);
                    }
                }
                if (blocks != null) {
                    for (AbstrBlock b : blocks) {
                        b.setHighlight(false);
                    }
                }
            }

            @Override
            public void started(IScriptThread t) {
                JProgressBar prg = gui.getProgress();
                if (!prg.isIndeterminate()) {
                    prg.setIndeterminate(true);
                }
            }
        };

        scriptEngine.addListener(defaultListener);
        initDefaultBlocks();
    }

    /**
     * Initialises the default blocks of the current ScriptEngine. <br />
     * (the blocks will be installed) <br />
     */
    private void initDefaultBlocks() {
        installBlock(BlockModel.createModel("hat", "Control", "When %{gf} clicked", new NativeBlock(0, GREEN_FLAG_PRESSED_ID) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return null;
            }
        }));
        installBlock(BlockModel.createModel("boolean", "Operators", "true", new NativeBlock(0, 200 + 2) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return true;
            }
        }));
        installBlock(BlockModel.createModel("boolean", "Operators", "false", new NativeBlock(0, 200 + 3) {

            @Override
            public Object evaluate(Object ctx, Object... param) {
                return false;
            }
        }));
        installBlock(BlockModel.createModel("cap", "Control", "return %{t}", scriptEngine.getDefaultBlock(Default.RETURN)));
        installBlock(BlockModel.createModel("command", "Control", "while %{b}%{br}%{s}", scriptEngine.getDefaultBlock(Default.WHILE)));
        installBlock(BlockModel.createModel("command", "Control", "if %{b}%{br}%{s}", scriptEngine.getDefaultBlock(Default.IF)));
        installBlock(BlockModel.createModel("command", "Control", "if %{b}%{br}%{s}%{br}else%{br}%{s}", scriptEngine.getDefaultBlock(Default.IF_ELSE)));
        installBlock(BlockModel.createModel("command", "Control", "repeat %{t}%{br}%{s}", scriptEngine.getDefaultBlock(Default.FOR)));

        installBlock(BlockModel.createModel("reporter", "Variables", "%{v}", scriptEngine.getDefaultBlock(Default.READ_GLOBAL_VARIABLE)));
        installBlock(BlockModel.createModel("command", "Variables", "set %{v} to %{t}", scriptEngine.getDefaultBlock(Default.WRITE_GLOBAL_VARIABLE)));
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
     * @see #getDesktop() 
     * @see org.jblocks.gui.JBlocksPane
     */
    public Container getContentPane() {
        return drag;
    }

    /**
     * Returns the desktop of JBlocks. <br />
     * You can use this to show a JInternalFrame. <br />
     * @return 
     */
    public JDesktopPane getDesktop() {
        return gui;
    }

    /**
     * Adds the specified block to JBlocks. <br />
     * The JBlockEditor will contain the block after this, too. <br />
     * <p />
     * If the block does already exists,
     * the old block will be uninstalled first. <br />
     * 
     * @throws IllegalArgumentException if the BlockModel hasn't an ID
     * @see #uninstallBlock(java.lang.String) 
     * @param model the block
     */
    public void installBlock(final BlockModel model) {
        long id = model.getID();
        if (id == BlockModel.NOT_AN_ID) {
            throw new IllegalArgumentException("the BlockModel hasn't an ID");
        }
        if (blockLib.containsKey(id)) {
            uninstallBlock(model);
        }
        blockLib.put(id, model);
        JBlockEditor editor = getEditor();
        editor.addBlock(BlockFactory.createBlock(model));
    }

    /**
     * Removes the specified block from JBlocks. <br />
     * The JBlockEditor won't contain the block anymore, too. <br />
     * 
     * @see #installBlock(java.lang.String, org.jblocks.scriptengine.Block) 
     * @param spec the spec of the block to uninstall
     */
    public void uninstallBlock(BlockModel model) {
        final long ID = model.getID();
        final JBlockEditor editor = getEditor();
        blockLib.remove(ID);
        editor.removeBlock(ID);
    }

    /**
     * Returns the JBlockEditor of the GUI. <br />
     * 
     * @see JBlocksPane#getEditor()
     */
    public JBlockEditor getEditor() {
        return gui.getEditor();
    }

    /**
     * Returns all installed blocks. <br />
     * The returned <code>Map</code> will be unmodifiable. <br />
     * 
     * @see #installBlock(java.lang.String, org.jblocks.scriptengine.Block) 
     * @see #uninstallBlock(java.lang.String) 
     */
    public Map<Long, BlockModel> getInstalledBlocks() {
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
            if (cont instanceof JDragPane) {
                return ((JBlocksPane) ((JDragPane) cont).getView()).getContext();
            }
            cont = cont.getParent();
        }
        return null;
    }

    /**
     * Stops all threads of the current ScriptEngine. <br />
     */
    public void stopScripts() {
        for (IScriptThread t : scriptEngine.getThreads()) {
            t.stop();
        }
    }

    /**
     * Starts all "green flag" hat blocks. <br />
     */
    public synchronized void startScripts() {
        // TODO: Run all JScriptPanes (=> JSpriteChooser)
        
        JScriptPane pane = gui.getEditor().getScriptPane();
        for (Component c : pane.getComponents()) {
            if (c instanceof AbstrBlock) {
                AbstrBlock block = (AbstrBlock) c;
                BlockModel model = block.getModel();
                if (model != null && model.getID() == GREEN_FLAG_PRESSED_ID) {
                    block.tryToExecute();
                }
            }
        }
    }

    /**
     * This will reset the highlight of the specified block <br />
     * when the script is finished. <br />
     * 
     * @param thread the thread.
     */
    public void addHighlight(IScriptThread thread, AbstrBlock[] blocks) {
        blocksToReset.put(thread, blocks);
    }

    /**
     * Returns <code>true</code> if the specified ID is a default block. <br />
     */
    public boolean isDefaultBlock(long ID) {
        if (ID >= 100 && ID < 300) {
            return true;
        }

        return false;
    }

    /**
     * Returns the <code>Color</code> of a category. <br />
     * If the category doesn't exists, {@link AbstrBlock#DEFAULT_COLOR} is returned. <br />
     * 
     * @param category the name of the category
     * @return the color of the category
     */
    public Color getCategoryColor(String category) {
        Category catg = getEditor().getCategory(category);
        if (catg != null) {
            return catg.getColor();
        }
        return AbstrBlock.DEFAULT_COLOR;
    }
}
