package org.jblocks.scriptengine.impl;

import java.util.Map;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScriptThread;

/**
 *
 * @author ZeroLuck
 */
public class DefaultScriptThread implements IScriptThread {

    private boolean stopRequest = false;
    private StackElement stack;
    private final Map<String, Object> globalVariables;

    public DefaultScriptThread(Map<String, Object> globalVariables, Block[] commands) {
        this.globalVariables = globalVariables;
        this.stack = new StackElement(null, new ByobBlock(0, commands), commands, false, globalVariables);
    }

    public boolean step() {
        if (stopRequest || stack == null) {
            return true;
        }
        if (stack.off >= stack.commands.length) {
            if (stack.perform instanceof ByobBlock) {   // BYOB
                ByobBlock byob = (ByobBlock) stack.perform;
                Block[] seq = byob.getSequence();
                if (seq == stack.commands) {    // finish
                    stack = stack.parent;
                } else {
                    StackElement child = new StackElement(stack.parent, byob, byob.getSequence(), false, globalVariables);
                    System.arraycopy(stack.param, 0, child.param, 0, stack.param.length);
                    stack = child;
                }
            } else if (stack.perform instanceof NativeBlock) {    // NATIVE
                NativeBlock nat = (NativeBlock) stack.perform;
                StackElement cp = stack.parent;
                Object val = nat.evaluate(stack, stack.param);

                stack = stack.parent;

                if (stack == cp) {
                    if (stack.doParam) {
                        stack.param[stack.off - 1] = val;
                    }
                }
            } else {
                throw new java.lang.UnsupportedOperationException("block '" + stack.perform + "' isn't supported");
            }
        } else {
            Object val = stack.commands[stack.off];
            stack.off++;
            if (val instanceof Block) {
                Block cmd = (Block) val;
                StackElement child = new StackElement(stack, cmd, cmd.getParameters(), true, globalVariables);
                stack = child;
            } else {
                if (stack.doParam) {
                    stack.param[stack.off - 1] = val;
                }
            }
        }
        return false;
    }

    @Override
    public void stop() {
        stopRequest = true;
    }

    @Override
    public boolean isAlive() {
        return !stopRequest;
    }

    public static class StackElement {

        int off = 0;
        final Block perform;
        StackElement parent;
        final Object[] commands;
        final Object[] param;
        final Map<String, Object> global;
        final boolean doParam;

        public StackElement(StackElement parent, Block perform, Object[] commands, boolean doParam, Map<String, Object> global) {
            this.perform = perform;
            this.parent = parent;
            this.commands = commands;
            this.param = new Object[perform.getParameterCount()];
            this.doParam = doParam;
            this.global = global;
        }
    }
}
