package org.jblocks.scratch.gui;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Reader {
	DataInputStream in;
	String version;
	int maxBytes;
	int avalable;
	int objtablesize;
	Object[] objtable;
	public Reader(InputStream file){
		this.in = new DataInputStream(file);
	}
	
	public void read() throws IOException{
		byte[] versionbytes = new byte[10];
		in.read(versionbytes);
		String version = new String(versionbytes);
		this.version = version;
		if (!version.equals("ScratchV02")) {
			throw new IOException("error: " + version + "is unknown");
		}
		System.out.println(version);
		this.readObjStore();
	}
	void readObjStore() throws IOException{
		
		int storeSize = in.readInt();//the length of the store in bytes
		System.out.println("store size " + storeSize);
		this.maxBytes = storeSize;
		avalable = in.available();//the amount of bytes remaining
		System.out.println(avalable);
		
		byte[] objsstchbytes = new byte[4];
	    in.read(objsstchbytes);
	    if (in.readByte() != 1) {
			throw new IOException("no 1 after ObjS");
		}
		String objsstch = new String(objsstchbytes);
		in.read(objsstchbytes);
		objsstch = objsstch + new String(objsstchbytes);

		 if (in.readByte() != 1) {
				throw new IOException("no 1 after Stch");
			}
		 System.out.println(objsstch);
		 if (!objsstch.equals("ObjSStch")) {
			throw new IOException("ObjSStch is not present");
		}
		 objtablesize = in.readInt();
		 System.out.println("there are " + objtablesize + " objects in this table");
		 objtable = new Object[objtablesize];
		 for (int i = 0; i < objtablesize; i++) {
			 System.out.println(i);
			 Object obj = readObj();
			 objtable[i] = obj;
			System.out.println(obj);
			if (obj instanceof Object[]) {
				Object[] arry = (Object[])obj;
				for (int j = 0; j < arry.length; j++) {
					System.out.println(arry[j]);
				}
			
			}

		}
			Images.bitmapFromByteArray(objtable[15]);
	     //if ((!"ObjS".equals(new String(arrayOfByte))) || (this.s.readByte() != 1)) throw new IOException();

	    // if ((!"Stch".equals(new String(arrayOfByte))) || (this.s.readByte() != 1)) throw new IOException();
	 
	}
	
	public Object readObj() throws IOException{
		int objId = in.readByte();
		System.out.println("reading " + objId);
		switch (objId) {
		case 1:
			return Builder.buildNil(in, this);
		case 2:
			return Builder.buildTrue(in, this);
		case 3:
			return Builder.buildFalse(in, this);
		case 4:
			return Builder.buildInt(in, this);
		case 5:
			return Builder.buildShort(in, this);
		case 6:
			return Builder.buildLargePosInt(in, this);
		case 7:
			return Builder.buildNil(in, this);
		case 8:
			return Builder.buildFloat(in, this);
		case 9:
			return Builder.buildSymol(in, this);
		case 10:
			return Builder.buildSymol(in, this);
		case 11:
			return Builder.buildByteArray(in, this);
		case 12:
			return Builder.buildSoundBuffer(in, this);
		case 13:
			return Builder.buildBitMap(in, this);
		case 14:
			return Builder.buildUTF8(in, this);
		case 23:
			return Builder.buildIdentSet(in, this);
		case 24:
			return Builder.buildDict(in, this);
		case 25:
			return Builder.buildDict(in, this);
		case 30:
			return Builder.buildColor(in, this);
		case 31:
			return Builder.buildTransColor(in, this);
		case 32:
			return Builder.buildPoint(in, this);
		case 33:
			return Builder.buildRectangle(in, this);
		case 99:
			return Builder.buildRef(in, this);
		case 35:
			return Builder.buildColorForm(in, this);//TODO
		case 20:
			return Builder.buildIdentSet(in, this);
		
		}
		throw new IOException("unknown class id " + objId);
		
	}
}
