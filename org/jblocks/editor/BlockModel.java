package org.jblocks.editor;

import org.jblocks.scriptengine.Block;

/**
 *
 * @author ZeroLuck
 */
public class BlockModel {

    private final long id;
    private final Block code;
    private Object content;
    private String blockspec;
    private String category;
    private String type;
    
    private BlockModel(long id, Block code, String spec, String ctg, String type) {
        this.id = id;
        this.code = code;
        this.blockspec = spec;
        this.category = ctg;
        this.type = type;
    }
    
    public String getBlockspec() {
        return blockspec;
    }
    
    public long getID() {
        return id;
    }
    
    public Block getCode() {
        return code;
    }
    
    public Object getContent() {
        return content;
    }
 
    public void setContent(Object o) {
        content = o;
    }
    
    public void setBlockspec(String spec) {
        blockspec = spec;
    }
    
}
