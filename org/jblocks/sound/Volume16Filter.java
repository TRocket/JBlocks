package org.jblocks.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;

/**
 *
 * Very basic control of a sound input. <br />
 * This class works just with 16 bit samples. <br />
 * 
 * @author ZeroLuck
 */
public class Volume16Filter extends SoundFilter {

    private float volume = 1F;

    public Volume16Filter(SoundInput inp) {
        super(inp);
    }

    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        len = len - len % 2;
        if (len <= 0) {
            return 0;
        }
        int cnt = parent.read(data, off, len);
        if (cnt == -1) {
            return -1;
        }
        AudioFormat fmt = getFormat();
        ByteBuffer dataBuf = ByteBuffer.wrap(data, off, len);
        if (fmt.isBigEndian()) {
            dataBuf.order(ByteOrder.BIG_ENDIAN);
        } else {
            dataBuf.order(ByteOrder.LITTLE_ENDIAN);
        }
        ShortBuffer samples = dataBuf.asShortBuffer();
        for (int i = 0; i < cnt / 2; i++) {
            float s = samples.get(i);
            s *= volume;

            samples.put(i, clip16((int) s));
        }

        return cnt;
    }

    private short clip16(int i) {
        return (i < Short.MIN_VALUE) ? Short.MIN_VALUE : (i > Short.MAX_VALUE) ? Short.MAX_VALUE : (short) i;
    }

    /**
     * Sets the volume of this Volume16Filter. <br />
     * @param v - the new volume.
     */
    public void setVolume(float v) {
        volume = v;
    }

    /**
     * Returns the volume of this Volume16Filter. <br />
     */
    public float getVolume() {
        return volume;
    }
}
