package org.jblocks.editor;

/**
 *
 * @author ZeroLuck
 */
interface Puzzle {

    void layoutPuzzle();
    
    void removeFromPuzzle(AbstrBlock b);
    
    PuzzleAdapter[] getPuzzleAdapters();
}
