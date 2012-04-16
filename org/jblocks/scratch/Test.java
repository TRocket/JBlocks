package org.jblocks.scratch;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class Test {
public static void main(String[] args) throws IOException{
	Reader r = new Reader(new DataInputStream(new FileInputStream(new File("C:\\Users\\Thomas\\Documents\\Scratch Projects\\Chat.py.sb"))));
	r.read();
}
}
