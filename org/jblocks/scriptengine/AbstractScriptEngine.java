package org.jblocks.scriptengine;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author ZeroLuck
 */
public abstract class AbstractScriptEngine {

    /**
     * Runs the specified Block in a separate block.
     * @throws IllegalArgumentException if the parameter 'b' is null.
     */
    public void execute(Script scr) {
        if (scr == null) {
            throw new IllegalArgumentException("the parameter 'scr' is null!");
        }
        executeImpl(scr);
    }

    protected abstract void executeImpl(Script scr);

    /**
     * Compiles a set of blocks to a Script. <br />
     * @throws IllegalArgumentException if the parameter 'scr' is null.
     */
    public Script compile(Block[] blocks) {
        if (blocks == null) {
            throw new IllegalArgumentException("the parameter 'blocks' is null!");
        }
        return compileImpl(blocks);
    }

    protected abstract Script compileImpl(Block[] blocks);

    /**
     * Loads a Block from an InputStream. <br />
     * 
     * @throws IOException if an IO error occurs.
     * @param in the InputStream from which to load the block.
     * @return the loaded Block, or null if an error occurs.
     */
    public Block load(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("the parameter 'in' is null!");
        }
        return loadImpl(in);
    }

    public abstract Block loadImpl(InputStream in)
            throws IOException;

    /**
     * Returns all running scripts. <br />
     */
    public abstract Script[] getRunningScripts();

    /**
     * Stops all Scripts of this AbstractScriptEngine. <br />
     */
    public abstract void stopAllScripts();

    public static abstract class Block {

        public final AbstractScriptEngine engine;

        /**
         * 
         * @throws IllegalArgumentException if the parameter 'engine' is null.
         * @param engine the AbstractScriptEngine which created this Script.
         */
        public Block(AbstractScriptEngine engine) {
            if (engine == null) {
                throw new IllegalArgumentException("the parameter 'engine' is null!");
            }
            this.engine = engine;
        }
    }

    /**
     * A Script is a set of Blocks compiled with an instance of an AbstractScriptEngine. <br />
     * It is executable. <br />
     */
    public static abstract class Script {

        public final AbstractScriptEngine engine;

        /**
         * 
         * @throws IllegalArgumentException if the parameter 'engine' is null.
         * @param engine the AbstractScriptEngine which created this Script.
         */
        public Script(AbstractScriptEngine engine) {
            if (engine == null) {
                throw new IllegalArgumentException("the parameter 'engine' is null!");
            }
            this.engine = engine;
        }
    }
}
