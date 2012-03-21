package org.jblocks;

import org.jblocks.blockloader.BlockLoader;
import org.jblocks.blocks.whengreenflagpressedjhatblock.WhenGreenFlagPressedJHatBlock;

/**
 * 
 * Main-Class for Desktop-Application.
 * 
 * @author TRocket
 * @author ZeroLuck
 */
public class JBlocksLauncher {

    /**
     * this is the standalone main method
     * @param args the command line args
     */
    public static void main(String[] args) {
        // TODO run JBlocks
        JBlocks jb = new JBlocks();
        jb.init();
        BlockLoader bl = new BlockLoader();
        bl.load(WhenGreenFlagPressedJHatBlock.class.getResourceAsStream("block.xml"));

    }
}
