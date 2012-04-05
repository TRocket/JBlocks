package org.jblocks.soundeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import org.jblocks.sound.SoundInput;
import org.jblocks.sound.SimplePlayer;
import org.jblocks.sound.Recorder;

/**
 *
 * @author ZeroLuck
 */
public class JSoundRecorder extends JPanel {

    private final VolumeView volume;
    private final JLabel time;
    private final Recorder rec;
    private final SimplePlayer player;

    public JSoundRecorder() {
        volume = new VolumeView();
        time = new JLabel("0:00");
        rec = new Recorder();
        player = new SimplePlayer();

        setLayout(new BorderLayout());

        JPanel north = new JPanel(new BorderLayout(10, 0));
        north.add(volume, BorderLayout.CENTER);
        north.add(time, BorderLayout.EAST);

        add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout());

        final JButton playButton = new JButton(JSoundEditor.icon_play);
        playButton.setToolTipText("Play");
        final JButton stopButton = new JButton(JSoundEditor.icon_stop);
        stopButton.setToolTipText("Stop");
        final JButton recordButton = new JButton(JSoundEditor.icon_microphone);
        recordButton.setToolTipText("Record");

        playButton.setEnabled(false);
        stopButton.setEnabled(false);

        rec.addRecorderListener(new Recorder.RecorderListener() {

            @Override
            public void update(int newPeak, int newVolume, long millis) {
                setTime(millis);
                int max = volume.getMaximumValue();
                int peak = (int) ((double) max * ((double) newPeak / Short.MAX_VALUE));
                if (peak < volume.getPeak()) {
                    peak = volume.getPeak() - 1;

                    volume.setPeak(peak);

                } else {
                    volume.setPeak(peak);
                }

                volume.setValue((int) ((double) max * ((double) newVolume / Short.MAX_VALUE)));
            }

            @Override
            public void error(Throwable t) {
                volume.setPeak(0);
                volume.setValue(0);
                playButton.setEnabled(false);
                stopButton.setEnabled(false);
            }
        });
        
        player.addPlayerListener(new SimplePlayer.PlayerListener() {

            @Override
            public void finished() {
                recordButton.setEnabled(true);
                playButton.setEnabled(true);
            }

            @Override
            public void error(Throwable t) {
                finished();
            }
        });

        recordButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!rec.isRecording()) {
                    rec.start();
                    recordButton.setEnabled(false);
                    playButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (rec.isRecording()) {
                    rec.stop();
                    volume.setValue(0);
                    volume.setPeak(0);
                    recordButton.setEnabled(true);
                    playButton.setEnabled(true);
                }
                if (player.isPlaying()) {
                    player.stop();
                }
            }
        });

        playButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                recordButton.setEnabled(false);
                stopButton.setEnabled(true);
                playButton.setEnabled(false);
                
                player.play(SoundInput.fromByteArray(rec.getAudioData(), rec.getFormat()));
            }
        });

        center.add(playButton);
        center.add(recordButton);
        center.add(stopButton);

        add(center, BorderLayout.CENTER);

        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        player.stop();
        rec.reset();
    }

    private void setTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds - (minutes * 60);

        String s = "" + seconds;
        if (s.length() == 1) {
            s = "0" + seconds;
        }

        time.setText(minutes + ":" + s);
    }

    /**
     * @author ZeroLuck
     */
    private static class VolumeView extends JComponent {

        // <global>
        private static final int H = 30;
        private static final int CNT = 100;
        private static final int LW = 3;
        // <member>
        private int peak = 0;
        private int value = 0;

        /**
         * Creates a new VolumeView with peak = 0 and volume = 0. <br />
         */
        public VolumeView() {
            setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
        }

        /**
         * Returns the current peak of this VolumeView. <br />
         * @see #setPeak(int) 
         */
        public void setPeak(int p) {
            if (p < 0) {
                p = 0;
            }
            if (p >= CNT) {
                p = CNT - 1;
            }
            peak = p;
            repaint();
        }

        /**
         * Returns the current peak of this VolumeView. <br />
         * @see #setPeak(int) 
         */
        public int getPeak() {
            return peak;
        }

        /**
         * Sets the current value of this VolumeView. <br />
         * @see #getValue() 
         */
        public void setValue(int v) {
            value = v;
            repaint();
        }

        /**
         * Returns the current value of this VolumeView. <br />
         * @see #setValue(int) 
         */
        public int getValue() {
            return value;
        }

        /**
         * Returns the maximum value (and peak) this VolumeView can have. <br />
         */
        public int getMaximumValue() {
            return CNT;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(CNT * LW, H);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paintComponent(Graphics grp) {
            Graphics2D g = (Graphics2D) grp;

            g.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, H, Color.DARK_GRAY));
            g.fillRect(0, 0, getWidth(), H);

            for (int i = 0; i < value; i++) {
                if (i < CNT / 5 * 3) {
                    g.setColor(Color.GREEN);
                } else if (i < CNT / 5 * 4) {
                    g.setColor(Color.ORANGE);
                } else if (i < CNT / 5 * 5) {
                    g.setColor(Color.RED);
                }
                g.drawLine(i * LW, 3, i * LW, H - 3);
            }

            g.setPaint(new GradientPaint(0, 3, Color.DARK_GRAY, 0, H - 3, Color.LIGHT_GRAY));
            for (int i = value; i < CNT; i++) {
                g.drawLine(i * LW, 3, i * LW, H - 3);
            }

            if (peak > 0) {
                g.setColor(Color.BLACK);
                g.fillRect(peak * LW, 3, LW, H - 5);
            }
        }
    }
}
