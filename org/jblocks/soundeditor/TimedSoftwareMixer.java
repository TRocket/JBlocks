package org.jblocks.soundeditor;

import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import org.jblocks.sound.SoftwareMixer;
import org.jblocks.sound.SoundInput;

/**
 *
 * @author ZeroLuck
 */
public class TimedSoftwareMixer extends SoftwareMixer {

    private final ArrayList<TimedInput> inputs = new ArrayList<TimedInput>();
    private final ArrayList<TimedInput> remove = new ArrayList<TimedInput>();
    private long currentRead = 0;
    private long currentTime = 0;

    public TimedSoftwareMixer(AudioFormat fmt) {
        super(fmt);
    }

    public void addTimed(SoundInput inp, long t) {
        if (t < currentTime) {
            return;
        }
        synchronized (inputs) {
            inputs.add(new TimedInput(inp, t));
        }
    }

    @Override
    public void close() {
        super.close();
        inputs.clear();
        remove.clear();
        inputs.trimToSize();
        remove.trimToSize();
    }
    
    @Override
    public boolean eof() {
        return super.eof() && inputs.isEmpty();
    }

    @Override
    public int read(byte[] data, int off, int len) throws IOException {
        synchronized (inputs) {
            remove.clear();
            for (TimedInput inp : inputs) {
                if (inp.time <= currentTime) {
                    addInput(inp.inp);
                    remove.add(inp);
                }
            }
            inputs.removeAll(remove);
        }
        AudioFormat fmt = getFormat();
        // this should be fixed.
        // <fixme>
        int cnt = super.read(data, off, Math.min(len, 2000));
        // </fixme>
        currentRead += cnt;
        currentTime = currentRead / (fmt.getFrameSize() * (int) fmt.getFrameRate()) * 1000;
        
        return cnt;
    }

    private static class TimedInput {

        final SoundInput inp;
        final long time;

        public TimedInput(SoundInput inp, final long time) {
            this.inp = inp;
            this.time = time;
        }
    }
}
