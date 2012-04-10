package org.jblocks.soundeditor;

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
    private float volume = 1F;

    public Track(Track parent, int off, int e) {
        if (e < off) {
            throw new IllegalArgumentException("end is smaller than the offset");
        }
        this.samples = parent.samples;
        this.spp = parent.spp;
        this.name = parent.name;
        this.volume = parent.volume;

        this.offset = parent.offset + off;
        this.end = parent.offset + e;
    }

    public Track(short[] samples, int samplesPerPixel, String name) {
        this.samples = samples;
        this.offset = 0;
        this.end = samples.length;
        this.name = name;
        this.spp = samplesPerPixel;
        this.volume = 1F;
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
    
    public void setVolume(float v) {
        volume = v;
    }
    
    public float getVolume() {
        return volume;
    }

    /**
     * Returns how many samples are displayed in one pixel. <br />
     */
    public int getSamplesPerPixel() {
        return spp;
    }
    
    private short clip16(int i) {
        return i > Short.MAX_VALUE ? Short.MAX_VALUE : i < Short.MIN_VALUE ? Short.MIN_VALUE : (short) i;
    }

    private short[] userView;
    private long time;
    
    /**
     * Creates a short array which can be displayed on screen. <br />
     * 
     * @see #getSamplesPerPixel() 
     */
    public short[] createUserView() {
        if (userView != null && System.currentTimeMillis() - time < 500) {
            return userView;
        }
        
        int length = end - offset;
        short[] view = new short[(int) ((double) length / spp)];
        for (int i = 0; i < view.length; i++) {
            view[i] = clip16((int) (samples[offset + i * spp] * volume));
        }
        userView = view;
        time = System.currentTimeMillis();
        return view;
    }
}
