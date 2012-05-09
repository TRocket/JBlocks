package org.jblocks.spriter;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferStrategy;
import java.io.File;

/**
 *
 * @author ZeroLuck
 */
public class Test extends Frame {

    private SpriterCharacter ch;

    public Test() throws Exception {
        super("Test");
        setPreferredSize(new Dimension(600, 400));
        setLocationByPlatform(true);
        setSize(new Dimension(600, 400));
        setVisible(true);

        SpriterManager mng = new SpriterManager();
        mng.load(new File("C:\\JTest\\example hero\\BetaFormatHero.scml"));
        mng.optimize(getGraphicsConfiguration());

        ch = mng.create("first character");
        ch.setAnimation("walk");
        //      ch.setAnimation("idle_healthy");
        ch.setLocation(200, 200);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                ch.setAnimation(ch.getAnimation().equals("walk") ? "idle_healthy" : "walk");
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent evt) {
                ch.setLocation(evt.getX(), evt.getY());
            }
        });

        createBufferStrategy(2);
        BufferStrategy str = getBufferStrategy();

        long start = System.currentTimeMillis();
        long frames = 0;

        mng.startAnimation(ch);
        while (true) {
            String s = "FPS: " + (frames / ((System.currentTimeMillis() - start) / 1000 + 1));
            frames++;

            Graphics2D g = (Graphics2D) str.getDrawGraphics();

            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawString(s, 15, 50);
            ch.paint(g);
            str.show();

            mng.stepAnimation();
            Thread.sleep(1);
        }
    }
    
    public static void main(String[] args) throws Exception {
        Test t = new Test();
    }
}
