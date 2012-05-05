package org.jblocks.scratch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jblocks.scratch.ObjReader.LContext;


public class Test {
public static void main(String[] args) throws IOException{
	//Reader r = new Reader(new DataInputStream(new FileInputStream(new File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb"))));
	//r.read();
	ObjReader reader= new ObjReader(new FileInputStream(new File("C:\\JTest\\Chat.py.sb")));
	try {
		
		LContext context = new LContext();
		
		System.out.println(reader.readObjects(context));
	} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}
	System.out.println(reader.objTable);
	for (int i = 0; i < reader.objTable.length; i++) {
		for (int j = 0; j < reader.objTable[i].length; j++) {
			System.out.println(reader.objTable[i][j]);
		}
	}
	System.out.println(reader.objTable);
}
}
