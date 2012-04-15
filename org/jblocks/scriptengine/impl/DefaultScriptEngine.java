/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.scriptengine.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.Queue;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScript;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.IScriptThread;

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

    private final Queue<DefaultScriptThread> threads;
    private final Map globalVariables;
    private Thread scriptThread;

    public DefaultScriptEngine() {
        threads = new java.util.LinkedList<DefaultScriptThread>();
        globalVariables = new java.util.HashMap(1000);
    }

    @Override
    public Block load(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IScript compile(Block[] blocks) {
        return new DefaultScript(blocks);
    }

    private void startThreadIfNecessary() {
        if (scriptThread == null || !scriptThread.isAlive()) {
            scriptThread = new Thread(this, "DefaultScriptEngine");
            scriptThread.setPriority(8);
            scriptThread.start();
        }
    }

    @Override
    public IScriptThread execute(IScript s) {
        synchronized (threads) {
            DefaultScriptThread thrd = new DefaultScriptThread(globalVariables, ((DefaultScript) s).getCommands());
            threads.add(thrd);
            startThreadIfNecessary();
            return thrd;
        }
    }

    @Override
    public IScriptThread[] getThreads() {
        synchronized (threads) {
            return threads.toArray(new IScriptThread[]{});
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (threads) {
                DefaultScriptThread thread = threads.poll();
                if (thread == null) {
                    return;
                }
                boolean add = true;
                for (int i = 0; i < 10; i++) {
                    if (thread.step()) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    threads.add(thread);
                }
            }
        }
    }

    @Override
    public Map getGlobalVariables() {
        return globalVariables;
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
