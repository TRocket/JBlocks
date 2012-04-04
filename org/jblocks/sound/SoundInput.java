package org.jblocks.sound;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author ZeroLuck
 */
public abstract class SoundInput {

    /**
     * Returns the AudioFormat of this SoundInput. <br />
     * 
     * @see javax.sound.sampled.AudioFormat
     * @return - the AudioFormat of this SoundInput.
     */
    public abstract AudioFormat getFormat();

    /**
     * 
     * @return - true if EOF, else false.
     */
    public abstract boolean eof();

    /**
     *
     * Reads samples from this SoundInput. <br />
     * 
     * @param data - the byte array in which to copy the data.
     * @param off - the offset of the "data" byte array.
     * @param len - the length to read.
     * @return - the count of bytes which were actually copied.
     * @throws IOException - if an IO error occurs.
     */
    public abstract int read(byte[] data, int off, int len)
            throws IOException;

    /**
     * closes this SoundInput. <br />
     */
    public abstract void close();

    /**
     * creates a SoundInput from an AudioInputStream. <br />
     * 
     * @param in - the source for this SoundInput.
     * @return - a created SoundInput object.
     */
    public static SoundInput fromStream(final AudioInputStream in) {
        return new SoundInput() {

            private boolean isEOF = false;

            @Override
            public AudioFormat getFormat() {
                return in.getFormat();
            }

            @Override
            public boolean eof() {
                return isEOF;
            }

            @Override
            public int read(byte[] data, int off, int len) throws IOException {
                int cnt = in.read(data, off, len);
                if (cnt == -1) {
                    isEOF = true;
                }
                return cnt;
            }

            @Override
            public void close() {
                try {
                    in.close();
                } catch (IOException io) {
                    // what to do?
                }
            }
        };
    }

    /**
     * creates a SoundInput from a byte array. <br />
     * 
     * @param b - the source byte array.
     * @param fmt - the AudioFormat of the byte array.
     * @return - the created SoundInput object.
     */
    public static SoundInput fromByteArray(final byte[] b, final AudioFormat fmt) {
        return new SoundInput() {

            private int offset = 0;

            @Override
            public AudioFormat getFormat() {
                return fmt;
            }

            @Override
            public boolean eof() {
                return offset < b.length;
            }

            @Override
            public int read(byte[] data, int off, int len) throws IOException {
                int x = Math.min(len, b.length - offset);
                if (b.length - offset <= 0) {
                    return -1;
                }
                System.arraycopy(b, offset, data, off, len);
                offset += x;
                return x;
            }

            @Override
            public void close() {
            }
        };
    }
}
