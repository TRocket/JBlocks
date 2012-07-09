package org.jblocks.scratch;

public class ObjStore {
int size;
Object[] contents;

public ObjStore(int size, Object[] contents) {
	super();
	this.size = size;
	this.contents = contents;
}

Object toObject(){
	return this.contents[0];
}

}
