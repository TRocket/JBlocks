/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jblocks.laf;

import javax.swing.UIDefaults;
import javax.swing.plaf.basic.BasicLookAndFeel;

/**
 *
 * LookAndFeel for the JBlocks project. <br />
 * @author ZeroLuck
 */
public class JBLookAndFeel extends BasicLookAndFeel {

    @Override
    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
        final String pkg = "org.jblocks.laf.";

        Object[] uiDefaults = {
            "ButtonUI", pkg + "JBButtonUI",
        };

        table.putDefaults(uiDefaults);
    }

    @Override
    public String getName() {
        return "JBLookAndFeel";
    }

    @Override
    public String getID() {
        return "JBLaF";
    }

    @Override
    public String getDescription() {
        return "A LookAndFeel for the JBlocks project.";
    }

    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }

    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
}
