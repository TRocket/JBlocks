package org.jblocks.soundeditor;

import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import org.jblocks.JBlocks;

/**
 *
 * This is the default JBlocks-SoundEditor. <br />
 * It uses a SoftwareMixer to mix from different sounds one. <br />
 * 
 * @author ZeroLuck
 * @version 0.1
 * @see org.jblocks.sound.SoftwareMixer
 */
public class JSoundEditor extends JPanel {

    // <global-1>
    static ImageIcon icon_microphone;
    static ImageIcon icon_play;
    static ImageIcon icon_stop;
    static ImageIcon icon_pause;
    static ImageIcon icon_open;
    static ImageIcon icon_synthesizer;
    // <global-2>
    private final int TRACK_HEIGHT = 50;
    // <member>
    private JToolBar tools;
    private JTrackPane tracks;

    static {
        icon_microphone = new ImageIcon(JBlocks.class.getResource("res/microphone.png"));
        icon_play = new ImageIcon(JBlocks.class.getResource("res/play.png"));
        icon_stop = new ImageIcon(JBlocks.class.getResource("res/stop.png"));
        icon_pause = new ImageIcon(JBlocks.class.getResource("res/pause.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_synthesizer = new ImageIcon(JBlocks.class.getResource("res/synthesizer.png"));
    }

    private short[] getRandomIntArray() {
        short[] ints = new short[1000];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = (short) ((Math.random() * 20000) - 10000);
        }
        return ints;
    }

    public JSoundEditor() {
        tools = new JToolBar();
        tracks = new JTrackPane(TRACK_HEIGHT);
        final JScrollPane scroll = new JScrollPane(tracks);

        setLayout(new BorderLayout());
        add(tools, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        tools.add(new JButton(icon_open));
        tools.add(new JSeparator(JSeparator.VERTICAL));

        JButton record = new JButton(icon_microphone);
        record.setToolTipText("<HTML><b>Microphone</b></HTML>");
        tools.add(record);

        JButton synth = new JButton(icon_synthesizer);
        synth.setToolTipText("<HTML><b>Synthesizer</b> - <i>not yet implemented</i></HTML>");
        tools.add(synth);

        for (int i = 0; i < 5; i++) {
            tracks.addTrack(new JSoundTrack(getRandomIntArray(), TRACK_HEIGHT));
        }
    }
}
