package org.jblocks.scratch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jblocks.scratch.ObjReader.Ref;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class Test {
	static BufferedOutputStream out;
	static OutputStreamWriter write;
	static Object[][] table = null;
	static int indent = 0;
public static void main(String[] args) throws IOException{
	//Reader r = new Reader(new DataInputStream(new FileInputStream(new File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb"))));
	//r.read();
	out = new BufferedOutputStream(new FileOutputStream(new File("C:\\JTest\\log.txt")));
	ObjReader reader= new ObjReader(new FileInputStream(new File("C:\\JTest\\test.sb")));
	write = new OutputStreamWriter(out);
	//reader.readInfo();
	Object[][] info = reader.readObjects();
	Object[][] contents;
	contents = reader.objTable;
	
	table = info;
	for (int i = 0; i < info.length; i++) {
		System.out.println(i);

	}
	for (int i = 0; i < 50; i++) {
		write.write("\n");
	}
	table = contents;
	for (int i = 0; i < contents.length; i++) {
		System.out.println(i);
		
	}
	
//	reader.objTable = info;
//	for (int i = 0; i < info.length; i++) {
//		System.out.println(i);
//		System.out.println(" class:" + info[i][1]);
//		for (int j = 0; j < info[i].length; j++) {
//			if (info[i][j] instanceof ObjReader.Ref) {
//			} else {
//				System.out.println(" field: " + info[i][j].getClass().getName());
//				System.out.println("  fieldvalue: " + info[i][j]);
//				try{
//				Object[] field = (Object[]) info[i][j];
//				System.out.println("inline array");
//				for (int j2 = 0; j2 < field.length; j2++) {
//					System.out.println("  iafield: " + field[j2].getClass().getName());
//					System.out.println("   iafieldvalue: " + field[j2]);
//				}
//				}catch (Exception e) {
//					// TODO: handle exception
//					
//				}
//			}
//			
//		}
//	}
//	System.out.println();
//	System.out.println();
//	System.out.println();
//	System.out.println();
//	System.out.println();
//	System.out.println();
//	System.out.println();
//	reader.objTable = table;
//	for (int i = 0; i < reader.objTable.length; i++) {
//		System.out.println(i);
//		System.out.println(" class:" + reader.objTable[i][1]);
//		for (int j = 0; j < reader.objTable[i].length; j++) {
//			if (reader.objTable[i][j] instanceof ObjReader.Ref) {
//				System.out.println("  field ref: " + reader.objTable[i][j]);
//				System.out.println("   field refid: " + reader.deRef((Ref) reader.objTable[i][j])[1]);
//				System.out.println("   field refvalue: " + reader.deRef((Ref) reader.objTable[i][j])[0]);
//			} else {
//				System.out.println(" field: " + reader.objTable[i][j].getClass().getName());
//				System.out.println("  fieldvalue: " + reader.objTable[i][j]);
//			}
//			
//		}
//		
//	}
//	ProjectBuilder.build(info, table);
//	/*System.out.println(reader.objTable);
//	for (int i = 0; i < reader.objTable.length; i++) {
//		for (int j = 0; j < reader.objTable[i].length; j++) {
//			System.out.println(reader.objTable[i][j]);
//		}
//	}
//	System.out.println(reader.objTable);
//	*/
}


}
