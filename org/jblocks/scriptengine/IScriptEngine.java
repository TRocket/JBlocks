package org.jblocks.scriptengine;

import java.io.InputStream;

/**
 *
 * @author ZeroLuck
 */
public interface IScriptEngine {

    public Block load(InputStream in);

    public IScript compile(Block[] blocks);

    public IScriptThread execute(IScript s);

    public IScriptThread[] getThreads();
}
