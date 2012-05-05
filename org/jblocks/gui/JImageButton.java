package org.jblocks.gui;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;

public class JImageButton extends AbstractButton {

    private Image img;

    public JImageButton(Image image) {
        setModel(new DefaultButtonModel());

        this.img = image;
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent evt) {
                getModel().setArmed(true);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                getModel().setPressed(true);
                fireActionPerformed(new java.awt.event.ActionEvent(JImageButton.this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
            }
            
            @Override
            public void mouseReleased(MouseEvent evt) {
                getModel().setPressed(false);
            }
            
            @Override
            public void mouseExited(MouseEvent evt) {
                getModel().setArmed(false);
                repaint();
            }
        });
    }
    
    /**
     * Sets the image of this JImageButton. <br />
     * Call {@link #repaint()} to make changes visible. <br />
     * @see #getImage() 
     */
    public void setImage(Image image) {
        this.img = image;
    }

    /**
     * Returns the image of this JImageButton. <br />
     * @see #setImage(java.awt.Image) 
     */
    public Image getImage() {
        return img;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(img.getWidth(null), img.getHeight(null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics grp) {
        Graphics2D g = (Graphics2D) grp;
        if (model.isPressed() && model.isEnabled()) {
            Composite backup = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
            g.drawImage(img, 0, 0, this);
            g.setComposite(backup);
        } else {
            if (!model.isArmed() || !model.isEnabled()) {
                Composite backup = g.getComposite();
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.75f));
                g.drawImage(img, 0, 0, this);
                g.setComposite(backup);
            } else {
                g.drawImage(img, 0, 0, this);
            }
        }
    }
}
