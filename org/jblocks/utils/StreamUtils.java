package org.jblocks.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * Utilities for streams and files. <br />
 * 
 * @author ZeroLuck
 */
public class StreamUtils {

    /**
     * Adds the file extension to a file-name if it is necessary. <br />
     * 
     * @param file - the filename
     * @param ext - the extension for the file
     * @return - the created filename.
     */
    public static String addFileExtension(String file, String ext) {
        if (!file.toLowerCase().endsWith(ext.toLowerCase())) {
            return file + "." + ext;
        }
        return file;
    }

    /**
     * Creates, if the OutputStream isn't already a BufferedOutputStream,<br />
     * a new buffered version. <br />
     * 
     * @param out - the OutputStream
     */
    public static OutputStream createBuffered(OutputStream out) {
        if (out instanceof BufferedOutputStream) {
            return out;
        }
        return new BufferedOutputStream(out);
    }

    /**
     * Creates, if the InputStream isn't already a BufferedInputStream,<br />
     * a new buffered version. <br />
     * 
     * @param in - the InputStream
     */
    public static InputStream createBuffered(InputStream in) {
        if (in instanceof BufferedInputStream) {
            return in;
        }
        return new BufferedInputStream(in);
    }

    /**
     * Closes the InputStream. <br />
     * 
     * @param in - the InputStream to close.
     */
    public static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ex) {
                // ???
            }
        }
    }

    /**
     * Copies the contents of an InputStream to an OutputStream. <br />
     * 
     * @param in - the InputStream
     * @param out - the OutputStream
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.flush();
    }

    /**
     * Closes the OutputStream.
     * 
     * @param out - the OutputStream
     */
    public static void safeClose(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
                // ???
            }
        }
    }
}
