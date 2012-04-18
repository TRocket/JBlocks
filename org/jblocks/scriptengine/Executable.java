package org.jblocks.scriptengine;

/**
 *
 * @author ZeroLuck
 */
interface Executable {

    public Object evaluate(Object ctx, Object... param);
}
