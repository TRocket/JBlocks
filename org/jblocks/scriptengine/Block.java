package org.jblocks.scriptengine;

/**
 *
 * @author ZeroLuck
 */
public abstract class Block {

    private Object[] parameters;

    public Block(int parameterCount) {
        parameters = new Object[parameterCount];
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
     * Returns the count of parameters this block has.
     */
    public int getParameterCount() {
        return parameters.length;
    }
    
    protected Object[] getParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Block clone();
}
