package org.jblocks.scratchold;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jblocks.scratchold.ObjReader.Ref;

public class TestOld {
	static BufferedOutputStream out;
	static OutputStreamWriter write;
	static Object[][] table = null;
	static int indent = 0;

	public static void main(String[] args) throws IOException {
		// Reader r = new Reader(new DataInputStream(new FileInputStream(new
		// File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb"))));
		// r.read();
		out = new BufferedOutputStream(new FileOutputStream(new File(
				"C:\\JTest\\log.txt")));
		ObjReader reader = new ObjReader(new FileInputStream(new File(
				"C:\\JTest\\test.sb")));
		write = new OutputStreamWriter(out);
		// reader.readInfo();
		Object[][] info = reader.readObjects();
		Object[][] contents;
		contents = reader.objTable;

		table = info;
		printTable(table[0]);
//		for (int i = 0; i < info.length; i++) {
//			System.out.println(i);
//			if (info[i][0] instanceof BuiltObject) {
//				BuiltObject value = (BuiltObject) info[i][0];
//				if (value.getValue() instanceof Object[]) {
//					Object[] arry = (Object[]) value.getValue();
//					for (int j = 0; j < arry.length; j++) {
//						if (arry[j] instanceof BuiltObject) {
//							System.out.println(((BuiltObject)arry[j]).getScratchType());
//							System.out.println(((BuiltObject)arry[j]).getValue());
//						}
//					
//					}
//					System.out.println("n: " + arry.length);
//				}
//				System.out.println("number of fields " + info[i].length);
//				System.out.println(value.getScratchType());
//				System.out.println(value.getValue());
//			}
//			System.out.println(info[i][0]);
//		}
//		for (int i = 0; i < 50; i++) {
//			write.write("\n");
//		}
//		table = contents;
//		for (int i = 0; i < contents.length; i++) {
//			System.out.println(i);
//
//		}

		// reader.objTable = info;
		// for (int i = 0; i < info.length; i++) {
		// System.out.println(i);
		// System.out.println(" class:" + info[i][1]);
		// for (int j = 0; j < info[i].length; j++) {
		// if (info[i][j] instanceof ObjReader.Ref) {
		// } else {
		// System.out.println(" field: " + info[i][j].getClass().getName());
		// System.out.println("  fieldvalue: " + info[i][j]);
		// try{
		// Object[] field = (Object[]) info[i][j];
		// System.out.println("inline array");
		// for (int j2 = 0; j2 < field.length; j2++) {
		// System.out.println("  iafield: " + field[j2].getClass().getName());
		// System.out.println("   iafieldvalue: " + field[j2]);
		// }
		// }catch (Exception e) {
		// // TODO: handle exception
		//
		// }
		// }
		//
		// }
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// reader.objTable = table;
		// for (int i = 0; i < reader.objTable.length; i++) {
		// System.out.println(i);
		// System.out.println(" class:" + reader.objTable[i][1]);
		// for (int j = 0; j < reader.objTable[i].length; j++) {
		// if (reader.objTable[i][j] instanceof ObjReader.Ref) {
		// System.out.println("  field ref: " + reader.objTable[i][j]);
		// System.out.println("   field refid: " + reader.deRef((Ref)
		// reader.objTable[i][j])[1]);
		// System.out.println("   field refvalue: " + reader.deRef((Ref)
		// reader.objTable[i][j])[0]);
		// } else {
		// System.out.println(" field: " +
		// reader.objTable[i][j].getClass().getName());
		// System.out.println("  fieldvalue: " + reader.objTable[i][j]);
		// }
		//
		// }
		//
		// }
		// ProjectBuilder.build(info, table);
		// /*System.out.println(reader.objTable);
		// for (int i = 0; i < reader.objTable.length; i++) {
		// for (int j = 0; j < reader.objTable[i].length; j++) {
		// System.out.println(reader.objTable[i][j]);
		// }
		// }
		// System.out.println(reader.objTable);
		// */
	}
	
	static void printTable(Object[] tbl) throws IOException {
		for (int i = 0; i < tbl.length; i++) {
			//System.out.println("printing: " + i);
		if (tbl[i] instanceof BuiltObject) {
			System.out.println(((BuiltObject)tbl[i]).getScratchType());
			System.out.println(((BuiltObject)tbl[i]).getValue());
		}
		if (tbl[i] instanceof Object[]) {
			printTable((Object[]) tbl[i]);
		}
		if (tbl[i] instanceof BuiltObject) {
			if (((BuiltObject)tbl[i]).getValue() instanceof Object[]) {
				printTable((Object[]) ((BuiltObject) tbl[i]).getValue());
			}
		}
			
			
		}
	}

}
