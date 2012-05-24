package org.jblocks.scratch;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import org.jblocks.gui.JImagePanel;

public class ProjectBuilder {

    public static ScratchProject build(Object[][] info, Object[][] contents) throws IOException {
        String[] history;
        BufferedImage thumb = null;


        for (int i = 0; i < info.length; i++) {
            if ((Integer) info[i][1] == 9) {
                String value = (String) info[i][0];
                System.out.println("string val: " + value);
                if (value.equals("thumbnail")) {
                    System.out.println("thumb");
                    //try to read the image
                    i++;
                    if ((Integer) info[i][1] == 35) {
                        BufferedImage img = (BufferedImage) info[i][0];
                        System.out.println(img.getColorModel());
                        thumb = img;
                    } else {
                        throw new IOException("tumbnail image id not35");
                    }

                }
            } else {
            }
        }
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JImagePanel jip = new JImagePanel(thumb);
        frame.add(jip);
        frame.pack();
        frame.setVisible(true);
        return null;

    }
}
