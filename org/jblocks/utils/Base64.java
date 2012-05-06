package org.jblocks.utils;

import java.io.ByteArrayOutputStream;

/**
 * A Base64 encoder and decoder. <br />
 * 
 * @author ZeroLuck
 */
public class Base64 {

    private static final char[] base =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int BITS = 6;

    private static int index(char c) {
        for (int i = 0; i < base.length; i++) {
            if (base[i] == c) {
                return i;
            }
        }
        throw new RuntimeException();
    }

    /**
     * Encodes bytes to base64. <br />
     * See {@link #decode(java.lang.String)}
     * 
     * @param data the bytes to encode
     * @return the encoded base64 string
     */
    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        int buf = 0;
        int off = 0;
        for (int b : data) {
            buf |= b << off;
            off += 8;
            while (off > BITS) {
                int idx = buf & ((1 << (BITS)) - 1);
                sb.append(base[idx]);
                buf >>= BITS;
                off -= BITS;
                buf &= ((1 << (off)) - 1);
            }
        }
        if (off > 0) {
            sb.append(base[buf & ((1 << (BITS)) - 1)]);
        }
        return sb.toString();
    }

    /**
     * Decodes a base64 string to bytes. <br />
     * See {@link #encode(byte[])}
     * 
     * @param base64 the base64 encoded string
     * @return the decoded bytes
     */
    public static byte[] decode(String base64) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(base64.length());
        char[] chars = base64.toCharArray();
        int buf = 0;
        int off = 0;
        for (int i = 0; i < chars.length; i++) {
            int idx = index(chars[i]);
            buf |= idx << off;
            off += BITS;
            while (off > 8) {
                byte b = (byte) (buf & 0xFF);
                out.write(b);
                buf >>= 8;
                off -= 8;
                buf &= ((1 << (off)) - 1);
            }
        }
        return out.toByteArray();
    }
}
