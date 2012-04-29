package org.jblocks.editor;

/**
 *
 * @author ZeroLuck
 */
public interface Puzzle {

    void layoutPuzzle();
    
    void removeFromPuzzle(AbstrBlock b);
        
    PuzzleAdapter[] getPuzzleAdapters();
}
