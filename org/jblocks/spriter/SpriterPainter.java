package org.jblocks.spriter;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.FileInputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.jblocks.gui.JBlocksPane;

/**
 *
 * This will be a class for drawing Spriter sprites. <br />
 * Currently in devolpment... <br />
 * 
 * @author ZeroLuck
 */
public class SpriterPainter {

    private final SCML model;

    public SpriterPainter(final SCML model) {
        this.model = model;
    }

    public void paintSprite(final Graphics2D g, final Sprite s) {
        final Composite compositeBackup = g.getComposite();
        final AffineTransform transformBackup = g.getTransform();
        g.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                s.getOpacity() / 100));

        final Image img = model.getImages().get(s.getImage());
        final int newW = (int) s.getWidth();
        final int newH = (int) s.getHeight();
        final int x = (int) s.getX();
        final int y = (int) s.getY();

        g.rotate(Math.toRadians(s.getAngle()), x, y);
        g.translate(x, y);

        g.drawImage(img, 0, 0, newW * (s.isxFlip() ? -1 : 1), newH * (s.isyFlip() ? -1 : 1), null);

        g.setTransform(transformBackup);
        g.setComposite(compositeBackup);
    }

    public void paintFrame(Graphics2D g, Frame f) {
        System.out.println(f.getName());
        for (Sprite s : f.getSprites()) {
            paintSprite(g, s);
        }
    }

    public SCML getModel() {
        return model;
    }

    // this class will be removed later
    private static class TestViewer extends JComponent {

        private SpriterPainter painter;

        public TestViewer(SCML m) {
            painter = new SpriterPainter(m);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.translate(200, 200);

            SCML model = painter.getModel();
            Frame frm = null;
            for (Frame f : model.getFrames()) {
                if (f.getSprites().length != 0 && Math.random() > 0.85) {
                    frm = f;
                    break;
                }
            }
            if (frm != null) {
                painter.paintFrame((Graphics2D) g, frm);
            }
        }
    }

    // this function will be removed later
    public static void main(String[] args) throws Exception {
        JBlocksPane.setLaF();


        SCML scml = SCMLReader.readSCMLFile(new FileInputStream("C:\\JTest\\example hero\\BetaFormatHero.scml"), "C:\\JTest\\example hero");
        JFrame frm = new JFrame("SpriterPainter : Test");
        frm.setLocationByPlatform(true);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(600, 500);
        frm.setLayout(new BorderLayout());
        frm.add(new TestViewer(scml), BorderLayout.CENTER);

        frm.setVisible(true);

    }
}
