package org.jblocks.scratch.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import sun.org.mozilla.javascript.internal.Decompiler;

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
	
	public static int[] bitmapFromByteArray(Object bytearray_) throws IOException{
		byte[] bytearray;
		int[] bitmap;
		//if (bytearray_ instanceof Object[]) {
			bytearray = (byte[]) bytearray_;
		//}else throw new IOException("is not a bytearray");
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytearray));
		int size = decodeInt(in)*4;
		System.out.println("size will be " + size);
		bitmap = new int[size];
		int bitmapwrites = 0;
		int j = 0;
		while (in.available()>0) {
			
			int n = decompressDecodeInt(in);
			int code = n & 0x3;
			n = n >> 2;
		
			System.out.println(code);
			System.out.println("n " + n);
			int data;
			switch (code) {
			case 0:
				System.out.println("SKIPPED " + n + "bytes");
				bitmapwrites = bitmapwrites + n*8;j=j+n;
			case 1:
				data = in.readUnsignedByte();
				//System.out.println(data);
				data = data << 24;
				//System.out.println(data);
				data = data | (data << 16);
				//System.out.println(data);
				data = data | (data << 8);
				//System.out.println(data);
				data = data | data;
				System.out.println("1         " + data);
				for (int i = 0; i < n; i++) {
					
						bitmap[i+j] = data; bitmapwrites++;j++;
				
					
				}
				break;
			case 2:
				data = 0;
				data = in.readUnsignedByte() << 24;
				data |= in.readUnsignedByte() << 16;
				data |= in.readUnsignedByte() << 8;
				data |= in.readUnsignedByte();
				System.out.println("2          " + data);
				for (int i = 0; i < n; i++) {
					bitmap[i+j] = data; bitmapwrites++;j++;
				}
			case 3:
				for (int i = 0; i < n; i++) {
					data = 0;
					data = in.readUnsignedByte() << 24;
					data |= in.readUnsignedByte() << 16;
					data |= in.readUnsignedByte() << 8;
					data |= in.readUnsignedByte();
					System.out.println("3          " + data);
					
						bitmap[i+j] = data; bitmapwrites = bitmapwrites + 4;j++;
				}
			default:
				break;
			}
			System.out.println(bitmapwrites);
		}
		System.out.println(bitmapwrites);
		for (int i = 1; i < bitmap.length; i++) {
			if (bitmap[i] != bitmap[i-1]) {
				System.out.println("xxxxxxxxxxxx: " + bitmap);
			}
			
		}
		return bitmap;
	}
	
	public static int[] UnHibernate(Object obj_) throws IOException{
		if (obj_ instanceof int[] || obj_ instanceof byte[]) {
			byte[] obj = (byte[]) obj_;
			if (obj[1] > 256) {
				int[] ints = new int[obj.length];
				for (int i = 0, n = obj.length; i < n; i++) {
		            int read = (int) ((int) obj[i] & 0xff);
		            System.out.println("Read: " + read);
		            ints[i] = read;
		        }
				return ints;
			}else {
				return bitmapFromByteArray(obj);
			}
		} else {
			throw new IOException("is not a bytearray or bitmap");
		}

		
	}
	
	public static Image depthBuild(int depth, int[] bits, int height, int width, Color[] colorMap){
		System.out.println("image depth is " + depth);
		System.out.println("height is " + height);
		System.out.println("width is " + width);
		
		switch (depth) {
		case 8:
			byte[] raster = getByteRaster(bits, height, width, depth);
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();
			for (int x = 0; x < raster.length/height; x++) {
				for (int y = 0; y < raster.length/width; y++) {
					System.out.print(y+x*width);
					System.out.println(" " + raster[(y+x*width)+256]);
					g.setColor(colorMap[raster[(y+x*width)+256]]);
					g.drawLine(y, x, y, x);
				}

			}
			return img;

		case 16:
			
		case 32:

		
		}
		return null;
	}
	
	public static byte[] getByteRaster(int[] bits, int height, int width, int depth){
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\JTest\\log.txt"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] byteRaster = new byte[height*width];
		int pixelsPerHeight = bits.length/width;
		System.out.println("length of bitmap is " + bits.length);
		System.out.println("height should be " + pixelsPerHeight);
		
   int bitsPerPixel = 32 / depth;

   for (int x = 0; x < width; x++) {
	   for (int y = 0; y < height; y++) {
		   int data = bits[(x * height + y / bitsPerPixel)];
		  
		   try {
			   out.write("reading from: " + (x * height + y / bitsPerPixel) + " ");
			out.write("data: " + data + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   int i2 = depth * (bitsPerPixel - y % bitsPerPixel - 1);
		   int pixel = (data >> i2 & ((1 << depth) - 1));
		   if (pixel > 0) {
			   System.out.print("writing " + pixel + " to " + (x * height + y));
		}
		   
		   byteRaster[(x * height + y)] = (byte)pixel;
      }
	   System.out.println();
		
	
    }
   System.out.println(byteRaster.length);
   System.out.println(width);
   System.out.println(height);
   return byteRaster;
		
	}
	
	public static int decompressDecodeInt(DataInputStream in) throws IOException{
		int Int = in.readUnsignedByte();
		System.out.println(Int);
		if (Int <= 223) {
			
		}else{
			if (Int <= 254) {
				Int = (Int-224)*256 + in.readUnsignedByte();
			}else{
				Int = 0;
				Int = in.readInt();
			}
		}
		return Int;
		//anInt <= 223 ifFalse:
		//	[anInt <= 254
		//		ifTrue: [anInt _ (anInt-224)*256 + (ba at: i).  i _ i+1]
		//		ifFalse: [anInt _ 0.
		//				1 to: 4 do: [:j | anInt _ (anInt bitShift: 8) + (ba at: i).  i _ i+1]]].
		//n _ anInt >> 2.
	}
}
