package org.jblocks.scriptengine.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private final Map<String, Object> globalVariables;
    private Thread scriptThread;
    private final List<ScriptEngineListener> listeners;

    public DefaultScriptEngine() {
        threads = new RepeatingQueue<DefaultScriptThread>();
        globalVariables = new HashMap<String, Object>(100);
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
        DefaultScriptThread thrd = new DefaultScriptThread(globalVariables, ((DefaultScript) s).getCommands());
        synchronized (threads) {
            threads.add(thrd);
        }
        startThreadIfNecessary();
        fireStartedEvent(thrd);
        return thrd;
    }

    @Override
    public IScriptThread[] getThreads() {
        synchronized (threads) {
            return threads.toArray(new IScriptThread[]{});
        }
    }

    private void printStackTrace(StackElement elm, PrintStream str) {
        str.println("\t--- stack ---");
        int cnt = 0;
        while (elm != null) {
            str.println("\tat StackElement [doParam: " + elm.doParam + ", off: " + elm.off + " block: " + elm.perform + "]"
                    + ", param: " + Arrays.toString(elm.param));

            elm = elm.parent;

            if (cnt++ > 40) {
                str.println("\t...");
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (threads) {
                DefaultScriptThread thread = threads.peek();
                // no thread to execute?
                if (thread == null) {
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    try {
                        // thread.step() returns true if the thread is finished
                        if (thread.step()) {
                            // the thread is finished: stop the thread
                            threads.remove();
                            fireFinishedEvent(thread, null);
                            break;
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                        printStackTrace(thread.getStack(), System.err);

                        threads.remove();
                        fireFinishedEvent(thread, t);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    @Override
    public Block getDefaultBlock(Default def) {
        return DefaultBlocks.getDefaultBlock(def);
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
