/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.scriptengine.impl;

import java.io.InputStream;
import java.util.Queue;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScript;
import org.jblocks.scriptengine.IScriptEngine;
import org.jblocks.scriptengine.IScriptThread;

/**
 *
 * @author ZeroLuck
 */
public class DefaultScriptEngine implements IScriptEngine, Runnable {
    
    private final Queue<DefaultScriptThread> threads;
    private Thread scriptThread;
    
    public DefaultScriptEngine() {
        threads = new java.util.LinkedList<DefaultScriptThread>();
    }
    
    @Override
    public Block load(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public IScript compile(Block[] blocks) {
        return new DefaultScript(blocks);
    }
    
    @Override
    public IScriptThread execute(IScript s) {
        DefaultScriptThread thrd = new DefaultScriptThread(((DefaultScript) s).getCommands());
        threads.add(thrd);
        if (scriptThread == null || !scriptThread.isAlive()) {
            scriptThread = new Thread(this);
            scriptThread.start();
        }
        
        return thrd;
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
                if (!thread.step()) {
                    threads.add(thread);
                }
            }
        }
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
