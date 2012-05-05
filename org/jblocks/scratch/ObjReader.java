package org.jblocks.scratch;


import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.imageio.ImageIO;

class ObjReader {

    private static final Object[] empty = new Object[0];
    
    static final byte[] macRomanToISOLatin = {-60, -59, -57, -55, -47, -42, -36, -31, -32, -30, -28, -29, -27, -25, -23, -24, -22, -21, -19, -20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4, -122, -80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128, -58, -40, -127, -79, -118, -115, -91, -75, -114, -113, -112, -102, -99, -86, -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, -78, -85, -69, -123, -96, -64, -61, -43, -116, -100, -106, -105, -109, -108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67, -121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50, -49, -52, -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, -81, -41, -35, -34, -72, -16, -3, -2};
    static final byte[] squeakColors = {-1, -1, -1, 0, 0, 0, -1, -1, -1, -128, -128, -128, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, -1, -1, -1, -1, 0, -1, 0, -1, 32, 32, 32, 64, 64, 64, 96, 96, 96, -97, -97, -97, -65, -65, -65, -33, -33, -33, 8, 8, 8, 16, 16, 16, 24, 24, 24, 40, 40, 40, 48, 48, 48, 56, 56, 56, 72, 72, 72, 80, 80, 80, 88, 88, 88, 104, 104, 104, 112, 112, 112, 120, 120, 120, -121, -121, -121, -113, -113, -113, -105, -105, -105, -89, -89, -89, -81, -81, -81, -73, -73, -73, -57, -57, -57, -49, -49, -49, -41, -41, -41, -25, -25, -25, -17, -17, -17, -9, -9, -9, 0, 0, 0, 0, 51, 0, 0, 102, 0, 0, -103, 0, 0, -52, 0, 0, -1, 0, 0, 0, 51, 0, 51, 51, 0, 102, 51, 0, -103, 51, 0, -52, 51, 0, -1, 51, 0, 0, 102, 0, 51, 102, 0, 102, 102, 0, -103, 102, 0, -52, 102, 0, -1, 102, 0, 0, -103, 0, 51, -103, 0, 102, -103, 0, -103, -103, 0, -52, -103, 0, -1, -103, 0, 0, -52, 0, 51, -52, 0, 102, -52, 0, -103, -52, 0, -52, -52, 0, -1, -52, 0, 0, -1, 0, 51, -1, 0, 102, -1, 0, -103, -1, 0, -52, -1, 0, -1, -1, 51, 0, 0, 51, 51, 0, 51, 102, 0, 51, -103, 0, 51, -52, 0, 51, -1, 0, 51, 0, 51, 51, 51, 51, 51, 102, 51, 51, -103, 51, 51, -52, 51, 51, -1, 51, 51, 0, 102, 51, 51, 102, 51, 102, 102, 51, -103, 102, 51, -52, 102, 51, -1, 102, 51, 0, -103, 51, 51, -103, 51, 102, -103, 51, -103, -103, 51, -52, -103, 51, -1, -103, 51, 0, -52, 51, 51, -52, 51, 102, -52, 51, -103, -52, 51, -52, -52, 51, -1, -52, 51, 0, -1, 51, 51, -1, 51, 102, -1, 51, -103, -1, 51, -52, -1, 51, -1, -1, 102, 0, 0, 102, 51, 0, 102, 102, 0, 102, -103, 0, 102, -52, 0, 102, -1, 0, 102, 0, 51, 102, 51, 51, 102, 102, 51, 102, -103, 51, 102, -52, 51, 102, -1, 51, 102, 0, 102, 102, 51, 102, 102, 102, 102, 102, -103, 102, 102, -52, 102, 102, -1, 102, 102, 0, -103, 102, 51, -103, 102, 102, -103, 102, -103, -103, 102, -52, -103, 102, -1, -103, 102, 0, -52, 102, 51, -52, 102, 102, -52, 102, -103, -52, 102, -52, -52, 102, -1, -52, 102, 0, -1, 102, 51, -1, 102, 102, -1, 102, -103, -1, 102, -52, -1, 102, -1, -1, -103, 0, 0, -103, 51, 0, -103, 102, 0, -103, -103, 0, -103, -52, 0, -103, -1, 0, -103, 0, 51, -103, 51, 51, -103, 102, 51, -103, -103, 51, -103, -52, 51, -103, -1, 51, -103, 0, 102, -103, 51, 102, -103, 102, 102, -103, -103, 102, -103, -52, 102, -103, -1, 102, -103, 0, -103, -103, 51, -103, -103, 102, -103, -103, -103, -103, -103, -52, -103, -103, -1, -103, -103, 0, -52, -103, 51, -52, -103, 102, -52, -103, -103, -52, -103, -52, -52, -103, -1, -52, -103, 0, -1, -103, 51, -1, -103, 102, -1, -103, -103, -1, -103, -52, -1, -103, -1, -1, -52, 0, 0, -52, 51, 0, -52, 102, 0, -52, -103, 0, -52, -52, 0, -52, -1, 0, -52, 0, 51, -52, 51, 51, -52, 102, 51, -52, -103, 51, -52, -52, 51, -52, -1, 51, -52, 0, 102, -52, 51, 102, -52, 102, 102, -52, -103, 102, -52, -52, 102, -52, -1, 102, -52, 0, -103, -52, 51, -103, -52, 102, -103, -52, -103, -103, -52, -52, -103, -52, -1, -103, -52, 0, -52, -52, 51, -52, -52, 102, -52, -52, -103, -52, -52, -52, -52, -52, -1, -52, -52, 0, -1, -52, 51, -1, -52, 102, -1, -52, -103, -1, -52, -52, -1, -52, -1, -1, -1, 0, 0, -1, 51, 0, -1, 102, 0, -1, -103, 0, -1, -52, 0, -1, -1, 0, -1, 0, 51, -1, 51, 51, -1, 102, 51, -1, -103, 51, -1, -52, 51, -1, -1, 51, -1, 0, 102, -1, 51, 102, -1, 102, 102, -1, -103, 102, -1, -52, 102, -1, -1, 102, -1, 0, -103, -1, 51, -103, -1, 102, -103, -1, -103, -103, -1, -52, -103, -1, -1, -103, -1, 0, -52, -1, 51, -52, -1, 102, -52, -1, -103, -52, -1, -52, -52, -1, -1, -52, -1, 0, -1, -1, 51, -1, -1, 102, -1, -1, -103, -1, -1, -52, -1, -1, -1, -1};
    // <member>
    private DataInputStream in;
    Object[][] objTable;

    /**
     * @param in the scratch file
     */
    public ObjReader(InputStream in) {
        this.in = new DataInputStream(in);
    }

    public Object[][] readObjects() throws IOException {
        readInfo();
        readObjTable();
        return this.objTable;
    }

    private HashMap readInfo() throws IOException {
        byte[] buf = new byte[10];
        this.in.read(buf);
        if ((!"ScratchV01".equals(new String(buf))) && (!"ScratchV02".equals(new String(buf)))) {
            throw new IOException();
        }

        int i = this.in.readInt();
        readObjTable();

        Object[] table = (Object[]) this.objTable[0][0];
        HashMap info = new HashMap(table.length);
        for (int j = 0; j < table.length - 1; j += 2) {
            info.put(table[j], table[(j + 1)]);
        }
        return info;
    }

    private void readObjTable() throws IOException {
        byte[] arrayOfByte = new byte[4];
        // read header(4 bytes)
        this.in.read(arrayOfByte);
        if ((!"ObjS".equals(new String(arrayOfByte))) || (this.in.readByte() != 1)) {
            throw new IOException();
        }
        this.in.read(arrayOfByte);
        if ((!"Stch".equals(new String(arrayOfByte))) || (this.in.readByte() != 1)) {
            throw new IOException();
        }
        // read one int value
        int length = this.in.readInt();

        this.objTable = new Object[length][];
        for (int i = 0; i < length; i++) {
            this.objTable[i] = readObj();
        }
        buildImagesAndSounds();
        fixSounds();
        uncompressMedia();
    }

    private Object[] readObj() throws IOException {
        int id = this.in.readUnsignedByte();
        Object[] object;
        if (id < 99) {
            //Fixed format object
            object = new Object[2];
            //the object
            object[0] = readFixedFormat(id);
            //the id
            object[1] = new Integer(id);
        } else {
            //user class object
            int j = this.in.readUnsignedByte();
            int k = this.in.readUnsignedByte();
            object = new Object[3 + k];
            object[0] = empty;
            object[1] = new Integer(id);
            object[2] = new Integer(j);
            for (int m = 3; m < object.length; m++) {
                object[m] = readField();
            }
        }
        return object;
    }

    private Object readField() throws IOException {
        int classID = this.in.readUnsignedByte();
        if (classID == 99) {
            int ref = this.in.readUnsignedByte() << 16;
            ref += (this.in.readUnsignedByte() << 8);
            ref += this.in.readUnsignedByte();
            return new Ref(ref);
        }
        return readFixedFormat(classID);
    }

    private Object readFixedFormat(int paramInt)
            throws IOException {
        int objectData1;
        int objectdata2;
        int objectData3;
        byte[] objectData;
        Object[] object;
        int m;
        switch (paramInt) {
            case 1:
                // null
                return empty;
            case 2:
                // true
                return Boolean.TRUE;
            case 3:
                // false
                return Boolean.FALSE;
            case 4:
                // SmallInt
                return new Integer(this.in.readInt());
            case 5:
                // SmallInt16
                return new Integer(this.in.readShort());
            case 6:
            case 7:
                // LargeNegativeInt
                double d1 = 0.0D;
                double d2 = 1.0D;
                objectData1 = this.in.readShort();
                for (objectdata2 = 0; objectdata2 < objectData1; objectdata2++) {
                    objectData3 = this.in.readUnsignedByte();
                    d1 += d2 * objectData3;
                    d2 *= 256.0D;
                }
                if (paramInt == 7) {
                    d1 = -d1;
                }
                return new Double(d1);
            case 8:
                // Float
                return new Double(this.in.readDouble());
            case 9:
            // String
            case 10:
                // Symbol
                objectData1 = this.in.readInt();
                this.in.read(objectData = new byte[objectData1]);
                for (objectdata2 = 0; objectdata2 < objectData1; objectdata2++) {
                    if (objectData[objectdata2] >= 0) {
                        continue;
                    }
                    objectData[objectdata2] = macRomanToISOLatin[(objectData[objectdata2] + 128)];
                }
                try {
                    return new String(objectData, "ISO-8859-1");
                } catch (UnsupportedEncodingException ex) {
                    return new String(objectData);
                }
            case 11:
                // ByteArray
                objectData1 = this.in.readInt();
                this.in.read(objectData = new byte[objectData1]);
                return objectData;
            case 12:
                // SoundBuffer
                objectData1 = this.in.readInt();
                this.in.read(objectData = new byte[2 * objectData1]);
                return objectData;
            case 13:
                // Bitmap
                objectData1 = this.in.readInt();
                int[] arrayOfInt = new int[objectData1];
                for (objectData3 = 0; objectData3 < arrayOfInt.length; objectData3++) {
                    arrayOfInt[objectData3] = this.in.readInt();
                }
                return arrayOfInt;
            case 14:
                //UTF-8
                objectData1 = this.in.readInt();
                this.in.read(objectData = new byte[objectData1]);
                try {
                    return new String(objectData, "UTF-8");
                } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
                    return new String(objectData);
                }
            case 20:
            case 21:
            case 22:
            case 23:
                // IdentitySet
                objectData1 = this.in.readInt();
                object = new Object[objectData1];
                for (m = 0; m < object.length; m++) {
                    object[m] = readField();
                }
                return object;
            case 24:
            case 25:
                // IdentityDictionary
                objectData1 = this.in.readInt();
                object = new Object[2 * objectData1];
                for (m = 0; m < object.length; m++) {
                    object[m] = readField();
                }
                return object;
            case 30:
            case 31:
                // Color
                m = this.in.readInt();
                int n = 255;
                if (paramInt == 31) {
                    n = this.in.readUnsignedByte();
                }
                return new Color(m >> 22 & 0xFF, m >> 12 & 0xFF, m >> 2 & 0xFF, n);
            case 32:
                // Point
                object = new Object[2];
                object[0] = readField();
                object[1] = readField();
                return object;
            case 33:
                // Rectangle
                object = new Object[4];
                object[0] = readField();
                object[1] = readField();
                object[2] = readField();
                object[3] = readField();
                return object;
            case 34:
            case 35:
                // ColorForm
                Object[] arrayOfObject2 = new Object[6];
                for (int i1 = 0; i1 < 5; i1++) {
                    arrayOfObject2[i1] = readField();
                }

                if (paramInt == 35) {
                    arrayOfObject2[5] = readField();
                }
                return arrayOfObject2;
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 26:
            case 27:
            case 28:
            case 29:
        }
        throw new IOException("Unknown-fixed-format class " + paramInt);
    }

    private boolean floatWithoutFraction(Object obj) {
        if (!(obj instanceof Double)) {
            return false;
        }
        double d = ((Double) obj).doubleValue();
        return Math.round(d) == d;
    }

    private void resolveReferences() throws IOException {
        for (int i = 0; i < this.objTable.length; i++) {
            int j = ((Number) this.objTable[i][1]).intValue();

            if ((j >= 20) && (j <= 29)) {
                Object[] arrayOfObject = (Object[]) this.objTable[i][0];
                for (int m = 0; m < arrayOfObject.length; m++) {
                    Object localObject2 = arrayOfObject[m];
                    if ((localObject2 instanceof Ref)) {
                        arrayOfObject[m] = deRef((Ref) localObject2);
                    }
                }
            }
            if (j > 99) {
                for (int k = 3; k < this.objTable[i].length; k++) {
                    Object localObject1 = this.objTable[i][k];
                    if ((localObject1 instanceof Ref)) {
                        this.objTable[i][k] = deRef((Ref) localObject1);
                    }
                }
            }
        }
    }

    Object[] deRef(Ref paramRef) {
        Object[] row = this.objTable[paramRef.index];
        return row;
    }

    private void buildImagesAndSounds() throws IOException {
        for (int i = 0; i < this.objTable.length; i++) {
            int j = ((Number) this.objTable[i][1]).intValue();
            Object[] arrayOfObject;
            if ((j == 34) || (j == 35)) {
                arrayOfObject = (Object[]) this.objTable[i][0];
                int k = ((Number) arrayOfObject[0]).intValue();
                int m = ((Number) arrayOfObject[1]).intValue();
                int n = ((Number) arrayOfObject[2]).intValue();
                int[] arrayOfInt = decodePixels(this.objTable[((Ref) arrayOfObject[4]).index][0]);
                MemoryImageSource localMemoryImageSource = null;

                this.objTable[i][0] = empty;
                Object localObject2;
                Object localObject1;
                if (n <= 8) {//TODO
                    if (arrayOfObject[5] != null) {
                        localObject2 = (Object[]) this.objTable[((Ref) arrayOfObject[5]).index][0];
                        //localObject1 = customColorMap(n, localObject2);
                        localObject1 = squeakColors[n];
                    } else {
                        localObject1 = squeakColors[n];
                    }
                    //TODO localMemoryImageSource = new MemoryImageSource(k, m, (ColorModel)localObject1, rasterToByteRaster(raster, k, m, n), 0, k);
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
                    try {
                        ((PixelGrabber) localObject2).grabPixels();
                    } catch (InterruptedException localInterruptedException) {
                    }
                    BufferedImage localBufferedImage = new BufferedImage(k, m, 2);
                    localBufferedImage.getRaster().setDataElements(0, 0, k, m, localObject1);
                    this.objTable[i][0] = localBufferedImage;
                }
            }

            if (j == 109) {
                arrayOfObject = this.objTable[((Ref) this.objTable[i][6]).index];
                this.objTable[i][0] = new ScratchSound(((Number) this.objTable[i][7]).intValue(), (byte[]) arrayOfObject[0]);
            }
        }
    }

    private int[] decodePixels(Object paramObject)
            throws IOException {
        if ((paramObject instanceof int[])) {
            return (int[]) paramObject;
        }

        DataInputStream localDataInputStream = new DataInputStream(new ByteArrayInputStream((byte[]) paramObject));
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
                //for (i3 = 0; i3 < m; ) { raster[(classID++)] = i2; i3++; continue;
                //TODO

                /* i2 = localDataInputStream.readInt();
                for (i3 = 0; i3 < m; ) { raster[(classID++)] = i2; i3++; //continue;
                
                for (i3 = 0; i3 < m; i3++) {
                i2 = localDataInputStream.readUnsignedByte() << 24;
                i2 |= localDataInputStream.readUnsignedByte() << 16;
                i2 |= localDataInputStream.readUnsignedByte() << 8;
                i2 |= localDataInputStream.readUnsignedByte();
                raster[(classID++)] = i2;
                } }
                 */
                //}

                case 2:
                case 3:
            }
        }
        return arrayOfInt;
    }

    private int decodeInt(DataInputStream paramDataInputStream)
            throws IOException {
        int i = paramDataInputStream.readUnsignedByte();
        if (i <= 223) {
            return i;
        }
        if (i <= 254) {
            return (i - 224) * 256 + paramDataInputStream.readUnsignedByte();
        }
        return paramDataInputStream.readInt();
    }

    private int[] rasterAddAlphaTo32(int[] pixels) {
        for (int i = 0; i < pixels.length; i++) {
            int j = pixels[i];
            if (j == 0) {
                continue;
            }
            pixels[i] = (0xFF000000 | j);
        }
        return pixels;
    }

    private int[] raster16to32(int[] pixels, int width, int height) {
        int[] raster = new int[width * height];
        int i2 = (width + 1) / 2;
        for (int i3 = 0; i3 < height; i3++) {
            int i = 16;
            for (int i4 = 0; i4 < width; i4++) {
                int j = pixels[(i3 * i2 + i4 / 2)] >> i & 0xFFFF;
                int k = (j >> 10 & 0x1F) << 3;
                int m = (j >> 5 & 0x1F) << 3;
                int n = (j & 0x1F) << 3;
                int i1 = k + m + n == 0 ? 0 : 0xFF000000 | k << 16 | m << 8 | n;
                raster[(i3 * width + i4)] = i1;
                i = i == 16 ? 0 : 16;
            }
        }
        return raster;
    }

    private byte[] rasterToByteRaster(int[] raster, int width, int height, int paramInt3) {
        byte[] bytes = new byte[width * height];
        int i = raster.length / height;
        int j = (1 << paramInt3) - 1;
        int k = 32 / paramInt3;

        for (int m = 0; m < height; m++) {
            for (int n = 0; n < width; n++) {
                int i1 = raster[(m * i + n / k)];
                int i2 = paramInt3 * (k - n % k - 1);
                bytes[(m * width + n)] = (byte) (i1 >> i2 & j);
            }
        }
        return bytes;
    }

    private void fixSounds() {
        int i = 0;
        int k;
        for (int j = 0; j < this.objTable.length; j++) {
            k = ((Number) this.objTable[j][1]).intValue();
            if ((k == 109)
                    && (((ScratchSound) this.objTable[j][0]).isByteReversed())) {
                i = 1;
            }
        }

        if (i == 0) {
            return;
        }

        for (int j = 0; j < this.objTable.length; j++) {
            k = ((Number) this.objTable[j][1]).intValue();
            if (k == 109) {
                ((ScratchSound) this.objTable[j][0]).reverseBytes();
            }
        }
    }

    private void uncompressMedia() throws IOException {
        for (int i = 0; i < this.objTable.length; i++) {
            Object[] arrayOfObject = this.objTable[i];
            int j = ((Number) arrayOfObject[1]).intValue();
            int k = -1;
            if (j >= 100) {
                k = ((Number) arrayOfObject[2]).intValue();
            }

            if ((j == 162) && (k >= 4)) {
                if ((arrayOfObject[7] instanceof byte[])) {
                    BufferedImage localBufferedImage = ImageIO.read(new ByteArrayInputStream((byte[]) arrayOfObject[7]));
                    if (localBufferedImage != null) {
                        if ((arrayOfObject[4] instanceof Image)) {
                            ((Image) arrayOfObject[4]).flush();
                        }
                        arrayOfObject[4] = localBufferedImage;
                        arrayOfObject[7] = empty;
                    }
                }
                if ((arrayOfObject[8] instanceof BufferedImage)) {
                    arrayOfObject[4] = arrayOfObject[8];
                    arrayOfObject[8] = empty;
                }
            }
            if ((j != 164) || (k < 2)
                    || (!(arrayOfObject[9] instanceof byte[]))) {
                continue;
            }
            int m = ((Number) arrayOfObject[7]).intValue();
            int n = ((Number) arrayOfObject[8]).intValue();
            byte[] arrayOfByte = (byte[]) arrayOfObject[9];
            int i1 = (arrayOfByte.length * 8 + (n - 1)) / n;
            int[] arrayOfInt = new ADPCMDecoder(arrayOfByte, n).decode(i1);
            arrayOfObject[4] = new ScratchSound(m, arrayOfInt);
            boolean tmp261_260 = (arrayOfObject[9] == empty);
            arrayOfObject[8] = tmp261_260;
            arrayOfObject[7] = tmp261_260;
        }
    }

    class MapList {

        Object[] tokens;
        int offset = 0;

        MapList(Object[] paramArrayOfObject) {
            this.tokens = paramArrayOfObject;
        }

        Object next() {
            return this.tokens[(this.offset++)];
        }

        Object peek() {
            return this.tokens[this.offset];
        }

        boolean eof() {
            return this.offset == this.tokens.length;
        }
    }

    class Ref {

        int index;

        Ref(int index) {
            this.index = (index - 1);
        }

        @Override
        public String toString() {
            return "Ref(" + this.index + ")";
        }
    }
}

