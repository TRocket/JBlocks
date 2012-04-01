package org.jblocks.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sound.sampled.AudioFormat;

/**
 *
 * This sound-mixer creates from many SoundInputs one. <br />
 * 
 * @author ZeroLuck
 */
public class SoftwareMixer extends SoundInput {

    // <member>
    private final List<SoundInput> inputs = new ArrayList<SoundInput>();
    private final AudioFormat fmt;

    /**
     * Creates a new SoftwareMixer. <br />
     * The SoftwareMixer mixs many SoundInputs to one. <br />
     * 
     * @throws NullPointerException - if 'fmt' is null.
     * @param fmt - the AudioFormat of this mixer.
     */
    public SoftwareMixer(AudioFormat fmt) {
        if (fmt.getChannels() != 1) {
            throw new IllegalArgumentException("the AudioFormat isn't mono!");
        }
        this.fmt = fmt;
    }

    /**
     * Adds a SoundInput to the mixer. <br />
     * 
     * @throws IllegalArgumentException - if the AudioFormat of 'in' doesn't 
     *     matches to the mixer's one.
     * @throws NullPointerException - if the AudioFormat of 'in' is null.
     * @param in - the sound input to mix. 
     */
    public void addInput(SoundInput in) {
        if (!in.getFormat().matches(fmt)) {
            throw new IllegalArgumentException("the AudioFormat doesn't matches the mixer's one.");
        }
        synchronized (inputs) {
            inputs.add(in);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public AudioFormat getFormat() {
        return fmt;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean eof() {
        return inputs.isEmpty();
    }

    private byte clip8(int i) {
        if (i > Byte.MAX_VALUE) {
            return Byte.MAX_VALUE;
        }
        if (i < Byte.MIN_VALUE) {
            return Byte.MIN_VALUE;
        }
        return (byte) i;
    }

    private short clip16(int i) {
        if (i > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if (i < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (short) i;
    }

    private int clip32(long i) {
        if (i > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (i < Integer.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (int) i;
    }
    
    private int readFully(SoundInput in, byte[] data, int off, int len) throws IOException {
        int cnt = 0;
        while(cnt < len) {
            int x = in.read(data, off + cnt, len - cnt);
            cnt += x;
            if (cnt == -1) {
                break;
            }
        }
        return cnt;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        if (eof()) {
            return -1;
        }

        final int SAMPLE_SIZE = fmt.getFrameSize();
        final ByteBuffer outBuf = ByteBuffer.wrap(data, off, len);


        if (fmt.isBigEndian()) {
            outBuf.order(ByteOrder.BIG_ENDIAN);
        } else {
            outBuf.order(ByteOrder.LITTLE_ENDIAN);
        }

        if (len % SAMPLE_SIZE != 0) {
            throw new IllegalArgumentException("len % FRAME_SIZE != 0");
        }

        for (int i = 0; i < len; i++) {
            data[i] = 0;
        }

        synchronized (inputs) {
            Collection<SoundInput> remove = new ArrayList<SoundInput>();
            ByteBuffer inBuf = ByteBuffer.allocate(len);
            if (fmt.isBigEndian()) {
                inBuf.order(ByteOrder.BIG_ENDIAN);
            } else {
                inBuf.order(ByteOrder.LITTLE_ENDIAN);
            }
            int inputsSize = inputs.size();
            int i;

            switch (SAMPLE_SIZE) {
                case 1:
                    for (int j = 0; j < inputsSize; j++) {
                        SoundInput inp = inputs.get(j);
                        if (inp.eof()) {
                            remove.add(inp);
                        } else {
                            int cnt = readFully(inp, inBuf.array(), 0, len);
                            for (i = 0; i < cnt; i += SAMPLE_SIZE) {
                                int s = outBuf.get(i);
                                s += inBuf.get(i);
                                outBuf.put(i, clip8(s));
                            }
                        }
                    }
                    break;
                case 2:
                    for (int j = 0; j < inputsSize; j++) {
                        SoundInput inp = inputs.get(j);
                        if (inp.eof()) {
                            remove.add(inp);
                        } else {
                            int cnt = readFully(inp, inBuf.array(), 0, len);
                            for (i = 0; i < cnt; i += SAMPLE_SIZE) {
                                int s = outBuf.getShort(i);
                                s += inBuf.getShort(i);
                                outBuf.putShort(i, clip16(s));
                            }
                        }
                    }
                    break;
                case 4:
                    for (int j = 0; j < inputsSize; j++) {
                        SoundInput inp = inputs.get(j);
                        if (inp.eof()) {
                            remove.add(inp);
                        } else {
                            int cnt = readFully(inp, inBuf.array(), 0, len);
                            for (i = 0; i < cnt; i += SAMPLE_SIZE) {
                                long s = outBuf.getInt(i);
                                s += inBuf.getInt(i);
                                outBuf.putInt(i, clip32(s));
                            }
                        }
                    }
                    break;

                default:
                    throw new UnsupportedOperationException("unsupported sample-size!");
            }
            inputs.removeAll(remove);
            return len;
        }
    }

    /**
     * closes this mixer <b>and all of its inputs</b>!
     */
    @Override
    public void close() {
        synchronized (inputs) {
            for (SoundInput snd : inputs) {
                snd.close();
            }
            inputs.clear();
        }
    }
}
