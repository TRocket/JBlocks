package org.jblocks.scratch.gui;

import java.awt.Color;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

public class Builder {
	
	static final byte[] macRomanToISOLatin = { -60, -59, -57, -55, -47, -42, -36, -31, -32, -30, -28, -29, -27, -25, -23, -24, -22, -21, -19, -20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4, -122, -80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128, -58, -40, -127, -79, -118, -115, -91, -75, -114, -113, -112, -102, -99, -86, -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, -78, -85, -69, -123, -96, -64, -61, -43, -116, -100, -106, -105, -109, -108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67, -121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50, -49, -52, -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, -81, -41, -35, -34, -72, -16, -3, -2 };
	
	static Object buildNil(DataInputStream in, Reader context){
		return null;
	}
	
	static Object buildTrue(DataInputStream in, Reader context){
		return true;
	}

	static Object buildFalse(DataInputStream in, Reader context){
		return false;
	}
	
	static Object buildInt(DataInputStream in, Reader context) throws IOException{
		return in.readInt();
	}

	static Object buildShort(DataInputStream in, Reader context) throws IOException{
		return in.readShort();
	}
	
	static Object buildLargePosInt(DataInputStream in, Reader context) throws IOException{
		double d1 = 0.0D;
	    double d2 = 1.0D;
	    int fisrt = in.readShort();
	    for (int i = 0; i < fisrt; i++) {
	      int second = in.readUnsignedByte();
	      d1 += d2 * second;
	      d2 *= 256.0D;
	    }
	    
	    return new Double(d1);
	}
	
	static Object buildLargeNegInt(DataInputStream in, Reader context) throws IOException{
		double d1 = 0.0D;
	    double d2 = 1.0D;
	    int fisrt = in.readShort();
	    for (int i = 0; i < fisrt; i++) {
	      int second = in.readUnsignedByte();
	      d1 += d2 * second;
	      d2 *= 256.0D;
	    }
	    d1 = -d1;
	    return new Double(d1);
	}
	
	static Object buildFloat(DataInputStream in, Reader context) throws IOException{
		return in.readDouble();
	}
	
	static Object buildSymol(DataInputStream in, Reader context) throws IOException{
		byte[] data;
		int size = in.readInt();
	    in.read(data = new byte[size]);
	    for (int i = 0; i < size; i++) {
	      if (data[i] >= 0) continue; data[i] = macRomanToISOLatin[(data[i] + 128)];
	    }
	    try {
	      return new String(data, "ISO-8859-1");
	    } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
	      return new String(data);
	    }
	}
	
	static Object buildByteArray(DataInputStream in, Reader context) throws IOException{
		byte[] data;
		int size = in.readInt();
		System.out.println("byte array size " + size);
	    in.read(data = new byte[size]);
	    DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream((byte[])data));
	    for (int i = 0; i < data.length; i++) {
			System.out.println(localDataInputStream.readUnsignedByte());
		}
	    return data;
	}
	
	static Object buildSoundBuffer(DataInputStream in, Reader context) throws IOException{
		byte[] data;
		int size = in.readInt();
	    in.read(data = new byte[size*2]);
	    return data;
	}
	
	static Object buildBitMap(DataInputStream in, Reader context) throws IOException{
		int size = in.readInt();
		int[] data = new int[size];
	   for (int i = 0; i < size; i++) {
		data[i] = in.readInt();
	}
	    return data;
	}
	
	
	static Object buildUTF8(DataInputStream in, Reader context) throws IOException{
		byte[] data;
		int size = in.readInt();
	     in.read(data = new byte[size]);
	     try {
	       return new String(data, "UTF-8");
	     } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
	       return new String(data);
	     }
	}
	 
	
	static Object buildIdentSet(DataInputStream in, Reader context) throws IOException{
		int size = in.readInt();
	     Hashtable<Integer, Object> set = new Hashtable<>(size);
	     for (int i = 0; i < size; i++){
	    	 Object o =  context.readObj();
	    	 set.put(i, o);
	    	 };
	     return set;
	}
	 
	static Object buildDict(DataInputStream in, Reader context) throws IOException{
		int size = in.readInt();
	     Hashtable<Object, Object> set = new Hashtable<>(size*2);
	     for (int i = 0; i < size; i++){set.put(context.readObj(), context.readObj());};
	     return set;
	}
	
	static Object buildColor(DataInputStream in, Reader context) throws IOException{
		int data = in.readInt();
	    int n = 255;
	    return new Color(data >> 22 & 0xFF, data >> 12 & 0xFF, data >> 2 & 0xFF, n);
	}
	
	static Object buildTransColor(DataInputStream in, Reader context) throws IOException{
		int data = in.readInt();
	    int n = 255;
	    n = in.readUnsignedByte();
	    return new Color(data >> 22 & 0xFF, data >> 12 & 0xFF, data >> 2 & 0xFF, n);
	}
	
	static Object buildPoint(DataInputStream in, Reader context) throws IOException{
		Object x = context.readObj();
	    Object y = context.readObj();
	    return new UnBuiltPoint(x, y);
	}
	
	static Object buildRectangle(DataInputStream in, Reader context) throws IOException{
		Object x = context.readObj();
	    Object y = context.readObj();
	    Object w = context.readObj();
	    Object h = context.readObj();
	    return new UnBuiltPoint(x, y);
	}
	
	static Object buildRef(DataInputStream in, Reader context) throws IOException{
		 int pos = in.readUnsignedByte() << 16;
	       pos += (in.readUnsignedByte() << 8);
	       pos += in.readUnsignedByte();
	    return new Ref(pos);
	}
	
	static Object buildColorForm(DataInputStream in, Reader context) throws IOException{
		return new UnBuiltColorForm(context.readObj(), context.readObj(), context.readObj(), context.readObj(), context.readObj(), context.readObj())
;
	}
	
	
}
