/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

/**
 *
 * A hat block. <br />
 * 
 * @author ZeroLuck
 */
public class HatBlock extends AbstrBlock {

    public HatBlock(JScriptPane pane) {
        super(pane);
    }
    
    @Override
    public void drawBorder(Graphics g) {
        Dimension size = getSize();
        g.setColor(Color.ORANGE);
        g.drawRect(1, 1, size.width -3, size.height -3);
    }

    @Override
    public Insets getBorderInsets(int width, int height) {
        return new Insets(2, 2, 2, 2);
    }
}
