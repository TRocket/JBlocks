package org.jblocks.scratch;

import java.awt.Color;
import java.awt.image.BufferedImage;

class Sprite {

    static final int originX = 241;
    static final int originY = 206;
    
    // <member>
    BufferedImage costume;
    BufferedImage rotatedCostume;
    BufferedImage filteredCostume;
    BufferedImage tempImage;
    double x;
    double y;
    boolean isShowing = true;
    boolean isDraggable = false;
    double alpha = 1.0D;
    double scale = 1.0D;
    double rotationDegrees = 90.0D;
    int rotationstyle;
    int rotationX;
    int rotationY;
    int offsetX;
    int offsetY;
    Bubble bubble;
    boolean penDown;
    int lastPenX;
    int lastPenY;
    Color penColor = new Color(0, 0, 255);
    int penSize = 1;
    double penHue;
    double penShade;
    boolean filterChanged = false;
    double color;
    double brightness;
}
