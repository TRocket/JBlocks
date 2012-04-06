package org.jblocks.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author ZeroLuck
 */
public class SimplePlayer implements Runnable {

    private Thread playerThread;
    private volatile boolean stopRequest;
    private volatile boolean pause;
    private SoundInput sound;
    private final List<PlayerListener> listeners;
    private final int BUFFER_SIZE;

    public SimplePlayer(int bufsize) {
        listeners = new ArrayList<PlayerListener>();
        BUFFER_SIZE = bufsize;
    }

    public SimplePlayer() {
        this(8096);
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
        pause = false;
        sound = inp;
        playerThread = new Thread(this, "Audio-Player");
        playerThread.start();
    }

    public void setPause(boolean p) {
        pause = p;
    }

    private void fireFinished() {
        for (PlayerListener pl : listeners) {
            pl.finished();
        }
    }

    private boolean fireError(final Throwable t) {
        int size = listeners.size();
        if (size <= 0) {
            return false;
        }
        for (PlayerListener pl : listeners) {
            pl.error(t);
        }
        return true;
    }

    private void fireTimeUpdate(final long newTime) {
        synchronized (listeners) {
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                listeners.get(i).timeUpdate(newTime);
            }
        }
    }

    @Override
    public void run() {
        try {
            AudioFormat fmt = sound.getFormat();
            int div = (int) (fmt.getFrameRate() * fmt.getFrameSize());

            SourceDataLine line = AudioSystem.getSourceDataLine(fmt);
            line.open();
            line.start();
            byte[] buf = new byte[BUFFER_SIZE];
            long written = 0;
            int len;
            while ((len = sound.read(buf, 0, buf.length)) != -1 && !stopRequest) {
                int off = 0;
                while (off < len) {
                    off += line.write(buf, off, len - off);
                }
                while (pause && !stopRequest) {
                    Thread.sleep(5);
                }
                written += len;
                fireTimeUpdate((long) ((double) written / div * 1000));
            }
            if (!stopRequest) {
                line.drain();
            }
            line.stop();
            line.close();
            fireFinished();
        } catch (Throwable t) {
            if (!fireError(t)) {
                System.err.println("WARNING: Unhandled Throwable in org.jblocks.SimplePlayer.");
            }
        }
    }

    public static interface PlayerListener {

        public void finished();

        public void error(Throwable t);

        public void timeUpdate(long newTime);
    }
}
