package org.jblocks.spriter;

import java.awt.Image;

/**
 * Container classes for storing the model data. <br />
 * The model-data can be shared between characters. <br />
 * 
 * @author ZeroLuck
 */

class AnimationData {

    final String name;
    final KeyFrameData[] frames;

    public AnimationData(String name, KeyFrameData[] frames) {
        this.name = name;
        this.frames = frames;
    }
}

class KeyFrameData {

    final float duration;
    final FrameData frame;

    public KeyFrameData(float duration, FrameData frame) {
        this.frame = frame;
        this.duration = duration;
    }
}

final class FrameData {

    final String name;
    final SpriteData[] sprites;

    public FrameData(String name, SpriteData[] sprites) {
        this.name = name;
        this.sprites = sprites;
    }
}

final class SpriteData {

    Image image;
    final int color;
    final float opacity;
    final float angle;
    final boolean xFlip;
    final boolean yFlip;
    final float width;
    final float height;
    final float x;
    final float y;

    public SpriteData(Image image, int color, float opacity, float angle,
            boolean xFlip, boolean yFlip, float width, float height, float x,
            float y) {
        this.image = image;
        this.color = color;
        this.opacity = opacity;
        this.angle = angle;
        this.xFlip = xFlip;
        this.yFlip = yFlip;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
}

final class CharacterData {

    final String name;
    final AnimationData[] animations;

    public CharacterData(String name, AnimationData[] animations) {
        this.name = name;
        this.animations = animations;
    }
}
