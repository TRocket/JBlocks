package org.jblocks.scriptengine.impl;

import java.util.Arrays;
import java.util.Map;

import org.jblocks.editor.BlockModel;
import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.ByobBlock;
import org.jblocks.scriptengine.IScriptThread;
import org.jblocks.scriptengine.NativeBlock;

/**
 *
 * @author ZeroLuck
 */
public class DefaultScriptThread implements IScriptThread {

    private volatile boolean stopRequest = false;
    private StackElement stack;
    private final Map<String, Object> globalVariables;
    private final StackElement rootStack;

    public DefaultScriptThread(Map<String, Object> globalVariables, Block[] commands) {
        this.globalVariables = globalVariables;
        this.stack = new StackElement(null, new ByobBlock(commands.length, BlockModel.NOT_AN_ID, commands), commands, true, globalVariables);
        this.rootStack = stack;
    }

    public StackElement getStack() {
        return stack;
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

    @Override
    public Object getReturnValue() {
        if (rootStack.param.length == 0) {
            return null;
        }
        return rootStack.param[0];
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
