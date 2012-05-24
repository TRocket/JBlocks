package org.jblocks.scratch;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

class ADPCMDecoder {

	static final int[] stepSizeTable = { 7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
			19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88,
			97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
			337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060,
			1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024,
			3327, 3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630,
			9493, 10442, 11487, 12635, 13899, 15289, 16818, 18500, 20350,
			22385, 24623, 27086, 29794, 32767 };
	static final int[] indices2bit = { -1, 2 };
	static final int[] indices3bit = { -1, -1, 2, 4 };
	static final int[] indices4bit = { -1, -1, -1, -1, 2, 4, 6, 8 };
	static final int[] indices5bit = { -1, -1, -1, -1, -1, -1, -1, -1, 1, 2, 4,
			6, 8, 10, 13, 16 };
	private final DataInputStream in;
	private final int bitsPerSample;
	private final int[] indexTable;
	private int currentByte;
	private int bitPosition;
	private int predicted;
	private int index;

	public ADPCMDecoder(byte[] data, int bits) {
		this(new ByteArrayInputStream(data), bits);
	}

	public ADPCMDecoder(InputStream in, int bits) {
		this.in = new DataInputStream(in);
		this.bitsPerSample = bits;
		switch (this.bitsPerSample) {
		case 2:
			this.indexTable = indices2bit;
			break;
		case 3:
			this.indexTable = indices3bit;
			break;
		case 4:
			this.indexTable = indices4bit;
			break;
		case 5:
			this.indexTable = indices5bit;
			break;
		default:
			throw new IllegalArgumentException("bitsPerSample < 2 or > 5");
		}
	}

	public int[] decode(int length) {
		final int bitField = 1 << this.bitsPerSample - 1;
		final int bitFieldM1 = bitField - 1;
		final int bit2n = bitField >> 1;

		int[] decoded = new int[length];

		for (int off = 0; off < length; off++) {
			int bits = nextBits();
			int step = stepSizeTable[this.index];
			int diff = 0;
			for (int i = bit2n; i > 0; i >>= 1) {
				if ((bits & i) != 0) {
					diff += step;
				}
				step >>= 1;
			}
			diff += step;

			this.predicted += ((bits & bitField) != 0 ? -diff : diff);
			if (this.predicted > 32767) {
				this.predicted = 32767;
			}
			if (this.predicted < -32768) {
				this.predicted = -32768;
			}
			decoded[off] = this.predicted;

			this.index += this.indexTable[(bits & bitFieldM1)];
			if (this.index < 0) {
				this.index = 0;
			}
			if (this.index <= 88) {
				continue;
			}
			this.index = 88;
		}
		return decoded;
	}

	private int nextBits() {
		int i = 0;
		int bitcnt = this.bitsPerSample;
		while (true) {
			int k = bitcnt - this.bitPosition;
			i += (k < 0 ? this.currentByte >> -k : this.currentByte << k);
			if (k <= 0) {
				break;
			}
			bitcnt -= this.bitPosition;
			try {
				this.currentByte = this.in.readUnsignedByte();
			} catch (IOException io) {
				this.currentByte = -1;
			}
			this.bitPosition = 8;
			if (this.currentByte < 0) {
				this.bitPosition = 0;
				return i;
			}
		}
		this.bitPosition -= bitcnt;
		this.currentByte &= 255 >> 8 - this.bitPosition;
		return i;
	}
}
