package org.jblocks.stage;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author ZeroLuck
 */
public abstract class Sprite {

    private Point.Float location = new Point.Float(0, 0);
    private Object contents;
    private boolean visible = true;
    private float opacity = 1F;
    private Stage stage;

    public abstract Rectangle getClipBounds();

    public abstract void paint(Graphics2D g);

    public abstract boolean contains(int x, int y);

    public void addNotify(Stage s) {
        this.stage = s;
    }

    public Point.Float getLocation() {
        return location;
    }

    public void repaint() {
        stage.repaint(this);
    }

    public void delete() {
        stage.remove(this);
    }

    public void setLocation(Point.Float p) {
        this.location.x = p.x;
        this.location.y = p.y;
    }
    
    public void setLocation(float x, float y) {
        this.location.x = x;
        this.location.y = y;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setRenderingContents(Object o) {
        this.contents = o;
    }

    public Object getRenderingContents() {
        return this.contents;
    }
}
