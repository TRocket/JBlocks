package org.jblocks.soundeditor;

/**
 *
 * @author ZeroLuck
 */
public abstract class SoundEditorTool {

    private final JSoundEditor edt;

    /**
     * @throws IllegalArgumentException - if 'e' is null!
     */
    public SoundEditorTool(JSoundEditor e) {
        if (e == null) {
            throw new IllegalArgumentException("'e' is null!");
        }
        edt = e;
    }
    
    /**
     * Returns the JSoundEditor of this tool. <br />
     * 
     * @return - the tool's JSoundEditor field.
     */
    public JSoundEditor getEditor() {
        return edt;
    }
    
    public abstract void install(JTrackPane p);
    
    public abstract void uninstall(JTrackPane p);
}
