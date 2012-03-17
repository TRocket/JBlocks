/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.laf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

/**
 *
 * @author ZeroLuck
 */
public class JBButtonUI extends BasicButtonUI {

    private static Object BUTTON_UI_KEY = "ButtonUI";

    public static ComponentUI createUI(JComponent c) {
        AppContext appContext = AppContext.getAppContext();
        JBButtonUI jbButtonUI =
                (JBButtonUI) appContext.get(BUTTON_UI_KEY);
        if (jbButtonUI == null) {
            jbButtonUI = new JBButtonUI();
            appContext.put(BUTTON_UI_KEY, jbButtonUI);
        }
        return jbButtonUI;
    }
    
    private void paintBorder(Graphics grap, AbstractButton button) {
        Rectangle rect = button.getBounds();
        
        Graphics2D g = (Graphics2D) grap;
        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(5, 5, rect.width - 10, rect.height - 10, 20, 20);
    }
    
    @Override
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();

        paintBorder(g, button);
        
        paint(g, c);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        if (b.isContentAreaFilled()) {
            paintBorder(g, b);
        }
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b,
            Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
        int mnemIndex = b.getDisplayedMnemonicIndex();

        // Draw the Text 
        if (model.isEnabled()) {
            // paint the text normally 
            g.setColor(b.getForeground());
        } else {
            // paint the text disabled
            g.setColor(Color.LIGHT_GRAY);
        }
        SwingUtilities2.drawStringUnderlineCharAt(c, g, text, mnemIndex,
                textRect.x, textRect.y + fm.getAscent());
    } 
}
