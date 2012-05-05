package org.jblocks.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;

/**
 * This filter converts a 16bit sound-input to another AudioFormat. <br />
 * This does: <br />
 * <ul>
 *    <li>upsampling/downsampling </li>
 *    <li>removes/adds channels </li>
 *    <li>converts big-endian to little-endian, or the other way</li>
 * </ul>
 * Warning: Upsampling and Downsampling is implemented very bad! <br />
 * 
 * @author ZeroLuck
 */
public class AudioFormat16Filter extends SoundFilter {

    private final AudioFormat target;
    private final float sampleFactor;
    private final boolean switchBytes;

    public AudioFormat16Filter(SoundInput inp, AudioFormat target) {
        super(inp);
        AudioFormat fmt = inp.getFormat();
        this.target = target;
        this.sampleFactor = (float) target.getFrameRate() / fmt.getFrameRate() * target.getChannels() / fmt.getChannels();
        if (fmt.isBigEndian() != target.isBigEndian()) {
            switchBytes = true;
        } else {
            switchBytes = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioFormat getFormat() {
        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] dest, int off, int len) throws IOException {
        len = len - len % 2; // we can just read 16-bit values!
        if (len <= 0) {
            return 0;
        }
     
        byte[] buf = null;
        if (sampleFactor <= 1) {
            buf = new byte[len];
        } else {
            buf = new byte[(int) (len / sampleFactor)];
        }
        int cnt = parent.read(buf, 0, buf.length);
        if (cnt == -1) {
            return -1; // EOF
        }

        // switch between little-endian and big-endian if necessary
        if (switchBytes) {
            for (int i = 0; i < len; i += 2) {
                byte tmp = buf[i];
                buf[i] = buf[i + 1];
                buf[i + 1] = tmp;
            }
        }

        ByteBuffer destBufBytes = ByteBuffer.wrap(dest, off, len);
        ByteBuffer srcBufBytes = ByteBuffer.wrap(buf);
        if (!target.isBigEndian()) {
            destBufBytes.order(ByteOrder.LITTLE_ENDIAN);
            srcBufBytes.order(ByteOrder.LITTLE_ENDIAN);
        }


        ShortBuffer destBuf = destBufBytes.asShortBuffer();
        ShortBuffer srcBuf = srcBufBytes.asShortBuffer();

        if (sampleFactor <= 1) {
            // downsampling
            double i = 0;
            for (int c = 0; c < cnt / 2; i += sampleFactor, c++) {
                destBuf.put((int) i, srcBuf.get(c));
            }
            return (int) i * 2;
        } else {
            // upsampling
            double i = 0;
            for (int c = 0; c < cnt / 2; c++) {
                destBuf.put((int) i, srcBuf.get(c));
                int j = (int) (sampleFactor - sampleFactor % 1);
                while (j > 0) {
                    destBuf.put((int) i, srcBuf.get(c));
                    j--;
                    i++;
                }
                i += (sampleFactor % 1);
                
            }
            return (int) i * 2;
        }
    }
}
