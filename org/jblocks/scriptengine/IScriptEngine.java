package org.jblocks.scriptengine;

import java.util.Map;

/**
 *
 * @author ZeroLuck
 */
public interface IScriptEngine {

    public IScript compile(Block[] blocks);

    public IScriptThread execute(IScript s);

    public IScriptThread[] getThreads();

    public Map<String, Object> getGlobalVariables();

    public Block getDefaultBlock(Block.Default def);
    
    public void addListener(ScriptEngineListener listener);
    
    public void removeListener(ScriptEngineListener listener);

    public static interface ScriptEngineListener {

        public void finished(IScriptThread t, Throwable error);
        
        public void started(IScriptThread t);
    };
}
