package org.jblocks.editor;

import java.security.SecureRandom;
import java.util.Random;
import org.jblocks.scriptengine.Block;

/**
 * The BlockModel is storing informations about blocks. <br />
 * A BlockModel can be shared between more than one <code>AbstrBlock</code>. <br />
 * Each model has an ID.
 * 
 * @see AbstrBlock
 * @author ZeroLuck
 */
public class BlockModel {

    public static final long NOT_AN_ID = 0;
    private final long id;
    private final String category;
    private final String type;
    private String content;
    private String blockspec;
    private Block code;

    private BlockModel(long id, String spec, String ctg, String type) {
        this.id = id;
        this.blockspec = spec;
        this.category = ctg;
        this.type = type;
    }

    /**
     * Returns the <code>syntax</code> of this BlockModel. <br />
     * The <code>syntax</code> is the <code>String</code> representation of 
     * the block's label. <br />
     * You can use this to create a new instance of the block with 
     * {@link org.jblocks.editor.BlockFactory}. <br />
     * 
     * @see org.jblocks.editor.BlockFactory
     * @see #setSyntax(java.lang.String) 
     * @return the block's syntax
     */
    public String getSyntax() {
        return blockspec;
    }

    /**
     * Sets the <code>syntax</code> of this BlockModel. <br />
     * The <code>syntax</code> is the <code>String</code> representation of 
     * the block's label. <br />
     * 
     * @see #getSyntax() 
     * @see BlockFactory
     */
    public void setSyntax(final String spec) {
        blockspec = spec;
    }

    /**
     * Returns the <code>category</code> of this BlockModel. <br />
     * This may return <code>null</code>.
     * 
     * @see org.jblocks.editor.JBlockEditor
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the <code>type</code> of this BlockModel. <br />
     * This can be "reporter", "command", "cap," "hat", etc.. <br />
     * 
     * @see org.jblocks.editor.BlockFactory
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the <code>ID</code> of this BlockModel. <br />
     * Each <code>BlockModel</code> should have an unique ID. <br />
     * If this <code>BlockModel</code> has no ID this method will return {@link #NOT_AN_ID}.
     * 
     * @return the ID of this BlockModel
     * @see #NOT_AN_ID
     */
    public long getID() {
        return id;
    }

    /**
     * Returns the executable Block. <br />
     * This may return <code>null</code>.
     * 
     * @see #setCode(org.jblocks.scriptengine.Block) 
     */
    public Block getCode() {
        return code;
    }

    /**
     * Sets the executable Block. <br />
     * 
     * @see #getCode() 
     * @param b the executable block for this model, null is allowed.
     */
    public void setCode(final Block b) {
        code = b;
    }

    /**
     * Returns the <code>content</code> of this BlockModel. <br />
     * This method may return <code>null</code>.
     * 
     * @see #setContent(java.lang.Object) 
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the <code>content</code> of this BlockModel. <br />
     * The <code>content</code> can be used to store more 
     * informations about the block. <br />
     * 
     * @see #getContent() 
     * @see o the new content, null is allowed
     */
    public void setContent(final String o) {
        content = o;
    }
    private static Random r;

    /**
     * Creates a new BlockModel and calls {@link #setCode(org.jblocks.scriptengine.Block) } <br />
     * See {@link #createModel(java.lang.String, java.lang.String, java.lang.String) }
     * 
     * @see #createModel(java.lang.String, java.lang.String, java.lang.String) 
     */
    public static BlockModel createModel(final String type, final String category, final String syntax, final Block code) {
        BlockModel model = new BlockModel(code.getID(), syntax, category, type);
        model.setCode(code);
        return model;
    }

    /**
     * Creates a <code>new BlockModel</code>. <br />
     * The model will have an ID. <br />
     * 
     * @param type the type of the block ("reporter", "cap", "command" etc...)
     * @param category the category of the block
     * @param syntax the syntax of the block label. (see BlockFactory)
     * @return the created BlockModel
     */
    public static BlockModel createModel(final String type, final String category, final String syntax) {
        return new BlockModel(createID(), syntax, category, type);
    }

    /**
     * See {@link #createModel(java.lang.String, java.lang.String, java.lang.String)} <br />
     * This method will create a new BlockModel without an ID and no category. <br />
     */
    public static BlockModel createPreviewModel(final String type, final String syntax) {
        return new BlockModel(NOT_AN_ID, syntax, null, type);
    }

    /**
     * Creates a <code>new BlockModel</code>. <br />
     * The moedl will have the specified ID. <br />
     * 
     * @param type the type of the block ("reporter", "cap", etc)
     * @param syntax the syntax of the block label (see BlockFactory)
     * @param category the category of block
     * @param ID the ID of the block
     * @return the created BlockModel
     */
    public static BlockModel createModel(final String type, final String category, final String syntax, final long ID) {
        return new BlockModel(ID, syntax, category, type);
    }

    /**
     * Creates a new ID. <br />
     */
    public static synchronized long createID() {
        if (r == null) {
            r = new SecureRandom();
        }
        long id = 0;
        while (id == NOT_AN_ID) {
            id = r.nextLong();
        }
        return id;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * Two <code>BlockModel</code>s are equal if the IDs are the same
     * and the IDs aren't {@link #NOT_AN_ID}.
     * 
     * <hr />
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof BlockModel)) {
            return super.equals(o);
        }

        BlockModel model = (BlockModel) o;
        if (model.id == NOT_AN_ID) {
            return false;
        }

        return model.id == id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BlockModel [syntax=" + getSyntax() + ", type=" + getType() + ", category=" + category + ", code=" + code
                + (content == null ? "" : ", content=" + content) + "]";
    }
}
