package org.jblocks.stage;

import java.awt.Image;
import java.util.List;
import org.jblocks.editor.JScriptPane;
import org.jblocks.stage.ImageSprite.Costume;

/**
 * @author ZeroLuck
 */
public class SpriteData {

    private Sprite view;
    private JScriptPane scripts;
    private String name;

    public SpriteData(String name, Sprite view) {
        setName(name);
        this.scripts = new JScriptPane();
        this.view = view;
    }

    /**
     * Returns the <code>JScriptPane</code> for this Sprite. <br />
     * @return the JScriptPane of this Sprite.
     */
    public JScriptPane getScriptPane() {
        return scripts;
    }

    /**
     * Returns the name of this sprite. <br />
     * @see #setName(java.lang.String) 
     * @return the name of this Sprite
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the view of this Sprite which can be displayed
     * on a <code>Stage</code>. 
     * 
     * @return the view of this Sprite. <br />
     */
    public Sprite getView() {
        return view;
    }

    /**
     * Returns a preview image of this Sprite.
     * @return a preview image of this Sprite or null.
     */
    public Image getPreviewImage() {
        if (view instanceof ImageSprite) {
            List<Costume> costumes = ((ImageSprite) view).getCostumes();
            if (!costumes.isEmpty()) {
                return costumes.get(((ImageSprite) view).getCurrentCostume()).getImage();
            }
        }
        return null;
    }

    /**
     * Sets the name of this Sprite. <br />
     * @see #getName() 
     * @param n the new name
     */
    public final void setName(String n) {
        if (n == null) {
            throw new IllegalArgumentException("name is null!");
        }
        this.name = n;
    }
}
