package org.jblocks.utils;

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
