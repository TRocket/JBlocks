package org.jblocks.scratch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ObjReader o = new ObjReader(new FileInputStream(new File("C:\\JTest\\test.sb")));
			Object info = o.readInfo();
			if (info instanceof HashMap) {
				HashMap infohash = (HashMap) info;
				System.out.println(infohash);
			}
			o.readObjTable();
			o.readObjTable();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
