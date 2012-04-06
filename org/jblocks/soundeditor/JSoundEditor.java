package org.jblocks.soundeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.jblocks.JBlocks;
import org.jblocks.sound.SimplePlayer;
import org.jblocks.sound.SoftwareMixer;
import org.jblocks.sound.SoundInput;

/**
 *
 * This is the default JBlocks-SoundEditor. <br />
 * It uses a SoftwareMixer to mix from different sounds one sound. <br />
 * 
 * @author ZeroLuck
 * @version 0.1
 * @see org.jblocks.sound.SoftwareMixer
 */
public final class JSoundEditor extends JPanel {

    // <global-1>
    static ImageIcon icon_microphone;
    static ImageIcon icon_play;
    static ImageIcon icon_stop;
    static ImageIcon icon_pause;
    static ImageIcon icon_open;
    static ImageIcon icon_synthesizer;
    static ImageIcon icon_cutout;
    static ImageIcon icon_trash;
    static ImageIcon icon_eraser;
    static ImageIcon icon_cursor;
    // <global-2>
    static final int TRACK_HEIGHT = 50;
    private final int TRACK_CNT = 6;
    private final int PIX_FOR_SECOND = 50;
    // <member>
    private JToolBar tools;
    private JTrackPane tracks;
    private SoundEditorTool tool;
    private final AudioFormat editorFormat = new AudioFormat(22050, 16, 1, true, false);
    private final SimplePlayer player;

    static {
        icon_microphone = new ImageIcon(JBlocks.class.getResource("res/microphone.png"));
        icon_play = new ImageIcon(JBlocks.class.getResource("res/play.png"));
        icon_stop = new ImageIcon(JBlocks.class.getResource("res/stop.png"));
        icon_pause = new ImageIcon(JBlocks.class.getResource("res/pause.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_synthesizer = new ImageIcon(JBlocks.class.getResource("res/synthesizer.png"));
        icon_trash = new ImageIcon(JBlocks.class.getResource("res/trash.png"));
        icon_cutout = new ImageIcon(JBlocks.class.getResource("res/cut-out.png"));
        icon_eraser = new ImageIcon(JBlocks.class.getResource("res/eraser.png"));
        icon_cursor = new ImageIcon(JBlocks.class.getResource("res/cursor.png"));
    }

    // just a test method, will be removed later...
    private static short[] read(String file) throws Exception {
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(file));
        System.out.println(in.getFormat());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        in.close();
        byte[] bytes = out.toByteArray();
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        return shorts;
    }

    private short[] getDefaultTrack() {
        try {
            // ZeroLuck: This works just on my PC. It will be removed later.
            return read("C:/JTest/test2.wav");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public JSoundEditor() {
        tools = new JToolBar();
        tracks = new JTrackPane(TRACK_HEIGHT, TRACK_CNT);
        player = new SimplePlayer(2056);
        final JScrollPane scroll = new JScrollPane(tracks);

        setLayout(new BorderLayout());
        add(tools, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        JButton openButton = new JButton(icon_open);
        openButton.setToolTipText("Import");
        tools.add(openButton);

        JButton cleanButton = new JButton(icon_trash);
        cleanButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                tracks.removeAll();
                tracks.repaint();
            }
        });
        cleanButton.setToolTipText("Clean");
        tools.add(cleanButton);
        tools.add(new JSeparator(JSeparator.VERTICAL));

        JButton record = new JButton(icon_microphone);
        record.setToolTipText("Microphone");
        record.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openRecorderDialog();
            }
        });
        tools.add(record);

        JButton synth = new JButton(icon_synthesizer);
        synth.setToolTipText("<HTML>Synthesizer - <i>not implemented yet</i></HTML>");
        tools.add(synth);

        tools.add(new JSeparator(JSeparator.VERTICAL));

        final JButton playButton = new JButton(icon_play);
        playButton.setToolTipText("Play");
        playButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                play();
                playButton.setEnabled(false);
            }
        });
        tools.add(playButton);

        final JButton stopButton = new JButton(icon_stop);
        stopButton.setToolTipText("Stop");
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                player.stop();
            }
        });
        
        tools.add(playButton);
        tools.add(stopButton);
        
        player.addPlayerListener(new SimplePlayer.PlayerListener() {

            @Override
            public void finished() {
                playButton.setEnabled(true);
                tracks.resetPos();
            }

            @Override
            public void error(Throwable t) {
                finished();
                Toolkit.getDefaultToolkit().beep();
            }

            @Override
            public void timeUpdate(long newTimeLong) {
                double newTime = newTimeLong;
                tracks.setPos((int) (newTime / 1000 * PIX_FOR_SECOND));
            }
        });

        tools.add(new JSeparator(JSeparator.VERTICAL));


        Track t = new Track(getDefaultTrack(), (int) getFormat().getFrameRate() / PIX_FOR_SECOND, "Hello World");
        tracks.addTrack(new JSoundTrack(t, TRACK_HEIGHT));


        // <tools>
        addTool(new CursorTool(this), icon_cursor, "cursor");
        addTool(new CutOutTool(this), icon_cutout, "cut out");
        addTool(new EraserTool(this, icon_eraser), icon_eraser, "eraser");
        // </tool>

        scroll.setColumnHeaderView(new TimeColumnView(PIX_FOR_SECOND));
        scroll.setRowHeaderView(new TrackRowView(TRACK_HEIGHT, TRACK_CNT));
    }

    private void play() {
        AudioFormat fmt = getFormat();
        TimedSoftwareMixer mix = new TimedSoftwareMixer(fmt);
        for (Component comp : tracks.getComponents()) {
            if (comp instanceof JSoundTrack) {
                JSoundTrack t = (JSoundTrack) comp;
                Track track = t.getTrack();
                short[] samples = track.getSamples();
                byte[] data = new byte[samples.length * 2];
                ByteBuffer bytes = ByteBuffer.wrap(data);
                bytes.order(ByteOrder.LITTLE_ENDIAN);
                for (int i = 0; i < samples.length; i++) {
                    bytes.putShort(i * 2, samples[i]);
                }
                SoundInput inp = SoundInput.fromByteArray(data, track.getOffset() * 2, (track.getEnd() - track.getOffset()) * 2, fmt);
                mix.addTimed(inp, (long) ((double) t.getX() / PIX_FOR_SECOND * 1000));
            }
        }
        player.play(mix);
    }

    private JDesktopPane getDesktop() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JDesktopPane) {
                return (JDesktopPane) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private void openRecorderDialog() {
        Container desktop = getDesktop();
        if (desktop != null) {
            JInternalFrame frm = new JInternalFrame("Microphone Recorder");
            frm.setClosable(true);
            frm.setLayout(new BorderLayout());
            JSoundRecorder p = new JSoundRecorder();
            frm.add(p, BorderLayout.CENTER);
            desktop.add(frm, 0);

            frm.pack();
            frm.setVisible(true);
            Point loc = SwingUtilities.convertPoint(this, getLocation(), desktop);
            frm.setLocation(loc.x + getWidth() / 2 - frm.getWidth() / 2,
                    loc.y + getHeight() / 2 - frm.getHeight() / 2);

        }
    }

    public AudioFormat getFormat() {
        return editorFormat;
    }

    void setTool(final SoundEditorTool t) {
        if (tool != null) {
            tool.uninstall(tracks);
        }
        tool = t;
        if (tool != null) {
            tool.install(tracks);
        }
    }

    void addTool(final SoundEditorTool tool, ImageIcon icon, String tip) {
        JButton b = new JButton(icon);
        b.setToolTipText(tip);
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                setTool(tool);
            }
        });

        tools.add(b);
    }
}
