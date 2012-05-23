package org.jblocks.scriptengine;

import org.jblocks.editor.BlockModel;

/**
 *
 * @author ZeroLuck
 */
public abstract class Block {

    private final Object[] parameters;
    private final long ID;

    public Block(int parameterCount, long id) {
        this.parameters = new Object[parameterCount];
        this.ID = id;
    }

    /**
     * Returns the ID of this Block. <br />
     */
    public long getID() {
        return ID;
    }

    /**
     * Sets the value of a parameter. <br />
     * 
     * @param index the index of the parameter
     * @param param the new value of the parameter[i]
     */
    public void setParameter(int index, Object param) {
        parameters[index] = param;
    }

    /**
     * Returns the parameter <i>index</i> of this Block. <br />
     * 
     * @param index the index of the parameter
     */
    public Object getParameter(int index) {
        return parameters[index];
    }

    /**
     * Returns the count of parameters this block has. <br />
     */
    public int getParameterCount() {
        return parameters.length;
    }

    /**
     * Returns all the parameters of this block. <br />
     */
    public Object[] getParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Block clone();

    /**
     * Two blocks are equal if, and only if, the IDs are the same and the IDs aren't {@link BlockModel#NOT_AN_ID}.
     * <hr />
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Block) {
            Block b = (Block) o;
            if (b.ID == ID && ID != BlockModel.NOT_AN_ID) {
                return true;
            }
        }
        return super.equals(o);
    }

    protected static void copyParameters(Block src, Block dest) {
        int len = src.getParameterCount();
        for (int i = 0; i < len; i++) {
            Object o = src.getParameter(i);
            if (o instanceof Block) {
                dest.setParameter(i, ((Block) o).clone());
            } else {
                dest.setParameter(i, o);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (this.ID ^ (this.ID >>> 32));
        return hash;
    }

    public static enum Default {

        FOR, RETURN, WHILE, IF, IF_ELSE, READ_GLOBAL_VARIABLE, READ_PARAM_VARIABLE,
        WRITE_GLOBAL_VARIABLE, WRITE_PARAM_VARIABLE, TRUE, FALSE, ADD, SUB, MUL, DIV, MOD, SMALLER,
        BIGGER, EQUALS, OR, AND, NOT, RUN, THE_SCRIPT
    }
}
