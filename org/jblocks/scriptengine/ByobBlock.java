package org.jblocks.scriptengine;

/**
 *
 * @author ZeroLuck
 */
public class ByobBlock extends Block {

    private final Block[] seq;

    public ByobBlock(int paramCount, long id, Block[] sequence) {
        super(paramCount, id);
        this.seq = new Block[sequence.length];
        System.arraycopy(sequence, 0, seq, 0, sequence.length);
    }

    public Block[] getSequence() {
        return seq;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Block clone() {
        int len = getParameterCount();
        ByobBlock n = new ByobBlock(len, getID(), seq);
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
    
    @Override
    public String toString() {
        return "ByobBlock[" + super.getID() + "]";
    }
}
