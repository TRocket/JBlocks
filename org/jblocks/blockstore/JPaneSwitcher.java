package org.jblocks.blockstore;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author ZeroLuck
 */
class JPaneSwitcher extends JPanel {

    private Component currentDisplayed;
    private Component switching;
    private Timer timer;
    private float currentSwitch = 0;

    private void switchTo() {
        currentSwitch = 0;
        timer = new javax.swing.Timer(50, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                tick();
            }
        });
        timer.start();
    }
    
    public void setView(Component c) {
        if (switching != null)
            return;
        
        if (currentDisplayed != null) {
            switching = c;
            add(c);
            doLayout();
            switchTo();
        } else {
            currentDisplayed = c;
            add(currentDisplayed);
        }
    }
    
    public Component getView() {
        return switching == null ? currentDisplayed : switching;
    }
    
    @Override
    public void doLayout() {
        for (Component c : getComponents()) {
            c.setSize(getSize());
            c.setLocation(0, 0);
        }
    }

    private void tick() {
        Dimension size = getSize();
        currentDisplayed.setLocation((int) (size.width * currentSwitch), 0);
        switching.setLocation((int) (size.width * currentSwitch - size.width), 0);

        currentSwitch += 0.05;
        if (currentSwitch >= 1) {
            timer.stop();
            remove(currentDisplayed);
            currentDisplayed = switching;
            switching = null;
            invalidate();
            validate();
            repaint();
        }
    }
}
