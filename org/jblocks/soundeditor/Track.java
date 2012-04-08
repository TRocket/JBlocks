package org.jblocks.soundeditor;

import org.jblocks.sound.SoundInput;

/**
 *
 * @author ZeroLuck
 */
class Track {

    private final int offset;
    private final short[] samples;
    private final int end;
    private final int spp;
    private final String name;

    public Track(Track parent, int off, int e) {
        if (e < off) {
            throw new IllegalArgumentException("end is smaller than the offset");
        }
        this.samples = parent.samples;
        this.spp = parent.spp;
        this.name = parent.name;

        this.offset = parent.offset + off;
        this.end = parent.offset + e;
    }

    public Track(short[] samples, int samplesPerPixel, String name) {
        this.samples = samples;
        this.offset = 0;
        this.end = samples.length;
        this.name = name;
        this.spp = samplesPerPixel;
    }

    public String getName() {
        return name;
    }

    public short[] getSamples() {
        return samples;
    }

    public int getOffset() {
        return offset;
    }

    public int getEnd() {
        return end;
    }

    /**
     * Returns how many samples are displayed in one pixel. <br />
     */
    public int getSamplesPerPixel() {
        return spp;
    }

    /**
     * Creates a short array which can be displayed on screen. <br />
     * 
     * @see #getSamplesPerPixel() 
     */
    public short[] createUserView() {
        int length = end - offset;
        short[] view = new short[(int) ((double) length / spp)];
        for (int i = 0; i < view.length; i++) {
            view[i] = samples[offset + i * spp];
        }
        return view;
    }
}
