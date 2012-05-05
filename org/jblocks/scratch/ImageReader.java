package org.jblocks.scratch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.Vector;

import org.jblocks.scratch.ObjReader.Ref;

public class ImageReader {
	static IndexColorModel squeakColorMap(int paramInt)
	   {
	     int i = paramInt == 1 ? -1 : 0;
	     return new IndexColorModel(paramInt, 256, ObjReader.squeakColors, 0, false, i);
	   }
	 
	  static  IndexColorModel customColorMap(int paramInt, Object[] paramArrayOfObject, Object[][] objTable) {
	     byte[] arrayOfByte = new byte[4 * paramArrayOfObject.length];
	     int i = 0;
	     for (int j = 0; j < paramArrayOfObject.length; j++) {
	       Color localColor = (Color)objTable[((Ref)paramArrayOfObject[j]).index][0];
	       arrayOfByte[(i++)] = (byte)localColor.getRed();
	       arrayOfByte[(i++)] = (byte)localColor.getGreen();
	       arrayOfByte[(i++)] = (byte)localColor.getBlue();
	       arrayOfByte[(i++)] = (byte)localColor.getAlpha();
	     }
	     return new IndexColorModel(paramInt, paramArrayOfObject.length, arrayOfByte, 0, true, 0);
	   }
	  
	  static BufferedImage jpegDecode(byte[] paramArrayOfByte)
	   {
	     Toolkit localToolkit = Toolkit.getDefaultToolkit();
	     Image localImage = localToolkit.createImage(paramArrayOfByte);
	     int i = localImage.getWidth(null);
	     int j = localImage.getHeight(null);
	     BufferedImage localBufferedImage = new BufferedImage(i, j, 2);
	     Graphics localGraphics = localBufferedImage.getGraphics();
	     localGraphics.drawImage(localImage, 0, 0, i, j, null);
	     localGraphics.dispose();
	     localImage.flush();
	     return localBufferedImage;
	   }
	  
	 
		 
}
