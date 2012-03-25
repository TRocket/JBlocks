package org.jblocks.editor;

import java.awt.Rectangle;

/**
 * Just a container for some values <br />
 * (not very clean design) <br />
 * 
 * @author ZeroLuck
 */
class PuzzleAdapter {

    // <global>
    public static final int TYPE_TOP = 0;
    public static final int TYPE_DOWN = 1;
    // <member>
    final AbstrBlock block;
    final Rectangle bounds;
    final int type;
    AbstrBlock neighbour;

    public PuzzleAdapter(AbstrBlock b, int type) {
        this.block = b;
        this.type = type;
        this.bounds = new Rectangle();
    }
}
