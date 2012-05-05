package org.jblocks.scratch;

 import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;
 
 class ObjReader
 {
   DataInputStream s;
   Object[][] objTable;
   static final int ObjectRefID = 99;
   static final Object[] empty = new Object[0];
   static final Canvas canvas = new Canvas();
 
   static final byte[] macRomanToISOLatin = { -60, -59, -57, -55, -47, -42, -36, -31, -32, -30, -28, -29, -27, -25, -23, -24, -22, -21, -19, -20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4, -122, -80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128, -58, -40, -127, -79, -118, -115, -91, -75, -114, -113, -112, -102, -99, -86, -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, -78, -85, -69, -123, -96, -64, -61, -43, -116, -100, -106, -105, -109, -108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67, -121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50, -49, -52, -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, -81, -41, -35, -34, -72, -16, -3, -2 };
 
  public static final byte[] squeakColors = { -1, -1, -1, 0, 0, 0, -1, -1, -1, -128, -128, -128, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, -1, -1, -1, -1, 0, -1, 0, -1, 32, 32, 32, 64, 64, 64, 96, 96, 96, -97, -97, -97, -65, -65, -65, -33, -33, -33, 8, 8, 8, 16, 16, 16, 24, 24, 24, 40, 40, 40, 48, 48, 48, 56, 56, 56, 72, 72, 72, 80, 80, 80, 88, 88, 88, 104, 104, 104, 112, 112, 112, 120, 120, 120, -121, -121, -121, -113, -113, -113, -105, -105, -105, -89, -89, -89, -81, -81, -81, -73, -73, -73, -57, -57, -57, -49, -49, -49, -41, -41, -41, -25, -25, -25, -17, -17, -17, -9, -9, -9, 0, 0, 0, 0, 51, 0, 0, 102, 0, 0, -103, 0, 0, -52, 0, 0, -1, 0, 0, 0, 51, 0, 51, 51, 0, 102, 51, 0, -103, 51, 0, -52, 51, 0, -1, 51, 0, 0, 102, 0, 51, 102, 0, 102, 102, 0, -103, 102, 0, -52, 102, 0, -1, 102, 0, 0, -103, 0, 51, -103, 0, 102, -103, 0, -103, -103, 0, -52, -103, 0, -1, -103, 0, 0, -52, 0, 51, -52, 0, 102, -52, 0, -103, -52, 0, -52, -52, 0, -1, -52, 0, 0, -1, 0, 51, -1, 0, 102, -1, 0, -103, -1, 0, -52, -1, 0, -1, -1, 51, 0, 0, 51, 51, 0, 51, 102, 0, 51, -103, 0, 51, -52, 0, 51, -1, 0, 51, 0, 51, 51, 51, 51, 51, 102, 51, 51, -103, 51, 51, -52, 51, 51, -1, 51, 51, 0, 102, 51, 51, 102, 51, 102, 102, 51, -103, 102, 51, -52, 102, 51, -1, 102, 51, 0, -103, 51, 51, -103, 51, 102, -103, 51, -103, -103, 51, -52, -103, 51, -1, -103, 51, 0, -52, 51, 51, -52, 51, 102, -52, 51, -103, -52, 51, -52, -52, 51, -1, -52, 51, 0, -1, 51, 51, -1, 51, 102, -1, 51, -103, -1, 51, -52, -1, 51, -1, -1, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, -103, 0, 102, -52, 0, 102, -1, 0, 102, 0, 51, 102, 51, 51, 102, 102, 51, 102, -103, 51, 102, -52, 51, 102, -1, 51, 102, 0, 102, 102, 51, 102, 102, 102, 102, 102, -103, 102, 102, -52, 102, 102, -1, 102, 102, 0, -103, 102, 51, -103, 102, 102, -103, 102, -103, -103, 102, -52, -103, 102, -1, -103, 102, 0, -52, 102, 51, -52, 102, 102, -52, 102, -103, -52, 102, -52, -52, 102, -1, -52, 102, 0, -1, 102, 51, -1, 102, 102, -1, 102, -103, -1, 102, -52, -1, 102, -1, -1, -103, 0, 0, -103, 51, 0, -103, 102, 0, -103, -103, 0, -103, -52, 0, -103, -1, 0, -103, 0, 51, -103, 51, 51, -103, 102, 51, -103, -103, 51, -103, -52, 51, -103, -1, 51, -103, 0, 102, -103, 51, 102, -103, 102, 102, -103, -103, 102, -103, -52, 102, -103, -1, 102, -103, 0, -103, -103, 51, -103, -103, 102, -103, -103, -103, -103, -103, -52, -103, -103, -1, -103, -103, 0, -52, -103, 51, -52, -103, 102, -52, -103, -103, -52, -103, -52, -52, -103, -1, -52, -103, 0, -1, -103, 51, -1, -103, 102, -1, -103, -103, -1, -103, -52, -1, -103, -1, -1, -52, 0, 0, -52, 51, 0, -52, 102, 0, -52, -103, 0, -52, -52, 0, -52, -1, 0, -52, 0, 51, -52, 51, 51, -52, 102, 51, -52, -103, 51, -52, -52, 51, -52, -1, 51, -52, 0, 102, -52, 51, 102, -52, 102, 102, -52, -103, 102, -52, -52, 102, -52, -1, 102, -52, 0, -103, -52, 51, -103, -52, 102, -103, -52, -103, -103, -52, -52, -103, -52, -1, -103, -52, 0, -52, -52, 51, -52, -52, 102, -52, -52, -103, -52, -52, -52, -52, -52, -1, -52, -52, 0, -1, -52, 51, -1, -52, 102, -1, -52, -103, -1, -52, -52, -1, -52, -1, -1, -1, 0, 0, -1, 51, 0, -1, 102, 0, -1, -103, 0, -1, -52, 0, -1, -1, 0, -1, 0, 51, -1, 51, 51, -1, 102, 51, -1, -103, 51, -1, -52, 51, -1, -1, 51, -1, 0, 102, -1, 51, 102, -1, 102, 102, -1, -103, 102, -1, -52, 102, -1, -1, 102, -1, 0, -103, -1, 51, -103, -1, 102, -103, -1, -103, -103, -1, -52, -103, -1, -1, -103, -1, 0, -52, -1, 51, -52, -1, 102, -52, -1, -103, -52, -1, -52, -52, -1, -1, -52, -1, 0, -1, -1, 51, -1, -1, 102, -1, -1, -103, -1, -1, -52, -1, -1, -1, -1 };
 
  /**
   * 
   * @param paramInputStream the scratch file
   */
   ObjReader(InputStream paramInputStream)
   {
     this.s = new DataInputStream(paramInputStream);
   }
 
   Object[][] readObjects(LContext paramLContext) throws IOException
   {
     readInfo();
     readObjTable(paramLContext);
     return this.objTable;
   }
 
   Hashtable readInfo() throws IOException
   {
     byte[] arrayOfByte = new byte[10];
     this.s.read(arrayOfByte);
     if ((!"ScratchV01".equals(new String(arrayOfByte))) && (!"ScratchV02".equals(new String(arrayOfByte)))) {
       throw new IOException();
     }
 
     int i = this.s.readInt();
     readObjTable(null);
 
     Object[] arrayOfObject = (Object[])this.objTable[0][0];
     Hashtable localHashtable = new Hashtable(arrayOfObject.length);
     for (int j = 0; j < arrayOfObject.length - 1; j += 2) {
       localHashtable.put(arrayOfObject[j], arrayOfObject[(j + 1)]);
     }
     return localHashtable;
   }
 
   void readObjTable(LContext paramLContext) throws IOException {
     byte[] arrayOfByte = new byte[4];
     //read header(4 bytes)
     this.s.read(arrayOfByte);
     if ((!"ObjS".equals(new String(arrayOfByte))) || (this.s.readByte() != 1)) throw new IOException();
     this.s.read(arrayOfByte);
     if ((!"Stch".equals(new String(arrayOfByte))) || (this.s.readByte() != 1)) throw new IOException();
     //read one int value
     int length = this.s.readInt();
 
     this.objTable = new Object[length][];
     for (int j = 0; j < length; j++) {
       this.objTable[j] = readObj();
     }
     createSpritesAndWatchers(paramLContext);
     buildImagesAndSounds();
     fixSounds();
     resolveReferences();
     uncompressMedia();
   }
 
   Object[] readObj() throws IOException{
     int id = this.s.readUnsignedByte();
     Object[] object;
     System.out.println("class " + id);
     if (id < 99) {
    	 //Fixed format object
       object = new Object[2];
       //the object
       object[0] = readFixedFormat(id);
       //the id
       object[1] = new Integer(id);
     } else {
    	 //user class object
       int j = this.s.readUnsignedByte();
       int k = this.s.readUnsignedByte();
       object= new Object[3 + k];
       object[0] = empty;
       object[1] = new Integer(id);
       object[2] = new Integer(j);
       for (int m = 3; m < object.length; m++) object[m] = readField();
     }
     return object;
   }
 
   Object readField() throws IOException {
     int i = this.s.readUnsignedByte();
     System.out.println("field " + i);
     if (i == 99) {
       int j = this.s.readUnsignedByte() << 16;
       j += (this.s.readUnsignedByte() << 8);
       j += this.s.readUnsignedByte();
       Ref ref = new Ref(j);
       System.out.println(" field ref: " + deRef(ref));
       return ref;
     }
     return readFixedFormat(i);
   }
 
   Object readFixedFormat(int paramInt)
     throws IOException
   {
     int objectData1;
     int objectdata2;
     int objectData3;
     byte[] objectData;
     Object[] object;
     int m;
     switch (paramInt) {
     case 1://null
       return empty;
     case 2://true
       return Boolean.TRUE;
     case 3://false
       return Boolean.FALSE;
     case 4://smallint
       return new Integer(this.s.readInt());
     case 5://smallint16
       return new Integer(this.s.readShort());
     case 6://?
     case 7://largenegativeint
       double d1 = 0.0D;
       double d2 = 1.0D;
       objectData1 = this.s.readShort();
       for (objectdata2 = 0; objectdata2 < objectData1; objectdata2++) {
         objectData3 = this.s.readUnsignedByte();
         d1 += d2 * objectData3;
         d2 *= 256.0D;
       }
       if (paramInt == 7) d1 = -d1;
       return new Double(d1);
     case 8://float
       return new Double(this.s.readDouble());
     case 9://string
     case 10://symbol
       objectData1 = this.s.readInt();
       this.s.read(objectData = new byte[objectData1]);
       for (objectdata2 = 0; objectdata2 < objectData1; objectdata2++) {
         if (objectData[objectdata2] >= 0) continue; objectData[objectdata2] = macRomanToISOLatin[(objectData[objectdata2] + 128)];
       }
       try {
         return new String(objectData, "ISO-8859-1");
       } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
         return new String(objectData);
       }
     case 11://bytearray
       objectData1 = this.s.readInt();
       this.s.read(objectData = new byte[objectData1]);
       return objectData;
     case 12://soundbuffer
       objectData1 = this.s.readInt();
       this.s.read(objectData = new byte[2 * objectData1]);
       return objectData;
     case 13://bitmap
       objectData1 = this.s.readInt();
       int[] arrayOfInt = new int[objectData1];
       for (objectData3 = 0; objectData3 < arrayOfInt.length; objectData3++) arrayOfInt[objectData3] = this.s.readInt();
       return arrayOfInt;
     case 14://UTF-8
       objectData1 = this.s.readInt();
       this.s.read(objectData = new byte[objectData1]);
       try {
         return new String(objectData, "UTF-8");
       } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
         return new String(objectData);
       }
     case 20:
     case 21:
     case 22:
     case 23://identityset
       objectData1 = this.s.readInt();
       object = new Object[objectData1];
       for (m = 0; m < object.length; m++) object[m] = readField();
       return object;
     case 24:
     case 25://identitydictionary
       objectData1 = this.s.readInt();
       object = new Object[2 * objectData1];
       for (m = 0; m < object.length; m++) object[m] = readField();
       return object;
     case 30:
     case 31://color
       m = this.s.readInt();
       int n = 255;
       if (paramInt == 31) n = this.s.readUnsignedByte();
       return new Color(m >> 22 & 0xFF, m >> 12 & 0xFF, m >> 2 & 0xFF, n);
     case 32://point
       object = new Object[2];
       object[0] = readField();
       object[1] = readField();
       return object;
     case 33://rectangle
       object = new Object[4];
       object[0] = readField();
       object[1] = readField();
       object[2] = readField();
       object[3] = readField();
       return object;
     case 34:
     case 35://colorform
       Object[] arrayOfObject2 = new Object[6];
       for (int i1 = 0; i1 < 5; i1++) arrayOfObject2[i1] = readField();
 
       if (paramInt == 35) arrayOfObject2[5] = readField();
       return arrayOfObject2;
     case 15:
     case 16:
     case 17:
     case 18:
     case 19:
     case 26:
     case 27:
     case 28:
     case 29: } System.out.println("Unknown fixed-format class " + paramInt);
     throw new IOException();
   }
 
   void createSpritesAndWatchers(LContext paramLContext)
   {
     for (int i = 0; i < this.objTable.length; i++) {
       Object[] arrayOfObject = this.objTable[i];
       int j = ((Number)arrayOfObject[1]).intValue();
       if ((j == 124) || (j == 125)){
    	   for (int j2 = 0; j2 < arrayOfObject.length; j2++) {
    		   if (arrayOfObject[j2] instanceof Ref) {
    			   System.out.println(deRef((Ref) arrayOfObject[j2]).getClass().getName());
    			   System.out.println(deRef((Ref) arrayOfObject[j2]));
    		   } else {
    		   System.out.println(arrayOfObject[j2].getClass().getName());
    		   System.out.println(arrayOfObject[j2]);
			}
    		   
		}
    	   System.out.println();
    	   arrayOfObject[0] = new Sprite();
    	   }
       if (j == 155) {
    	   //TODO
         //arrayOfObject[0] = new Watcher(paramLContext);
 
         if (((Number)arrayOfObject[2]).intValue() > 3) {
           Number localNumber1 = (Number)arrayOfObject[23];
           Number localNumber2 = (Number)arrayOfObject[24];
           if ((floatWithoutFraction(localNumber1)) || (floatWithoutFraction(localNumber2)))
             arrayOfObject[24] = new Double(localNumber2.doubleValue() + 1.0E-008D);
         }
       }
     }
   }
 
   boolean floatWithoutFraction(Object paramObject)
   {
     if (!(paramObject instanceof Double)) return false;
     double d = ((Double)paramObject).doubleValue();
     return Math.round(d) == d;
   }
 
   void resolveReferences() throws IOException {
     for (int i = 0; i < this.objTable.length; i++) {
       int j = ((Number)this.objTable[i][1]).intValue();
 
       if ((j >= 20) && (j <= 29)) {
         Object[] arrayOfObject = (Object[])this.objTable[i][0];
         for (int m = 0; m < arrayOfObject.length; m++) {
           Object localObject2 = arrayOfObject[m];
           if ((localObject2 instanceof Ref)) {
             arrayOfObject[m] = deRef((Ref)localObject2);
           }
         }
       }
       if (j > 99)
         for (int k = 3; k < this.objTable[i].length; k++) {
           Object localObject1 = this.objTable[i][k];
           if ((localObject1 instanceof Ref))
             this.objTable[i][k] = deRef((Ref)localObject1);
         }
     }
   }
 
   Object deRef(Ref paramRef)
   {
     Object[] arrayOfObject = this.objTable[paramRef.index];
     try {
    	 return (arrayOfObject[0] == null) || (arrayOfObject[0] == empty) ? arrayOfObject : arrayOfObject[1];
	} catch (Exception e) {
		e.printStackTrace();
		// TODO: handle exception
		Object[] obj = new Object[3];
		obj[0] = new Object();
		obj[1] = 0;
		return obj;
	}
     
   }
 
   void buildImagesAndSounds() throws IOException {
     for (int i = 0; i < this.objTable.length; i++) {
       int j = ((Number)this.objTable[i][1]).intValue();
       Object[] arrayOfObject;
       if ((j == 34) || (j == 35)) {
         arrayOfObject = (Object[])this.objTable[i][0];
         int k = ((Number)arrayOfObject[0]).intValue();
         int m = ((Number)arrayOfObject[1]).intValue();
         int n = ((Number)arrayOfObject[2]).intValue();
         int[] arrayOfInt = decodePixels(this.objTable[((Ref)arrayOfObject[4]).index][0]);
         MemoryImageSource localMemoryImageSource = null;
 
         this.objTable[i][0] = empty;
         Object localObject2;
         Object localObject1;
         if (n <= 8)
         {//TODO
           if (arrayOfObject[5] != null) {
             localObject2 = (Object[])this.objTable[((Ref)arrayOfObject[5]).index][0];
             //localObject1 = customColorMap(n, localObject2);
             localObject1 = squeakColors[n];
           } else {
             localObject1 = squeakColors[n];
           }
           //TODO localMemoryImageSource = new MemoryImageSource(k, m, (ColorModel)localObject1, rasterToByteRaster(arrayOfInt, k, m, n), 0, k);
         }
         if (n == 16) {
           localMemoryImageSource = new MemoryImageSource(k, m, raster16to32(arrayOfInt, k, m), 0, k);
         }
         if (n == 32) {
           localMemoryImageSource = new MemoryImageSource(k, m, rasterAddAlphaTo32(arrayOfInt), 0, k);
         }
         if (localMemoryImageSource != null) {
           localObject1 = new int[k * m];
           localObject2 = new PixelGrabber(localMemoryImageSource, 0, 0, k, m, (int[]) localObject1, 0, k);
           try { ((PixelGrabber)localObject2).grabPixels(); } catch (InterruptedException localInterruptedException) {
           }BufferedImage localBufferedImage = new BufferedImage(k, m, 2);
           localBufferedImage.getRaster().setDataElements(0, 0, k, m, localObject1);
           this.objTable[i][0] = localBufferedImage;
         }
       }
 
       if (j == 109) {
         arrayOfObject = this.objTable[((Ref)this.objTable[i][6]).index];
         this.objTable[i][0] = new ScratchSound(((Number)this.objTable[i][7]).intValue(), (byte[])arrayOfObject[0]);
       }
     }
   }
 
   int[] decodePixels(Object paramObject)
     throws IOException
   {
     if ((paramObject instanceof int[])) return (int[])paramObject;
 
     DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream((byte[])paramObject));
     int i = decodeInt(localDataInputStream);
     int[] arrayOfInt = new int[i];
     int j = 0;
     int m;
     int i2;
     int i3;
     while (((localDataInputStream.available() > 0 ? 1 : 0) & (j < i ? 1 : 0)) != 0) {
       int k = decodeInt(localDataInputStream);
       m = k >> 2;
       int n = k & 0x3;
 
       switch (n) {
       case 0:
         j += m;
         break;
       case 1:
         int i1 = localDataInputStream.readUnsignedByte();
         i2 = i1 << 24 | i1 << 16 | i1 << 8 | i1;
         //for (i3 = 0; i3 < m; ) { arrayOfInt[(j++)] = i2; i3++; continue;
 //TODO
         
          /* i2 = localDataInputStream.readInt();
           for (i3 = 0; i3 < m; ) { arrayOfInt[(j++)] = i2; i3++; //continue;
 
             for (i3 = 0; i3 < m; i3++) {
               i2 = localDataInputStream.readUnsignedByte() << 24;
               i2 |= localDataInputStream.readUnsignedByte() << 16;
               i2 |= localDataInputStream.readUnsignedByte() << 8;
               i2 |= localDataInputStream.readUnsignedByte();
               arrayOfInt[(j++)] = i2;
             } }
          */
         //}
         
       case 2:
       case 3:
       }
     }
     return arrayOfInt;
   }
 
   int decodeInt(DataInputStream paramDataInputStream)
     throws IOException
   {
     int i = paramDataInputStream.readUnsignedByte();
     if (i <= 223) return i;
     if (i <= 254) return (i - 224) * 256 + paramDataInputStream.readUnsignedByte();
     return paramDataInputStream.readInt();
   }
 
   int[] rasterAddAlphaTo32(int[] paramArrayOfInt)
   {
     for (int i = 0; i < paramArrayOfInt.length; i++) {
       int j = paramArrayOfInt[i];
       if (j == 0) continue; paramArrayOfInt[i] = (0xFF000000 | j);
     }
     return paramArrayOfInt;
   }
 
   int[] raster16to32(int[] paramArrayOfInt, int paramInt1, int paramInt2)
   {
     int[] arrayOfInt = new int[paramInt1 * paramInt2];
     int i2 = (paramInt1 + 1) / 2;
     for (int i3 = 0; i3 < paramInt2; i3++) {
       int i = 16;
       for (int i4 = 0; i4 < paramInt1; i4++) {
         int j = paramArrayOfInt[(i3 * i2 + i4 / 2)] >> i & 0xFFFF;
         int k = (j >> 10 & 0x1F) << 3;
         int m = (j >> 5 & 0x1F) << 3;
         int n = (j & 0x1F) << 3;
         int i1 = k + m + n == 0 ? 0 : 0xFF000000 | k << 16 | m << 8 | n;
         arrayOfInt[(i3 * paramInt1 + i4)] = i1;
         i = i == 16 ? 0 : 16;
       }
     }
     return arrayOfInt;
   }
 
   byte[] rasterToByteRaster(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
     byte[] arrayOfByte = new byte[paramInt1 * paramInt2];
     int i = paramArrayOfInt.length / paramInt2;
     int j = (1 << paramInt3) - 1;
     int k = 32 / paramInt3;
 
     for (int m = 0; m < paramInt2; m++) {
       for (int n = 0; n < paramInt1; n++) {
         int i1 = paramArrayOfInt[(m * i + n / k)];
         int i2 = paramInt3 * (k - n % k - 1);
         arrayOfByte[(m * paramInt1 + n)] = (byte)(i1 >> i2 & j);
       }
     }
     return arrayOfByte;
   }
 
   
 
   void fixSounds() {
	     int i = 0;
	     int k;
	     for (int j = 0; j < this.objTable.length; j++) {
	       k = ((Number)this.objTable[j][1]).intValue();
	       if ((k == 109) && 
	         (((ScratchSound)this.objTable[j][0]).isByteReversed())) i = 1;
	     }
	 
	     if (i == 0) return;
	 
	     for (int j = 0; j < this.objTable.length; j++) {
	       k = ((Number)this.objTable[j][1]).intValue();
	       if (k == 109)
	         ((ScratchSound)this.objTable[j][0]).reverseBytes();
	     }
	   }
	 
	   void uncompressMedia()
	   {
	     for (int i = 0; i < this.objTable.length; i++) {
	       Object[] arrayOfObject = this.objTable[i];
	       int j = ((Number)arrayOfObject[1]).intValue();
	       int k = -1;
	       if (j >= 100) k = ((Number)arrayOfObject[2]).intValue();
	 
	       if ((j == 162) && (k >= 4)) {
	         if ((arrayOfObject[7] instanceof byte[])) {
	           BufferedImage localBufferedImage = ImageReader.jpegDecode((byte[])arrayOfObject[7]);
	           if (localBufferedImage != null) {
	             if ((arrayOfObject[4] instanceof Image)) ((Image)arrayOfObject[4]).flush();
	             arrayOfObject[4] = localBufferedImage;
	             arrayOfObject[7] = empty;
	           }
	         }
	         if ((arrayOfObject[8] instanceof BufferedImage)) {
	           arrayOfObject[4] = arrayOfObject[8];
	           arrayOfObject[8] = empty;
	         }
	       }
	       if ((j != 164) || (k < 2) || 
	         (!(arrayOfObject[9] instanceof byte[]))) continue;
	       int m = ((Number)arrayOfObject[7]).intValue();
	       int n = ((Number)arrayOfObject[8]).intValue();
	       byte[] arrayOfByte = (byte[])arrayOfObject[9];
	       int i1 = (arrayOfByte.length * 8 + (n - 1)) / n;
	       int[] arrayOfInt = new ADPCMDecoder(arrayOfByte, n).decode(i1);
	       arrayOfObject[4] = new ScratchSound(m, arrayOfInt);
	       boolean tmp261_260 = (arrayOfObject[9] == empty); arrayOfObject[8] = tmp261_260; arrayOfObject[7] = tmp261_260;
	     }
	   }
	 
	   void canonicalizeMedia()
	   {
	     Vector localVector1 = new Vector(100);
	     Vector localVector2 = new Vector(100);
	 
	     for (int i = 0; i < this.objTable.length; i++) {
	       Object[] arrayOfObject = this.objTable[i];
	       int j = ((Number)arrayOfObject[1]).intValue();
	       Object localObject;
	       if (j == 162) {
	         localObject = (BufferedImage)arrayOfObject[4];
	       }
	 
	       if (j == 164)
	         localObject = (ScratchSound)arrayOfObject[4];
	     }
	   }
   
 
   void printit(Object paramObject) {
     if (((paramObject instanceof Object[])) && (((Object[])paramObject).length == 0)) {
       System.out.print(" []");
       return;
     }
     if ((paramObject instanceof BufferedImage)) {
       BufferedImage localBufferedImage = (BufferedImage)paramObject;
       System.out.print(" BufferedImage_" + paramObject.hashCode() + "(" + localBufferedImage.getWidth(null) + "x" + localBufferedImage.getHeight(null) + ")");
       return;
     }
     System.out.print(" " + paramObject);
   }
   static class LContext
   {
     static final boolean isApplet = true;
     Hashtable oblist = new Hashtable();
     Hashtable props = new Hashtable();
     MapList iline;
     //TODO Symbol cfun;
    //TODO Symbol ufun;
     Object ufunresult;
     Object juststop = new Object();
     boolean mustOutput;
     boolean timeToStop;
     int priority = 0;
     Object[] locals;
     String errormessage;
     String codeBase;
     String projectURL;
     //TODO PlayerCanvas canvas;
     Sprite who;
     Thread logoThread;
     PrintStream tyo;
     boolean autostart;
   }
   class MapList
   {
     Object[] tokens;
     int offset = 0;
   
     MapList(Object[] paramArrayOfObject) {
       this.tokens = paramArrayOfObject;
     }
     Object next() {
       return this.tokens[(this.offset++)]; } 
     Object peek() { return this.tokens[this.offset]; } 
     boolean eof() { return this.offset == this.tokens.length;
     }
   }
   
   class Ref
   {
     int index;
   
     Ref(int paramInt)
     {
       this.index = (paramInt - 1);
     }
   
     public String toString() {
       return "Ref(" + this.index + ")";
     }
   }
 }