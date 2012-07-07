package org.jblocks.scratchold;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Provides methods for reading scratch files
 * @author TRocket
 *
 */
public class Reader {
	public ObjectTable resolve() {
		return null;

	}
	
	public static void read(InputStream file) throws IOException{
		ObjReader reader = new ObjReader(file);
		//read the file
		Object[][] infoTable1 = reader.readObjects();
		Object[][] contentsTable1 = reader.objTable;
		//convert to Serialized Object array
		BuiltObject[][] infoTable = (BuiltObject[][]) infoTable1;
		BuiltObject[][] contentsTable = (BuiltObject[][]) contentsTable1;
		HashMap<String, BuiltObject> info = parseInfo(infoTable[0][0]);
		
		System.out.println("author: " + info.get("author"));
		System.out.println("history: " + info.get("history"));
		System.out.println("language: " + info.get("language"));
		System.out.println("scratch-version: " + info.get("scratch-version"));
		System.out.println("os-version: " + info.get("os-version"));
	
		
	}
	/**
	 * parse an IdentityDictionary to a HashMap
	 * @param infoTable an indentityDictionary (in a {@link BuiltObject}) usally the first object in the info table
	 * @throws IOException if info is not of type indentityDictionary
	 * @return a {@link HashMap} with the project info
	 */
	private static HashMap<String, BuiltObject> parseInfo(BuiltObject infoTable) throws IOException{
		if (infoTable.getScratchType().equals("indentityDictionary") && infoTable.getValue() instanceof BuiltObject[]) {
			BuiltObject[] infoDict = (BuiltObject[]) infoTable.getValue();
			HashMap<String, BuiltObject> info = new HashMap<String, BuiltObject>();
			String currentKey = null;
			BuiltObject currentObject = null;
			for (int i = 0; i < infoDict.length/2; i++) {
				currentKey = (String) infoDict[i].getValue();
				i++;
				currentObject = infoDict[i];
				info.put(currentKey, currentObject);
			}
			return info;
		} else {
			throw new IOException("info is not of type indentityDictionary(file corrupt?)");
		}
		
		
	}
}
