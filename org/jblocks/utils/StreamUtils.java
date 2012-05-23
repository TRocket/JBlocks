package org.jblocks.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

/**
 *
 * Utilities for streams and files. <br />
 * 
 * @author ZeroLuck
 */
public class StreamUtils {

    /**
     * Packs a <code>{@literal  Map<String, byte[]>}</code> to a JAR byte array. <br />
     * The JAR will be compressed. <br />
     * 
     * @param data the Map of entrys
     * @return the Map to pack
     * @throws IOException 
     */
    public static byte[] jarPack(Map<String, byte[]> data, Manifest m) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream zip = new JarOutputStream(out, m);
        zip.setLevel(Deflater.BEST_COMPRESSION);
        for (String key : data.keySet()) {
            zip.putNextEntry(new ZipEntry(key));
            zip.write(data.get(key));
            zip.closeEntry();
        }
        zip.close();
        return out.toByteArray();
    }

    /**
     * Unpacks a JAR <code>JAR byte[]</code> to a <code>{@literal Map<String, byte[]>}</code>. <br />
     * 
     * @param data the byte[]
     * @return the files of the JAR
     * @throws IOException 
     */
    public static Map<String, byte[]> jarUnpack(byte[] data) throws IOException {
        Map<String, byte[]> files = new HashMap<String, byte[]>(100);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        JarInputStream zip = new JarInputStream(in);
        ZipEntry entry;
        byte[] buf = new byte[1024];
        while ((entry = zip.getNextEntry()) != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = zip.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            files.put(entry.getName(), out.toByteArray());
            zip.closeEntry();
        }
        zip.close();
        return files;
    }

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
