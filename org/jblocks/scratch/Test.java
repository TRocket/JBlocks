package org.jblocks.scratch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jblocks.scratch.ObjReader.Ref;


public class Test {
public static void main(String[] args) throws IOException{
	//Reader r = new Reader(new DataInputStream(new FileInputStream(new File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb"))));
	//r.read();
	ObjReader reader= new ObjReader(new FileInputStream(new File("C:\\JTest\\test.sb")));
	//reader.readInfo();
	Object[][] info = reader.readObjects();
	for (int i = 0; i < info.length; i++) {
		System.out.println("class:" + info[i][1]);
		for (int j = 0; j < info[i].length; j++) {
			System.out.println(" field: " + info[i][j].getClass().getName());
			System.out.println("  fieldvalue: " + info[i][j]);
			
			
		}
	}
	System.out.println();
	System.out.println();
	System.out.println();
	System.out.println();
	System.out.println();
	System.out.println();
	System.out.println();
	for (int i = 0; i < reader.objTable.length; i++) {
		System.out.println(i);
		System.out.println(" class:" + reader.objTable[i][1]);
		for (int j = 0; j < reader.objTable[i].length; j++) {
			if (reader.objTable[i][j] instanceof ObjReader.Ref) {
				System.out.println("  field ref: " + reader.objTable[i][j]);
				System.out.println("   field refid: " + reader.deRef((Ref) reader.objTable[i][j])[1]);
				System.out.println("   field refvalue: " + reader.deRef((Ref) reader.objTable[i][j])[0]);
			} else {
				System.out.println(" field: " + reader.objTable[i][j].getClass().getName());
				System.out.println("  fieldvalue: " + reader.objTable[i][j]);
			}
			
		}
	}
	
	/*System.out.println(reader.objTable);
	for (int i = 0; i < reader.objTable.length; i++) {
		for (int j = 0; j < reader.objTable[i].length; j++) {
			System.out.println(reader.objTable[i][j]);
		}
	}
	System.out.println(reader.objTable);
	*/
}

        

}
