package org.jblocks.scratch.gui;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Images {

	static int decodeInt(DataInputStream in) throws IOException{
		int number = in.readUnsignedByte();
		if (number <= 223) {//simple
			return number;
		}
		if (number <= 254) {
			return (number-224)*256 + in.readUnsignedByte();
		}
		number = in.readInt();
		return number;
		
	}
	
	public static void bitmapFromByteArray(Object bytearray_) throws IOException{
		byte[] bytearray;
		int[] bitmap;
		//if (bytearray_ instanceof Object[]) {
			bytearray = (byte[]) bytearray_;
		//}else throw new IOException("is not a bytearray");
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytearray));
		int size = decodeInt(in);
		System.out.println(size);
		bitmap = new int[size];
		while (in.available()>0) {
			int n = decodeInt(in);
			int code = n & 0x3;
			n = n >> 2;
			System.out.println(code);
			System.out.println("n " + n);
			switch (code) {
			case 0:
				
			case 1:
				int data = in.readUnsignedByte();
				

			default:
				break;
			}
			
		}
	}
	
}
