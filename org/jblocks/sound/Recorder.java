package org.jblocks.sound;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * This class can be used to record a microphone. <br />
 *
 * @author ZeroLuck
 */
public class Recorder implements Runnable {

    private AudioFormat fmt;
    private ByteArrayOutputStream data;
    private Thread recorderThread;
    private List<RecorderListener> listeners;
    private volatile boolean stopRequest;

    /**
     * Creates a new Recorder with this AudioFormat: <br />
     * 22050 Hz, 16 Bits, mono, signed, little-endian <br />
     */
    public Recorder() {
        // 22050 Hz, 16 Bits, mono, signed, little-endian
        this(new AudioFormat(22050, 16, 1, true, false));
    }

    /**
     * Adds the specified RecorderListener to this Recorder.
     */
    public void addRecorderListener(RecorderListener rl) {
        listeners.add(rl);
    }

    /**
     * Removes the specified RecorderListener from this Recorder.
     */
    public void removeRecorderListener(RecorderListener rl) {
        listeners.remove(rl);
    }

    /**
     * Creates a new Recorder with the specified AudioFormat. <br />
     * 
     * @param f - the AudioFormat
     */
    public Recorder(AudioFormat f) {
        fmt = f;
        data = new ByteArrayOutputStream();
        listeners = new ArrayList<RecorderListener>();
    }

    /**
     * Resets the Recorder and releases the sample-buffer. <br />
     * If the Recorder is currently recording, stop() is called. <br />
     */
    public void reset() {
        if (isRecording()) {
            stop();
        }
        data = new ByteArrayOutputStream();
    }

    /**
     * Returns the AudioFormat of this Recorder. <br />
     */
    public AudioFormat getFormat() {
        return fmt;
    }

    /**
     * Returns true if the Recorder is running, false otherwise. <br />
     */
    public boolean isRecording() {
        return recorderThread != null && recorderThread.isAlive();
    }

    /**
     * Starts the Recorder. <br />
     * @throws IllegalStateException - if the Recorder is already running.
     * @see #stop()
     */
    public void start() {
        if (isRecording()) {
            throw new IllegalStateException("the Recorder is already running.");
        }
        recorderThread = new Thread(this, "Sound-Recorder");
        stopRequest = false;
        recorderThread.start();
    }

    /**
     * Stops the Recorder. <br />
     * @see #getAudioData() 
     */
    public void stop() {
        if (recorderThread.isAlive()) {
            stopRequest = true;
            try {
                recorderThread.join(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Creates a byte-array from the Recorder's recorded audio data <br />
     * <code>stop()</code> should be called before. <br />
     */
    public byte[] getAudioData() {
        return data.toByteArray();
    }

    private void fireUpdate(int newPeak, int newVolume, long millis) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            RecorderListener rl = listeners.get(i);
            rl.update(newPeak, newVolume, millis);
        }
    }

    private void fireError(final Throwable t) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            RecorderListener rl = listeners.get(i);
            rl.error(t);
        }
    }

    @Override
    public void run() {
        try {
            int sampleSize = fmt.getFrameSize();
            int Hz = (int) fmt.getSampleRate();
            boolean updateSupported = fmt.getChannels() == 1 && sampleSize == 2;

            TargetDataLine line = AudioSystem.getTargetDataLine(fmt);
            line.open();
            byte[] buf = new byte[8096];
            ByteBuffer sampleBuffer = ByteBuffer.wrap(buf);
            if (fmt.isBigEndian()) {
                sampleBuffer.order(ByteOrder.BIG_ENDIAN);
            } else {
                sampleBuffer.order(ByteOrder.LITTLE_ENDIAN);
            }
            int len;

            line.start();
            while (!stopRequest) {
                len = line.read(buf, 0, buf.length);
                data.write(buf, 0, len);

                if (updateSupported) {
                    int peak = 0;
                    long volume = 0;
                    int cnt = 0;
                    for (int i = 0; i < len; i += 2, cnt++) {
                        int sample = Math.abs(sampleBuffer.getShort(i));
                        if (sample > peak) {
                            peak = sample;
                        }
                        volume += sample;
                    }
                    fireUpdate(peak, (int) (volume / cnt), (data.size() / sampleSize / Hz) * 1000);
                }
            }
            line.stop();
            line.close();
        } catch (LineUnavailableException ex) {
            fireError(ex);
        } catch (Throwable ex) {
            fireError(ex);
        }
    }

    public static interface RecorderListener {

        public void update(int newPeak, int newVolume, long millis);

        public void error(Throwable t);
    }
}
