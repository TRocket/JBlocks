package org.jblocks.soundeditor;

/**
 *
 * @author ZeroLuck
 */
class Track {

    private final int minOffset;
    private int offset;
    private short[] samples;
    private int end;
    private final int maxEnd;
    private int spp;
    private String name;

    public Track(short[] samples, int samplesPerPixel, int off, int e, String name) {
        this.samples = samples;
        this.offset = 0;
        this.minOffset = off;
        this.end = e;
        this.maxEnd = e;
        this.spp = samplesPerPixel;
        this.name = name;
    }

    public Track(short[] samples, int samplesPerPixel, String name) {
        this(samples, samplesPerPixel, 0, samples.length, name);
    }

    public void setOffset(int p) {
        offset = Math.min(minOffset + p * spp, end);
    }

    public String getName() {
        return name;
    }
    
    
    public void setEnd(int p) {
        end = Math.min(minOffset + p * spp, maxEnd);
    }
    
    public int getOffset() {
        return offset - minOffset;
    }
    
    public int getEnd() {
        return end - minOffset;
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
        short[] view = new short[length / spp];
        for (int i = 0; i < view.length; i++) {
            view[i] = samples[offset + i * spp]; 
        }
        return view;
    }
}
