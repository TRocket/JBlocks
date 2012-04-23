/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.scriptengine.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.Block.Default;
import org.jblocks.scriptengine.IScript;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.IScriptThread;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 *
 * <p>
 * The DefaultScriptEngine is good for projects in which many scripts are running 
 * at the same time because it doesn't use heavy weight {@link java.lang.Thread}s. <br />
 * </p>
 * <p>
 * This is an emulated Thread-Scheduler which is much slower than real ones. <br />
 * The Thread-Scheduler thread runs just when one or more scripts are running. 
 * </p>
 * 
 * @author ZeroLuck
 */
public class DefaultScriptEngine implements IScriptEngine, Runnable {

    private final RepeatingQueue<DefaultScriptThread> threads;
    private final Map globalVariables;
    private Thread scriptThread;
    private final List<ScriptEngineListener> listeners;

    public DefaultScriptEngine() {
        threads = new RepeatingQueue();
        globalVariables = new java.util.HashMap(100);
        listeners = new ArrayList<ScriptEngineListener>(1);
    }
    
    @Override
    public IScript compile(Block[] blocks) {
        return new DefaultScript(blocks);
    }

    private void startThreadIfNecessary() {
        if (scriptThread == null || !scriptThread.isAlive()) {
            scriptThread = new Thread(this, "DefaultScriptEngine");
            scriptThread.setPriority(1); // <- TEST
            scriptThread.start();
        }
    }

    @Override
    public IScriptThread execute(IScript s) {
        synchronized (threads) {
            DefaultScriptThread thrd = new DefaultScriptThread(globalVariables, ((DefaultScript) s).getCommands());
            threads.add(thrd);
            startThreadIfNecessary();
            fireStartedEvent(thrd);
            return thrd;
        }
    }

    @Override
    public IScriptThread[] getThreads() {
        synchronized (threads) {
            return threads.toArray(new IScriptThread[]{});
        }
    }

    private void printStackTrace(StackElement elm) {
        System.err.println("\t--- stack ---");
        while (elm != null) {
            System.err.println("\tat StackElement [doParam: " + elm.doParam + ", off: " + elm.off + " block: " + elm.perform + "]"
                    + ", param: " + Arrays.toString(elm.param));

            elm = elm.parent;
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (threads) {
                DefaultScriptThread thread = threads.peek();
                if (thread == null) {
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    try {
                        if (thread.step()) {
                            threads.remove();
                            fireFinishedEvent(thread, null);
                            break;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                        printStackTrace(thread.getStack());
                        threads.remove();
                        fireFinishedEvent(thread, t);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Map getGlobalVariables() {
        return globalVariables;
    }

    @Override
    public Block getDefaultBlock(Default def) {
        switch (def) {
            case FOR:
                return DefaultBlocks.FOR;
            case WHILE:
                return DefaultBlocks.WHILE;
            case RETURN:
                return DefaultBlocks.RETURN;
            case READ_GLOBAL_VARIABLE:
                return DefaultBlocks.READ_GLOBAL_VARIABLE;
            case READ_PARAM_VARIABLE:
                return DefaultBlocks.READ_PARAM_VARIABLE;
            case WRITE_GLOBAL_VARIABLE:
                return DefaultBlocks.WRITE_GLOBAL_VARIABLE;
            case WRITE_PARAM_VARIABLE:
                return DefaultBlocks.WRITE_PARAM_VARIABLE;
            case IF:
                return DefaultBlocks.IF;
            case IF_ELSE:
                return DefaultBlocks.IF_ELSE;
            default:
                return null;
        }
    }

    private void fireFinishedEvent(IScriptThread t, Throwable error) {
        int len = listeners.size();
        for (int i = 0; i < len; i++) {
            listeners.get(i).finished(t, error);
        }
    }

    private void fireStartedEvent(IScriptThread t) {
        int len = listeners.size();
        for (int i = 0; i < len; i++) {
            listeners.get(i).started(t);
        }
    }

    @Override
    public void addListener(ScriptEngineListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ScriptEngineListener listener) {
        listeners.remove(listener);
    }

    private static class DefaultScript implements IScript {

        private Block[] blocks;

        public DefaultScript(Block[] blocks) {
            this.blocks = blocks;
        }

        public Block[] getCommands() {
            return blocks;
        }
    }
}
