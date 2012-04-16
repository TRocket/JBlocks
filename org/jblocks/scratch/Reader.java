package org.jblocks.scratch;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



public class Reader extends Objects{
DataInputStream in;
	public Reader(DataInputStream in){
		this.in = in;
	}
	
	public void read() throws IOException{
		byte[] header = new byte[10];
		in.read(header);
		if (new String(header).equals("ScratchV01\n") || new String(header).equals("ScratchV02\n")) {
			throw new IOException("incorrect header(file may be damaged or corrupt?");
		}
		System.out.println(new String(header));
		
		readObjTable();
		
	}
	
	HashMap<String, Object> readObjTable() throws IOException{
		System.out.println(in.readInt());
		int length;
		byte[] str = new byte[4];
		in.read(str);
		if (!new String(str).equals("ObjS")) {
			throw new IOException();
		}
		if (in.readByte() != 1) {
			throw new IOException();
		}
		in.read(str);
		if (!new String(str).equals("Stch")) {
			throw new IOException();
		}
		if (in.readByte() != 1) {
			throw new IOException();
		}
		length = in.readByte();
		
		System.out.println(length);
		ArrayList<Object> objects;
		length =in.readInt();
		System.out.println(in.readByte());
		for (int i = 0; i < 1000; i++) {
			System.out.println(in.readByte() + " ");
		}

		
		
		return null;
		
	}
	
	public Object readObject(){
		return null;
		
	}
	
	
}
