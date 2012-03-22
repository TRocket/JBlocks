package org.jblocks.blockloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureClassLoader;

/**
 *
 * @author ZeroLuck
 */
public class StreamClassLoader extends SecureClassLoader {

    public final Class<?> defineClass(String name, InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[1024];
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        return super.defineClass(name, out.toByteArray(), 0, out.size());
    }
}
