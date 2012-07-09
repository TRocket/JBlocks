package org.jblocks.scratch.gui;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Images {

	int decodeInt(DataInputStream in) throws IOException{
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
	
	void bitmapFromByteArray(Object bytearray_) throws IOException{
		byte[] bytearray;
		if (bytearray_ instanceof byte[]) {
			bytearray = (byte[]) bytearray_;
		}else throw new IOException("is not a bytearray");
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytearray));
		int size = decodeInt(in);
	}
	
}
