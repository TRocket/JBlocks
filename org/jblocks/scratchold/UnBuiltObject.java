package org.jblocks.scratchold;

import java.io.InvalidObjectException;

public class UnBuiltObject {
private int objectId = 0;
private String Squeakname = null;
private Object[] params = null;

/**
 * get this unbuilt Object's id
 * @return the object's id
 */
public int getObjectId() {
	return objectId;
}

public BuiltObject build() throws InvalidObjectException{
	this.params = fixParams(this.params);
	System.out.println("building object id: " + this.objectId + " name: " + this.Squeakname);
	return null;
	
}

public UnBuiltObject(int objectId, String squeakname, Object[] params) {
	super();
	this.objectId = objectId;
	Squeakname = squeakname;
	this.params = params;
}


/**
 * @return the squeakname
 */
public String getSqueakname() {
	return Squeakname;
}

/**
 * @return the params
 */
public Object[] getParams() {
	return params;
}


private BuiltObject[] fixParams(Object[] params) throws InvalidObjectException{
	//check
	for (int i = 0; i < params.length; i++) {
		if (params[i] instanceof BuiltObject) continue;//if its already built, just continue
		if (params[i] instanceof UnBuiltObject) {//if it isn't, build it!
			UnBuiltObject object = (UnBuiltObject) params[i];
			params[i] = object.build();//TODO warning! infinite recursion could occur here
			continue;
		}
		throw new InvalidObjectException("Object is not a squeak object");
	}
	//cast
	BuiltObject[] castedParams = new BuiltObject[params.length];
	for (int i = 0; i < params.length; i++) {
		castedParams[i] = (BuiltObject) params[i];
	}
	return castedParams;
	
}
}
