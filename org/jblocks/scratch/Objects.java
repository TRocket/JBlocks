package org.jblocks.scratch;

class Objects {
	/*
	 * inline values
	 */

	static final int INLINE_VALUE_NIL = 1;
	static final int INLINE_VALUE_TRUE = 2;
	static final int INLINE_VALUE_FALSE = 3;
	static final int INLINE_VALUE_SMALLINTEGER = 4;
	static final int INLINE_VALUE_SMALLINTEGER16 = 5;
	static final int INLINE_VALUE_LARGEPOSITVEINTEGER = 6;
	static final int INLINE_VALUE_LARGENEGETIVEINTEGER = 7;
	static final int INLINE_VALUE_FLOAT = 8;

	/*
	 * Fixed format objects
	 */
	/*
	 * Non-pointer object
	 */
	/**
	 * length: 32-bit unsigned integer<br>
	 * bytes: length many bytes.
	 */
	static final int FIXED_FORMAT_OBJECT_STRING = 9;
	static final int FIXED_FORMAT_OBJECT_SYMBOL = 10;
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
	static final int USER_CLASS_OBJECT_MORPH = 100;
	static final int USER_CLASS_OBJECT_BORDERDMORPH = 101;
	static final int USER_CLASS_OBJECT_RECTANGLEMORPH = 102;
	static final int FUSER_CLASS_OBJECT_ELLIPSEMORPH = 103;
	static final int USER_CLASS_OBJECT_ALIGNMENTMORPH = 104;
	static final int USER_CLASS_OBJECT_STRINGMORPH = 105;
	static final int USER_CLASS_OBJECT_UPDATINGSTRINGMORPH = 106;
	static final int USER_CLASS_OBJECT_SIMPLESLIDERMORPH = 107;
	static final int USER_CLASS_OBJECT_SIMPLEBUTTONMORPH = 108;
	static final int USER_CLASS_OBJECT_SAMPLEDSOUND = 109;
	static final int USER_CLASS_OBJECT_IMAGEMORPH = 110;
	static final int USER_CLASS_OBJECT_SKETCHMORPH = 111;
	/*
	 * Scratch-specific classes
	 */
	static final int USER_CLASS_OBJECT_SENSORBOARDMORPH = 123;
	static final int FIXED_FORMAT_OBJECT_SCRATCHSPRITEMORPH = 124;
	static final int FIXED_FORMAT_OBJECT_SCRATCHSTAGEMORPH = 125;
	static final int USER_CLASS_OBJECT_CHOICEARGMORPH = 140;
	static final int USER_CLASS_OBJECT_COLORARGMORPH = 141;
	static final int USER_CLASS_OBJECT_EXPRESSIONARGMORPH = 142;
	static final int USER_CLASS_OBJECT_SPRITEARGMORPH = 145;
	static final int USER_CLASS_OBJECT_BLOCKMORPH = 147;
	static final int USER_CLASS_OBJECT_COMMANDBLOCKMORPH = 148;
	static final int USER_CLASS_OBJECT_CBLOCKMORPH = 149;
	static final int USER_CLASS_OBJECT_HATBLOCKMORPH = 151;
	static final int USER_CLASS_OBJECT_SCRATCHSCRIPTSMORPH = 153;
	static final int USER_CLASS_OBJECT_SCRATCHSLIDERMORPH = 154;
	static final int USER_CLASS_OBJECT_WATHCHERMORPH = 155;
	static final int USER_CLASS_OBJECT_SETTERBLOCKMORPH = 157;
	static final int USER_CLASS_OBJECT_EVENTHATMORPH = 158;
	static final int USER_CLASS_OBJECT_VARIABLEBLOCKMORPH = 160;
	static final int USER_CLASS_OBJECT_SCRATCHMEDIA = 0;
	static final int USER_CLASS_OBJECT_IMAGEMEDIA = 162;
	static final int USER_CLASS_OBJECT_MOVIEMEADIA = 163;
	static final int USER_CLASS_OBJECT_SOUNDMEADIA = 164;
	static final int USER_CLASS_OBJECT_KEYEVENTHATMORPH = 165;
	static final int USER_CLASS_OBJECT_BOOLEANARGMORPH = 166;
	static final int USER_CLASS_OBJECT_EVENTTITLEMORPH = 167;
	static final int USER_CLASS_OBJECTMOUSECLICKEVENTHATMORPH = 168;
	static final int USER_CLASS_OBJECT_EXPRESSIONARGMORPHWITHMENU = 169;
	static final int USER_CLASS_OBJECT_REPORTERBLOCKMORPH = 170;
	static final int USER_CLASS_OBJECT_MULTILINESTRINGMORPH = 171;
	static final int USER_CLASS_OBJECT_TOGGLEBUTTON = 172;
	static final int USER_CLASS_OBJECT_WATCHERREADOUTNAMEMORPH = 173;
	static final int USER_CLASS_OBJECT_WATCHERSLIDERMORPH = 174;
	static final int USER_CLASS_OBJECT_SCRATCHLISTMORPH = 175;
	static final int USER_CLASS_OBJECT_SCROLLINGTRINGMORPH = 178;

	static final byte[] macRomanToISOLatin = { -60, -59, -57, -55, -47, -42,
			-36, -31, -32, -30, -28, -29, -27, -25, -23, -24, -22, -21, -19,
			-20, -18, -17, -15, -13, -14, -12, -10, -11, -6, -7, -5, -4, -122,
			-80, -94, -93, -89, -107, -74, -33, -82, -87, -103, -76, -88, -128,
			-58, -40, -127, -79, -118, -115, -91, -75, -114, -113, -112, -102,
			-99, -86, -70, -98, -26, -8, -65, -95, -84, -90, -125, -83, -78,
			-85, -69, -123, -96, -64, -61, -43, -116, -100, -106, -105, -109,
			-108, -111, -110, -9, -77, -1, -97, -71, -92, -117, -101, -68, -67,
			-121, -73, -126, -124, -119, -62, -54, -63, -53, -56, -51, -50,
			-49, -52, -45, -44, -66, -46, -38, -37, -39, -48, -120, -104, -81,
			-41, -35, -34, -72, -16, -3, -2 };
}
