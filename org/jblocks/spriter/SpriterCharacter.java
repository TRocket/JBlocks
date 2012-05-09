package org.jblocks.spriter;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public final class SpriterCharacter {

    final CharacterData character;
    private float xPos;
    private float yPos;
    private int currentFrame = 0;
    private AnimationData currentAnimation;

    SpriterCharacter(CharacterData character) {
        this.character = character;
    }

    /**
     * Returns the bounds of the Sprite. <br />
     * The bounds is independent of the displayed frame. <br />
     */
    public Rectangle calculateBounds() {
        Rectangle r = new Rectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
        Rectangle spriteRect = new Rectangle();
        for (SpriteData s : getSprites()) {
            AffineTransform tx = createTransform(s, false);
            spriteRect.width = s.image.getWidth(null);
            spriteRect.height = s.image.getHeight(null);

            Rectangle bounds = tx.createTransformedShape(spriteRect).getBounds();
            r.x = Math.min(bounds.x, r.x);
            r.y = Math.min(bounds.y, r.y);
            r.width = Math.max(bounds.x + bounds.width, r.x + r.width) - r.x;
            r.height = Math.max(bounds.y + bounds.height, r.y + r.height) - r.y;
        }
        return r;
    }

    /**
     * Sets the current animation. <br />
     * The current frame will 0. <br />
     * 
     * @throws IllegalArgumentException if the animation doesn't exists
     * @param name the name of the animation
     */
    public void setAnimation(String name) {
        for (AnimationData an : character.animations) {
            if (an.name.equals(name)) {
                currentAnimation = an;
                currentFrame = 0;
                return;
            }
        }
        throw new IllegalArgumentException("animation for the name '" + name + "' doesn't exists!");
    }

    /**
     * Returns the name of the current animation. <br />
     */
    public String getAnimation() {
        if (currentAnimation == null) {
            return null;
        }

        return currentAnimation.name;
    }

    /**
     * Sets the location of this Sprite. <br />
     */
    public void setLocation(float x, float y) {
        xPos = x;
        yPos = y;
    }

    /**
     * @return the character's name
     */
    public String getName() {
        return character.name;
    }

    /**
     * Changes the current frame by one. <br />
     */
    public void nextFrame() {
        currentFrame = (currentFrame + 1) % currentAnimation.frames.length;
    }

    private AffineTransform createTransform(SpriteData s, boolean canBeNull) {
        final Image img = s.image;
        final float h = s.height;
        final float w = s.width;
        final float scaleX = w / img.getWidth(null) * (s.xFlip ? -1 : 1);
        final float scaleY = h / img.getHeight(null) * (s.yFlip ? -1 : 1);
        if (canBeNull && scaleX == 1 && scaleY == 1 && s.angle == 0) {
            return null;
        }
        if (s.xFlip || s.yFlip)
            System.out.println("yo");
        AffineTransform tx = new AffineTransform();
        tx.translate(xPos, yPos);
        if (scaleX != 1 || scaleY != 1) {
            tx.scale(scaleX, scaleY);
        }
        tx.translate(s.x, s.y);
        if (s.angle != 0) {
            tx.rotate(Math.toRadians(-s.angle));
        }
        return tx;
    }

    private SpriteData[] getSprites() {
        AnimationData animation = currentAnimation;
        FrameData frame = animation.frames[currentFrame].frame;
        SpriteData[] sprites = frame.sprites;
        return sprites;
    }

    /**
     * Paints the Sprite in the specified <code>Graphics</code> at the
     * Sprite's location. <br />
     */
    public void paint(Graphics2D g) {
        final SpriteData[] sprites = getSprites();
        final Composite backup = g.getComposite();

        for (SpriteData s : sprites) {
            final Image img = s.image;
            final AffineTransform tx = createTransform(s, true);
            if (s.opacity < 100) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, s.opacity / 100));
            }
            if (tx == null) {
                g.drawImage(img, (int) (xPos + s.x), (int) (yPos + s.y), null);
            } else {
                g.drawImage(img, tx, null);
            }
            if (s.opacity < 100) {
                g.setComposite(backup);
            }
        }
    }

    AnimationData getCurrentAnimation() {
        return currentAnimation;
    }
    
    KeyFrameData getCurrentKeyFrame() {
        return currentAnimation.frames[currentFrame];
    }
}
