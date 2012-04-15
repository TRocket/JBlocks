package org.jblocks.scriptengine.impl;

import org.jblocks.scriptengine.Block;
import org.jblocks.scriptengine.impl.DefaultScriptThread.StackElement;

/**
 * A NativeBlock is written in Java. <br />
 * 
 * @author ZeroLuck
 */
class NativeBlock extends Block {

    public NativeBlock(int paramCount) {
        super(paramCount);
    }

    /**
     * This has to be executed <b>fast</b>. <br />
     * This code can block the Thread-Scheduler. <br />
     */
    public Object evaluate(StackElement o, Object... params) {
        /* 
         * just a test.
         * this will be replaced later. 
         */
        System.out.println("Hello");

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block clone() {
        int len = getParameterCount();
        NativeBlock n = new NativeBlock(len);
        for (int i = 0; i < len; i++) {
            Object o = getParameter(i);
            if (o instanceof Block) {
                n.setParameter(i, ((Block) o).clone());
            } else {
                n.setParameter(i, o);
            }
        }
        return n;
    }
}
