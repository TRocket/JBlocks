package org.jblocks.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import org.jblocks.sound.SoundInput;
import org.jblocks.sound.Recorder.RecorderListener;

/**
 *
 * @author ZeroLuck
 */
public class SimplePlayer implements Runnable {

    private Thread playerThread;
    private volatile boolean stopRequest;
    private SoundInput sound;
    private List<PlayerListener> listeners;

    public SimplePlayer() {
        listeners = new ArrayList<PlayerListener>();
    }

    /**
     * Returns true if the SimplePlayer is playing a sound, otherwise false. <br />
     */
    public boolean isPlaying() {
        return playerThread != null && playerThread.isAlive();
    }

    /**
     * Stops the SimplePlayer. <br />
     */
    public void stop() {
        if (isPlaying()) {
            fireFinished();
            stopRequest = true;
            try {
                playerThread.join(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Adds the specified PlayerListener to this SimplePlayer..
     */
    public void addPlayerListener(PlayerListener rl) {
        listeners.add(rl);
    }

    /**
     * Removes the specified PlayerListener from this SimplePlayer.
     */
    public void removePlayerListener(PlayerListener rl) {
        listeners.remove(rl);
    }

    /**
     * Plays the sound in the SimplePlayer's thread. <br />
     * 
     * @throws IllegalStateException - if the SimplePlayer is already playing a sound.
     */
    public void play(SoundInput inp) {
        if (isPlaying()) {
            throw new IllegalStateException("The player is already playing a sound");
        }
        stopRequest = false;
        sound = inp;
        playerThread = new Thread(this, "Audio-Player");
        playerThread.start();
    }

    private void fireFinished() {
        for (PlayerListener pl : listeners) {
            pl.finished();
        }
    }

    private void fireError(final Throwable t) {
        for (PlayerListener pl : listeners) {
            pl.error(t);
        }
    }

    @Override
    public void run() {
        try {
            SourceDataLine line = AudioSystem.getSourceDataLine(sound.getFormat());
            line.open();
            line.start();
            byte[] buf = new byte[8096];
            int len;
            while ((len = sound.read(buf, 0, buf.length)) != -1 && !stopRequest) {
                int off = 0;
                while (off < len) {
                    off += line.write(buf, off, len - off);
                }
            }
            if (!stopRequest) {
                line.drain();
            }
            line.stop();
            line.close();
            fireFinished();
        } catch (Throwable t) {
            fireError(t);
        }
    }

    public static interface PlayerListener {

        public void finished();

        public void error(Throwable t);
    }
}
