package org.jblocks.scriptengine;

import java.io.InputStream;
import java.util.Map;

/**
 *
 * @author ZeroLuck
 */
public interface IScriptEngine {

    public Block load(InputStream in);

    public IScript compile(Block[] blocks);

    public IScriptThread execute(IScript s);

    public IScriptThread[] getThreads();

    public Map getGlobalVariables();

    public Block getDefaultBlock(String name);
}
