package org.jblocks.scratch;



public class Reader {
	/*
	 * Fixed ofrmat objects
	 */
	/*
	 * Non-pointer object
	 */
	static final int FIXED_FORMAT_OBJECT_STRING = 9; 
	static final int FIXED_FORMAT_OBJECT_SYBOL = 10; 
	static final int FIXED_FORMAT_OBJECT_BYTEARRAY = 11; 
	
	static final int FIXED_FORMAT_OBJECT_SOUNDBUFFER = 12; 
	static final int FIXED_FORMAT_OBJECT_BITMAP = 13; 
	static final int FIXED_FORMAT_OBJECT_UTF8 = 14; 
	/*
	 * Collections
	 */
	static final int FIXED_FORMAT_OBJECT_ARRAY = 20; 
	static final int FIXED_FORMAT_OBJECT_ORDEREDCOLLECTION = 21; 
	static final int FIXED_FORMAT_OBJECT_SET = 22; 
	static final int FIXED_FORMAT_OBJECT_IDENTITYSET = 23;
	
	static final int FIXED_FORMAT_OBJECT_DICTIONARY = 24; 
	static final int FIXED_FORMAT_OBJECT_IDENTITYDICTIONARY = 25;
	/*
	 * Colors
	 */
	static final int FIXED_FORMAT_OBJECT_COLOR = 30; 
	static final int FIXED_FORMAT_OBJECT_TRANSLUCENTCOLOR = 31;
	/*
	 * Dimensions
	 */
	static final int FIXED_FORMAT_OBJECT_POINT = 32; 
	static final int FIXED_FORMAT_OBJECT_RECTANGLE = 33; 
	/*
	 * Forms
	 */
	static final int FIXED_FORMAT_OBJECT_FORM = 34; 
	static final int FIXED_FORMAT_OBJECT_COLORFORM = 35;
	/*
	 * 36-98 reserved
	 */
	
	
	/*
	 * User-class Objects
	 */
	/*
	 * Morphic-related
	 */
	static final int FIXED_FORMAT_OBJECT_MORPH = 100; 
	static final int FIXED_FORMAT_OBJECT_BORDERDMORPH = 101; 
	static final int FIXED_FORMAT_OBJECT_RECTANGLEMORPH = 102; 
	static final int FIXED_FORMAT_OBJECT_ELLIPSEMORPH = 103; 
	static final int FIXED_FORMAT_OBJECT_ALIGNMENTMORPH = 104; 
	static final int FIXED_FORMAT_OBJECT_STRINGMORPH = 105; 
	static final int FIXED_FORMAT_OBJECT_UPDATINGSTRINGMORPH = 106; 
	static final int FIXED_FORMAT_OBJECT_SIMPLESLIDERMORPH = 107; 
	static final int FIXED_FORMAT_OBJECT_SIMPLEBUTTONMORPH = 108; 
	static final int FIXED_FORMAT_OBJECT_SAMPLEDSOUND = 109; 
	static final int FIXED_FORMAT_OBJECT_IMAGEMORPH= 110; 
	static final int FIXED_FORMAT_OBJECT_SKETCHMORPH = 111; 
}
