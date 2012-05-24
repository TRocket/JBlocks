package org.jblocks.stage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZeroLuck
 */
public class ImageSprite extends Sprite {

    private int costume = 0;
    private List<Costume> images = new ArrayList<Costume>();

    public void addCostume(String name, Image img) {
        images.add(new Costume(name, img));
    }

    public List<Costume> getCostumes() {
        return images;
    }
    
    public int getCurrentCostume() {
        return costume;
    }

    @Override
    public Rectangle getClipBounds() {
        Point.Float loc = getLocation();
        Image img = images.get(costume).img;
        return new Rectangle((int) loc.x - img.getWidth(null) / 2, (int) loc.y - img.getHeight(null) / 2,
                img.getWidth(null), img.getHeight(null));
    }

    @Override
    public void paint(Graphics2D g) {
        Point.Float loc = getLocation();
        Image img = images.get(costume).img;
        g.setColor(java.awt.Color.LIGHT_GRAY);
        g.draw(getClipBounds().getBounds2D());
        g.drawImage(img, (int) loc.x - img.getWidth(null) / 2, (int) loc.y - img.getHeight(null) / 2, null);
    }

    @Override
    public boolean contains(int x, int y) {
        return getClipBounds().contains(x, y);
    }

    public static class Costume {

        private String name;
        private Image img;

        public Costume(String name, Image img) {
            this.name = name;
            this.img = img;
        }

        public String getName() {
            return name;
        }

        public Image getImage() {
            return img;
        }
    }
}
