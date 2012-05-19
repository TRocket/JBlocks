package org.jblocks.stage;

import java.awt.Dimension;

/**
 *
 * @author ZeroLuck
 */
public interface Stage {

    public void repaint(Sprite s);

    public void add(Sprite s);

    public void remove(Sprite s);
    
    public Dimension getStageSize();
}
