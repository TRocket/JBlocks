package org.jblocks.soundeditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioFormat;
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
    // <global-2>
    private final int TRACK_HEIGHT = 50;
    private final int TRACK_CNT = 6;
    private final int PIX_FOR_SECOND = 50;
    // <member>
    private JToolBar tools;
    private JTrackPane tracks;
    private SoundEditorTool tool;
    private AudioFormat editorFormat = new AudioFormat(440100, 16, 1, true, false);

    static {
        icon_microphone = new ImageIcon(JBlocks.class.getResource("res/microphone.png"));
        icon_play = new ImageIcon(JBlocks.class.getResource("res/play.png"));
        icon_stop = new ImageIcon(JBlocks.class.getResource("res/stop.png"));
        icon_pause = new ImageIcon(JBlocks.class.getResource("res/pause.png"));
        icon_open = new ImageIcon(JBlocks.class.getResource("res/open.png"));
        icon_synthesizer = new ImageIcon(JBlocks.class.getResource("res/synthesizer.png"));
        icon_trash = new ImageIcon(JBlocks.class.getResource("res/trash.png"));
        icon_cutout = new ImageIcon(JBlocks.class.getResource("res/cut-out.png"));
    }

    private short[] getRandomIntArray() {
        short[] ints = new short[440100 * 10];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = (short) ((Math.random() * 20000) - 10000);
        }
        return ints;
    }

    public JSoundEditor() {
        tools = new JToolBar();
        tracks = new JTrackPane(TRACK_HEIGHT, TRACK_CNT);
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


        Track t = new Track(getRandomIntArray(), 440100 / PIX_FOR_SECOND, "Hello World");
        for (int i = 0; i < 3; i++) {
            tracks.addTrack(new JSoundTrack(t, TRACK_HEIGHT));
        }

        // <tools>
        addTool(new CutOutTool(this), icon_cutout, "cut out");
        // </tool>

        scroll.setColumnHeaderView(new TimeColumnView(PIX_FOR_SECOND));
        scroll.setRowHeaderView(new TrackRowView(TRACK_HEIGHT, TRACK_CNT));
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
