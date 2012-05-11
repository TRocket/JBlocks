package org.jblocks.scriptengine;

/**
 *
 * @author ZeroLuck
 */
public interface IScriptThread {

    public void stop();

    public boolean isAlive();
    
    public Object getReturnValue();
}
