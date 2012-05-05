package org.jblocks.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;

public class Echo16Filter extends SoundFilter {

    private short[] delayBuffer;
    private int delayBufferPos;
    private float decay;

    /**
     * Creates an Echo16Filter with the specified number of delay samples and the
     * specified decay rate.
     */
    public Echo16Filter(SoundInput inp, int numDelaySamples, float decay) {
        super(inp);
        delayBuffer = new short[numDelaySamples];
        this.decay = decay;
    }

    /**
     * Convenience method for getting a 16-bit sample from a byte array. Samples
     * should be in 16-bit, signed, little-endian format.
     */
    private static short getSample(byte[] buffer, int position) {
        return (short) (((buffer[position + 1] & 0xff) << 8) | (buffer[position] & 0xff));
    }

    /**
     * Convenience method for setting a 16-bit sample in a byte array. Samples
     * should be in 16-bit, signed, little-endian format.
     */
    private static void setSample(byte[] buffer, int position, short sample) {
        buffer[position] = (byte) (sample & 0xff);
        buffer[position + 1] = (byte) ((sample >> 8) & 0xff);
    }

    /**
     * Filters the sound samples to add an echo. The samples played are added to
     * the sound in the delay buffer multipied by the decay rate. The result is
     * then stored in the delay buffer, so multiple echoes are heard.
     */
    @Override
    public int read(byte[] samples, int offset, int length) throws IOException {
        length -= length % 2;
        length = parent.read(samples, offset, length);
        for (int i = offset; i < offset + length; i += 2) {
            // update the sample
            short oldSample = getSample(samples, i);
            int newSample = (int) (oldSample + decay
                    * delayBuffer[delayBufferPos]);

            short clipped = newSample > Short.MAX_VALUE ? Short.MAX_VALUE
                    : newSample < Short.MIN_VALUE
                    ? Short.MIN_VALUE : (short) newSample;

            setSample(samples, i, clipped);

            // update the delay buffer
            delayBuffer[delayBufferPos] = clipped;
            delayBufferPos++;
            if (delayBufferPos == delayBuffer.length) {
                delayBufferPos = 0;
            }
        }
        return length;
    }

    public static void main(String[] args) throws Exception {
        SimplePlayer p = new SimplePlayer();
        p.play(new Echo16Filter(SoundInput.fromStream(AudioSystem.getAudioInputStream(new File("C:/JTest/iyaz.wav"))), 11025 * 80, .8f));
    }
}

