package org.jblocks.sound;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;

/**
 *
 * @author ZeroLuck
 */
public abstract class SoundFilter extends SoundInput {

    protected final SoundInput parent;

    public SoundFilter(SoundInput p) {
        parent = p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean eof() {
        return parent.eof();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioFormat getFormat() {
        return parent.getFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        parent.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int read(byte[] data, int off, int len)
            throws IOException;
}
