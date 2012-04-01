package org.jblocks.painteditor2;

/**
 *
 * @author ZeroLuck
 */
abstract class PaintTool {

    private JPaintEditor edt;

    /**
     * 
     * @throws IllegalArgumentException - if 'edt' is null!
     * @param edt - the owner of this PaintTool.
     */
    public PaintTool(JPaintEditor edt) {
        if (edt == null) {
            throw new IllegalArgumentException("'edt' is null");
        }
        this.edt = edt;
    }

    /**
     * @return - the owner of this PaintTool.
     */
    JPaintEditor getEditor() {
        return edt;
    }

    abstract void install(JPaintCanvas can);

    abstract void uninstall(JPaintCanvas can);
}
