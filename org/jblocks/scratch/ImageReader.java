package org.jblocks.scratch;

import java.awt.Color;
import java.awt.image.IndexColorModel;

import org.jblocks.scratch.ObjReader.Ref;

class ImageReader {

	private ImageReader() {
	}

	static IndexColorModel squeakColorMap(int paramInt) {
		int i = paramInt == 1 ? -1 : 0;
		return new IndexColorModel(paramInt, 256, ObjReader.squeakColors, 0,
				false, i);
	}

	static IndexColorModel customColorMap(int paramInt,
			Object[] paramArrayOfObject, Object[][] objTable) {
		byte[] arrayOfByte = new byte[4 * paramArrayOfObject.length];
		int i = 0;
		for (int j = 0; j < paramArrayOfObject.length; j++) {
			Color localColor = (Color) objTable[((Ref) paramArrayOfObject[j]).index][0];
			arrayOfByte[(i++)] = (byte) localColor.getRed();
			arrayOfByte[(i++)] = (byte) localColor.getGreen();
			arrayOfByte[(i++)] = (byte) localColor.getBlue();
			arrayOfByte[(i++)] = (byte) localColor.getAlpha();
		}
		return new IndexColorModel(paramInt, paramArrayOfObject.length,
				arrayOfByte, 0, true, 0);
	}
}
