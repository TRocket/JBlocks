package org.jblocks.scratch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {
public static void main(String[] args){
	try {
		Reader r = new Reader(new FileInputStream(new File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb")));
		//r.readObjects();
		System.out.println(r.readInfo());
		r.printit(r.readObj());
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
