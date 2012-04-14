package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.IScriptThread;

/**
 *
 * @author ZeroLuck
 */
class DefaultScriptThread implements IScriptThread {

    private boolean stopRequest;
    private Offset stack;

    public DefaultScriptThread(Block[] commands) {
        stopRequest = false;
        ByobBlock script = new ByobBlock(0, commands);
        stack = new Offset(null, script, script.getSequence());
    }

    @Override
    public void stop() {
        stopRequest = true;
    }

    public boolean step() {
        if (stopRequest || stack == null) {
            return true;
        }
        if (stack.off >= stack.commands.length) {
            if (stack.perform instanceof NativeBlock) {
                NativeBlock nat = (NativeBlock) stack.perform;
                stack.parent.commands[stack.parent.off - 1] = nat.evaluate(stack.commands);
                stack = stack.parent;
            } else if (stack.perform instanceof ByobBlock) {
                ByobBlock byob = (ByobBlock) stack.perform;
                Block[] seq = byob.getSequence();
                if (seq == stack.commands) {
                    stack = stack.parent;
                } else {
                    Offset child = new Offset(stack.parent, byob, byob.getSequence());
                    stack = child;
                }
            } else {
                throw new RuntimeException("the block isn't supported!");
            }
        } else {
            Object obj = stack.commands[stack.off];
            stack.off++;
            
            if (obj instanceof ByobBlock) {
                Offset child = new Offset(stack, (ByobBlock) obj, ((ByobBlock) obj).getParameters());
                stack = child;
            } else if (obj instanceof NativeBlock) {
                Offset child = new Offset(stack, (NativeBlock) obj, ((NativeBlock) obj).getParameters());
                stack = child;
            }
        }
        return false;
    }

    private static class Offset {

        int off;
        final Offset parent;
        final Object[] commands;
        final Block perform;

        public Offset(Offset parent, Block perform, Object[] commands) {
            this.off = 0;
            this.parent = parent;
            this.commands = commands;
            this.perform = perform;
        }
    }
}
