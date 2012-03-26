package org.jblocks.gui;

import java.awt.HeadlessException;

import javax.swing.JFrame;

import org.jblocks.JBlocks;

/**
 * 
 * @author TRocket
 * @author ZeroLuck
 * 
 */
public class JBlocksGUI extends JFrame {

    // <global>
    public static final int JBLOCKS = 0;
    public static final int CLASSICSCRATCH = 1;
    public static final int SCRATCHTWO = 2;
    // <member>
    public int layoutstyle;

    /**
     * @throws HeadlessException
     */
    public JBlocksGUI() throws HeadlessException {
        super();
        this.setTitle("JBlocks" + JBlocks.LONGVERSIONNAME);
    }

    /**
     * @return the layoutstyle
     */
    public int getLayoutstyle() {
        return layoutstyle;
    }

    /**
     * @param layoutstyle the layoutstyle to set
     */
    public void setLayoutstyle(int layoutstyle) {
        this.layoutstyle = layoutstyle;
    }
}
