package org.jblocks.soundeditor;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jblocks.utils.SwingUtils;

/**
 *
 * @author ZeroLuck
 */
class VolumeTool extends SoundEditorTool {

    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent evt) {
            Component src = evt.getComponent();
            if (src instanceof JSoundTrack) {
                final JSoundTrack track = (JSoundTrack) src;
                JDesktopPane desktop = SwingUtils.getDesktop(getEditor());
                final JOptionPane optionPane = new JOptionPane();

                JSlider slider = new JSlider();
                slider.setMajorTickSpacing(5);
                slider.setPaintTicks(true);
                slider.setValue((int) (track.getTrack().getVolume() * 50));
                
                ChangeListener changeListener = new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent changeEvent) {
                        JSlider theSlider = (JSlider) changeEvent.getSource();
                        if (!theSlider.getValueIsAdjusting()) {
                            optionPane.setInputValue(new Integer(theSlider.getValue()));
                            track.getTrack().setVolume(((float) theSlider.getValue() / 50));
                            track.repaint();
                        }
                    }
                };

                slider.addChangeListener(changeListener);

                optionPane.setMessage(new Object[]{"Select a value: ", slider});
                optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
               
                
                JInternalFrame frm = optionPane.createInternalFrame(getEditor(), "Volume");
                
                frm.setVisible(true);
                desktop.add(frm, 0);
                frm.requestFocus();
            }
        }
    };

    public VolumeTool(JSoundEditor edt) {
        super(edt);
    }

    @Override
    public void install(JTrackPane p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.addMouseListener(mouseListener);
            }
        }
    }

    @Override
    public void uninstall(JTrackPane p) {
        for (Component c : p.getComponents()) {
            if (c instanceof JSoundTrack) {
                c.removeMouseListener(mouseListener);
            }
        }
    }
}
