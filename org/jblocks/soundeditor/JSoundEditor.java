package org.jblocks.soundeditor;

import ext.ogg.VorbisCodec;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.jblocks.JBlocks;
import org.jblocks.sound.AudioFormat16Filter;
import org.jblocks.sound.SimplePlayer;
import org.jblocks.sound.SoftwareMixer;
import org.jblocks.sound.SoundInput;
import org.jblocks.sound.Volume16Filter;
import org.jblocks.utils.StreamUtils;

/**
 *
 * This is the default JBlocks-SoundEditor. <br />
 * It uses a SoftwareMixer to mix from different sounds one sound. <br />
 * 
 * @author ZeroLuck
 * @version 0.1
 * @see org.jblocks.sound.SoftwareMixer
 * @see org.jblocks.soundeditor.TimedSoftwareMixer
 * 
 */
public final class JSoundEditor extends JPanel {

    // <global-1>
    static ImageIcon icon_microphone;
    static ImageIcon icon_play;
    static ImageIcon icon_stop;
    static ImageIcon icon_pause;
    static ImageIcon icon_open;
    static ImageIcon icon_save;
    static ImageIcon icon_synthesizer;
    static ImageIcon icon_cutout;
    static ImageIcon icon_trash;
    static ImageIcon icon_eraser;
    static ImageIcon icon_cursor;
    static ImageIcon icon_volume;
    // <global-2>
    static final int TRACK_HEIGHT = 50;
    private final int TRACK_CNT = 6;
    private final int PIX_FOR_SECOND = 50;
    // <member>
    private JToolBar tools;
    private JTrackPane tracks;
    private SoundEditorTool tool;
    private final AudioFormat editorFormat = new AudioFormat(44100, 16, 1, true, false);
    private final SimplePlayer player;

    static {
        // should we use an image-pool instead?
        icon_microphone = new ImageIcon(JBlocks.class.getResource("res/microphone.png"));
        icon_play = new ImageIcon(JBlocks.class.getResource("res/play.png"));
        icon_stop = new ImageIcon(JBlocks.class.getResource("res/stop.png"));
        icon_pause = new ImageIcon(JBlocks.class.getResource("res/pause.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_save = new ImageIcon(JBlocks.class.getResource("res/save.png"));
        icon_synthesizer = new ImageIcon(JBlocks.class.getResource("res/synthesizer.png"));
        icon_trash = new ImageIcon(JBlocks.class.getResource("res/trash.png"));
        icon_cutout = new ImageIcon(JBlocks.class.getResource("res/cut-out.png"));
        icon_eraser = new ImageIcon(JBlocks.class.getResource("res/eraser.png"));
        icon_cursor = new ImageIcon(JBlocks.class.getResource("res/cursor.png"));
        icon_volume = new ImageIcon(JBlocks.class.getResource("res/volume.png"));
    }

    public JSoundEditor() {
        tools = new JToolBar();
        tracks = new JTrackPane(TRACK_HEIGHT, TRACK_CNT);
        player = new SimplePlayer();
        final JScrollPane scroll = new JScrollPane(tracks);

        setLayout(new BorderLayout());
        add(tools, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        JButton openButton = new JButton(icon_open);
        openButton.setToolTipText("Import");
        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openImportFileChooser();
            }
        });
        tools.add(openButton);

        JButton saveButton = new JButton(icon_save);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                openExportFileChooser();
            }
        });
        tools.add(saveButton);

        JButton cleanButton = new JButton(icon_trash);
        cleanButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                clean();
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
                inform(JOptionPane.ERROR_MESSAGE, "Error while playing the sounds!\nError: " + t);
            }

            @Override
            public void timeUpdate(long newTimeLong) {
                double newTime = newTimeLong;
                tracks.setPos((int) (newTime / 1000 * PIX_FOR_SECOND));
            }
        });

        tools.add(new JSeparator(JSeparator.VERTICAL));


        // <tools>
        addTool(new CursorTool(this), icon_cursor, "<HTML><u>cursor</u>: move a track</HTML>");
        addTool(new CutOutTool(this), icon_cutout, "<HTML><u>cut out</u>: cut out a piece of a track</HTML>");
        addTool(new EraserTool(this, icon_eraser), icon_eraser, "<HTML><u>eraser</u>: remove a track</HTML>");
        addTool(new VolumeTool(this), icon_volume, "<HTML><u>volume</u>: change the volume of a track</HTML>");
        // </tool>

        scroll.setColumnHeaderView(new TimeColumnView(PIX_FOR_SECOND));
        scroll.setRowHeaderView(new TrackRowView(TRACK_HEIGHT, TRACK_CNT));
    }

    public void clean() {
        tracks.removeTracks();
        player.stop();
    }

    /**
     * Exports the editor's sound to the specified OutputStream. <br />
     * The user will be informed if an error occurs! <br />
     * 
     * @param out - the OutputStream
     * @param t - the output format (WAV, AU, OGG, ...)
     * @param q - the quality of the file. (only for OGG)
     */
    private void export(OutputStream out, String t, float q) {
        try {
            if (t.equals("OGG")) {
                AudioFormat fmt = new AudioFormat(44100, 16, 2, true, false);
                SoundInput sound = new AudioFormat16Filter(createMixedAudioData(), fmt);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len;
                while ((len = sound.read(b, 0, b.length)) != -1) {
                    bout.write(b, 0, len);
                }
                VorbisCodec.encode(new AudioInputStream(new ByteArrayInputStream(bout.toByteArray()), fmt, bout.size() / 2), out, q);
            } else if (t.equals("JBSP")) {
                exportProject(out);
            } else {
                AudioFormat fmt = getFormat();
                SoundInput sound = createMixedAudioData();
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int len;
                while ((len = sound.read(b, 0, b.length)) != -1) {
                    bout.write(b, 0, len);
                }
                Type type = Type.WAVE;
                if (t != null) {
                    if (t.equals("WAV")) {
                        type = Type.WAVE;
                    } else if (t.equals("AIFC")) {
                        type = Type.AIFC;
                    } else if (t.equals("AIFF")) {
                        type = Type.AIFF;
                    } else if (t.equals("SND")) {
                        type = Type.SND;
                    } else if (t.equals("AU")) {
                        type = Type.AU;
                    }
                }
                AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(bout.toByteArray()), fmt, bout.size() / 2), type, out);
            }
        } catch (IOException io) {
            inform(JOptionPane.ERROR_MESSAGE, "Error while exporting sound!\nError: " + io);
        } catch (OutOfMemoryError mem) {
            inform(JOptionPane.ERROR_MESSAGE, "Java out of memory! (please try to remove some of your very big tracks)\nError: " + mem);
        }
    }

    private void openExportFileChooser() {
        String[] chooser = new String[]{
            "OGG",
            /*  "JBSP", */
            "WAV",
            "AIFF",
            "AU"};
        final Object selected = JOptionPane.showInternalInputDialog(this, "Please select a file type.", "Export", JOptionPane.QUESTION_MESSAGE,
                null, chooser, null);

        if (selected == null) {
            return;
        }

        final JFileChooser ch = new JFileChooser();
        ch.setMultiSelectionEnabled(false);
        ch.setApproveButtonText("Save");

        final JDesktopPane desktop = getDesktop();
        final JInternalFrame frm = new JInternalFrame("Select a sound file...");
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(ch, BorderLayout.CENTER);
        frm.pack();

        frm.setVisible(true);
        Point loc = SwingUtilities.convertPoint(this, getLocation(), desktop);
        frm.setLocation(loc.x + getWidth() / 2 - frm.getWidth() / 2,
                loc.y + getHeight() / 2 - frm.getHeight() / 2);

        desktop.add(frm, 0);
        frm.toFront();

        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if (cmd != null) {
                    if (cmd.equals(JFileChooser.CANCEL_SELECTION)) {
                        frm.dispose();
                    } else if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
                        FileOutputStream out = null;
                        try {
                            File f = ch.getSelectedFile();
                            if (f == null) {
                                return;
                            }
                            if (f.exists()) {
                                out = new FileOutputStream(f);
                            } else {
                                String path = f.getAbsolutePath();
                                path = StreamUtils.addFileExtension(path, (String) selected);
                                out = new FileOutputStream(path);
                            }
                            export(out, (String) selected, 0.3f);
                        } catch (IOException io) {
                            inform(JOptionPane.ERROR_MESSAGE, "Error while opening file!\nError: " + io);
                        } finally {
                            StreamUtils.safeClose(out);
                        }
                        frm.dispose();
                    }
                }
            }
        });

    }

    /**
     * Opens an Open-JFileChooser and lets the user import files. <br />
     */
    private void openImportFileChooser() {
        final String[] ext = new String[]{"wav", /* "jbsp", */ "ogg", "aiff", "aif", "au"};
        final JFileChooser ch = new JFileChooser();
        ch.setMultiSelectionEnabled(false);
        ch.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName().toLowerCase();
                for (String e : ext) {
                    if (name.endsWith("." + e)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                StringBuilder sb = new StringBuilder("Sound files: ");
                if (ext.length > 0) {
                    sb.append(ext[0].toUpperCase());
                    for (int i = 1; i < ext.length; i++) {
                        sb.append(", ").append(ext[i].toUpperCase());
                    }
                }
                return sb.toString();
            }
        });
        final JDesktopPane desktop = getDesktop();
        final JInternalFrame frm = new JInternalFrame("Select a sound file...");
        frm.setClosable(true);
        frm.setLayout(new BorderLayout());
        frm.add(ch, BorderLayout.CENTER);
        frm.pack();

        frm.setVisible(true);
        Point loc = SwingUtilities.convertPoint(this, getLocation(), desktop);
        frm.setLocation(loc.x + getWidth() / 2 - frm.getWidth() / 2,
                loc.y + getHeight() / 2 - frm.getHeight() / 2);

        desktop.add(frm, 0);
        frm.toFront();

        ch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if (cmd != null) {
                    if (cmd.equals(JFileChooser.CANCEL_SELECTION)) {
                        frm.dispose();
                    } else if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
                        open(ch.getSelectedFile());
                        frm.dispose();
                    }
                }
            }
        });
    }

    /**
     * Opens the specified sound-file and adds it to the editor. <br />
     * 
     * @see #openImportFileChooser()
     * @param f - the file to open.
     */
    private void open(File f) {
        try {
            String name = f.getName().toLowerCase();
            if (name.endsWith(".ogg")) {
                // this should be an ogg file.
                SoundInput in = null;
                try {
                    in = VorbisCodec.decode(new FileInputStream(f));
                    addSound(f.getName(), in);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            } else if (name.endsWith(".jbsp")) {
                inportProject(new FileInputStream(f));
            } else {
                // this should be a wav, aiff, ... file
                AudioInputStream in = null;
                try {
                    in = AudioSystem.getAudioInputStream(f);
                    addSound(f.getName(), SoundInput.fromStream(in));
                } finally {
                    StreamUtils.safeClose(in);
                }
            }
        } catch (IOException io) {
            inform(JOptionPane.ERROR_MESSAGE, "Error while decoding the audio-file!\nError:" + io);
        } catch (UnsupportedAudioFileException ex) {
            inform(JOptionPane.ERROR_MESSAGE, "Can't decode the file: This audio-file isn't supported!\nError: " + ex);
        } catch (OutOfMemoryError mem) {
            inform(JOptionPane.ERROR_MESSAGE, "Java is out of memory: This audio-file is to big for the editor!\nError: " + mem);
        }
    }

    /**
     * Opens a message dialog. <br />
     * 
     * @param type - the type of the message. see: {@link javax.swing.JOptionPane}
     * @param msg - the message to display. 
     */
    private void inform(int type, String msg) {
        JOptionPane.showInternalMessageDialog(this, msg, "Message", type);
    }

    /**
     * Adds the SoundInput to the editor. <br />
     * This <b>doesn't</b> closes the SoundInput!<br />
     * 
     * PROBLEM: This can throw a {@link java.lang.OutOfMemoryError} <br />
     * 
     * @throws java.io.IOException
     * @param snd - the SoundInput
     * @param name - the name of the sound.
     */
    private void addSound(String name, SoundInput src) throws IOException {
        SoundInput in = new AudioFormat16Filter(src, getFormat());
        AudioFormat fmt = in.getFormat();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf, 0, buf.length)) != -1) {
            out.write(buf, 0, len);
        }
        in.close();
        byte[] bytes = out.toByteArray();
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer byteBuf = ByteBuffer.wrap(bytes);
        if (fmt.isBigEndian()) {
            byteBuf.order(ByteOrder.BIG_ENDIAN);
        } else {
            byteBuf.order(ByteOrder.LITTLE_ENDIAN);
        }
        byteBuf.asShortBuffer().get(shorts);

        // try to release memory...
        byteBuf = null;
        bytes = null;
        out = null;
        buf = null;

        Track t = new Track(shorts, (int) getFormat().getFrameRate() / PIX_FOR_SECOND, name == null ? "unnamed" : name);
        tracks.addTrack(new JSoundTrack(t, TRACK_HEIGHT));
    }

    /**
     * PROBLEM: This can throw a {@link java.lang.OutOfMemoryError} <br />
     */
    private SoundInput createMixedAudioData() {
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
                Volume16Filter filter = new Volume16Filter(inp);
                filter.setVolume(track.getVolume());
                mix.addTimed(filter, (long) ((double) t.getX() / PIX_FOR_SECOND * 1000));
            }
        }
        return mix;
    }

    /**
     * Plays the sound tracks in the editor. <br />
     */
    private void play() {
        try {
            player.play(createMixedAudioData());
        } catch (OutOfMemoryError mem) {
            inform(JOptionPane.ERROR_MESSAGE, "Java is out of memory! (try to remove some of the large sound-tracks!)\nError: " + mem);
        }
    }

    /**
     * Returns the desktop pane on which this JSoundEditor is, or null <br />
     * 
     */
    JDesktopPane getDesktop() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JDesktopPane) {
                return (JDesktopPane) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
    private int recordingCounter = 0;

    private void openRecorderDialog() {
        Container desktop = getDesktop();
        if (desktop != null) {
            final JInternalFrame frm = new JInternalFrame("Microphone Recorder");
            frm.setClosable(true);
            frm.setLayout(new BorderLayout());
            final JSoundRecorder p = new JSoundRecorder(getFormat());
            frm.add(p, BorderLayout.CENTER);
            desktop.add(frm, 0);
            JPanel south = new JPanel(new BorderLayout());
            JPanel southEast = new JPanel(new FlowLayout());
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    frm.dispose();
                    p.release();
                }
            });
            southEast.add(cancel);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    AudioFormat fmt = p.getFormat();
                    byte[] data = p.getAudioData();
                    p.release();
                    frm.dispose();
                    short[] samples = new short[data.length / 2];
                    ShortBuffer buf = ShortBuffer.wrap(samples);
                    ByteBuffer dataBuf = ByteBuffer.wrap(data);
                    if (fmt.isBigEndian()) {
                        dataBuf.order(ByteOrder.BIG_ENDIAN);
                    } else {
                        dataBuf.order(ByteOrder.LITTLE_ENDIAN);
                    }
                    buf.put(dataBuf.asShortBuffer());

                    tracks.addTrackLocated(new JSoundTrack(
                            new Track(samples, (int) ((double) getFormat().getSampleRate() / PIX_FOR_SECOND),
                            "Recording - " + (recordingCounter++ + 1)),
                            TRACK_HEIGHT));
                }
            });

            southEast.add(ok);
            south.add(southEast, BorderLayout.EAST);
            frm.add(south, BorderLayout.SOUTH);

            frm.pack();
            frm.setVisible(true);
            Point loc = SwingUtilities.convertPoint(this, getLocation(), desktop);
            frm.setLocation(loc.x + getWidth() / 2 - frm.getWidth() / 2,
                    loc.y + getHeight() / 2 - frm.getHeight() / 2);

        }
    }

    @Override
    public void removeNotify() {
        clean();
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
    private static final String PROJECT_MAGIC = "JBlocks\0SndP\0";
    private static final int KEY_EOF = 0;
    private static final int KEY_NEXT = 1;
    private static final int KEY_TRACKS = 2;
    private static final int KEY_COMPONENTS = 3;

    private void exportProject(OutputStream os) throws IOException {
        try {
            DataOutputStream out = new DataOutputStream(StreamUtils.createBuffered(os));
            out.writeUTF(PROJECT_MAGIC);
            out.writeByte(KEY_TRACKS);

            HashMap<short[], Integer> trackMap = new HashMap<short[], Integer>(50);

            int counter = 0;
            for (Component c : tracks.getComponents()) {
                if (c instanceof JSoundTrack) {
                    Track t = ((JSoundTrack) c).getTrack();
                    short[] smp = t.getSamples();
                    if (!trackMap.containsKey(smp)) {
                        out.writeByte(KEY_NEXT);
                        out.writeInt(smp.length);
                        for (int i = 0; i < smp.length; i++) {
                            out.writeShort(smp[i]);
                        }

                        trackMap.put(smp, counter);
                        counter++;
                    }
                }
            }

            out.writeByte(KEY_COMPONENTS);
            for (Component c : tracks.getComponents()) {
                if (c instanceof JSoundTrack) {
                    out.writeByte(KEY_NEXT);
                    JSoundTrack track = (JSoundTrack) c;
                    Track t = track.getTrack();
                    out.writeUTF(t.getName());
                    out.writeInt(t.getOffset());
                    out.writeInt(t.getEnd());
                    out.writeInt(t.getSamplesPerPixel());
                    out.writeInt(track.getX());
                    out.writeInt(track.getY());
                    out.writeInt(track.getForeground().getRGB());
                    out.writeInt(trackMap.get(t.getSamples()));
                }
            }
            out.writeByte(KEY_EOF);
        } finally {
            StreamUtils.safeClose(os);
        }
    }

    /*
     * WARNING: This has currently a bug...
     */
    private void inportProject(InputStream is) throws IOException {
        try {
            DataInputStream in = new DataInputStream(StreamUtils.createBuffered(is));
            if (!in.readUTF().equals(PROJECT_MAGIC)) {
                throw new IOException("this isn't a JBlocks sound-editor file!");
            }

            HashMap<Integer, short[]> trackMap = new HashMap<Integer, short[]>();

            in.readByte(); // KEY_TRACKS
            byte key;
            int counter = 0;
            while ((key = in.readByte()) == KEY_NEXT) {
                short[] samples = new short[in.readInt()];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = in.readShort();
                }
                trackMap.put(counter, samples);
                counter++;
            }
            while ((key = in.readByte()) == KEY_NEXT) {
                String name = in.readUTF();
                int off = in.readInt();
                int end = in.readInt();
                int spp = in.readInt();
                int x = in.readInt();
                int y = in.readInt();
                int col = in.readInt();
                int samples = in.readInt();

                JSoundTrack track = new JSoundTrack(new Track(new Track(trackMap.get(samples), spp, name), off, end), TRACK_HEIGHT);
                track.setForeground(new Color(col));
                track.setLocation(x, y);
                tracks.add(track);
            }
            tracks.validate();
        } finally {
            StreamUtils.safeClose(is);
        }
    }
}
