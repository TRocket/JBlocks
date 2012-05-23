package org.jblocks.scriptengine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.jblocks.utils.Base64;
import org.jblocks.utils.StreamUtils;


/**
 *
 * @author ZeroLuck
 */
public class StorableNativeBlock extends NativeBlock {

    private final NativeBlock block;
    private final String data;

    private StorableNativeBlock(NativeBlock block, String data) {
        super(block.getParameterCount(), block.getID());
        this.data = data;
        this.block = block;
    }

    @Override
    public Object evaluate(Object context, Object... param) {
        return block.evaluate(context, param);
    }

    public String getData() {
        return data;
    }

    public static String createData(Map<String, byte[]> classFiles, String main) throws IOException {
        Manifest m = new Manifest();
        Attributes attr = m.getMainAttributes();
        attr.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attr.put(Attributes.Name.MAIN_CLASS, main);

        return Base64.encode(StreamUtils.jarPack(classFiles, m));
    }

    public static StorableNativeBlock load(String data) throws
            IOException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {

        MemoryClassLoader loader = new MemoryClassLoader();
        JarInputStream in = new JarInputStream(new ByteArrayInputStream(Base64.decode(data)));
        String main = in.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);

        loader.addJar(in);
        Class<?> clazz = loader.loadClass(main);

        return new StorableNativeBlock((NativeBlock) clazz.newInstance(), data);
    }
}
/**
 *
 * @author ZeroLuck
 */
class MemoryClassLoader extends ClassLoader {

    private HashMap classes = new HashMap();
    private HashMap others = new HashMap();

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream stream = getParent().getResourceAsStream(name);
        if (stream == null) {
            byte[] buf = (byte[]) others.get(name);
            if (buf != null) {
                stream = new ByteArrayInputStream(buf);
            }
        }
        return stream;
    }

    @Override
    public URL getResource(String name) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    protected Enumeration findResources(String name) throws IOException {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        byte[] data = findClassData(name);
        if (data != null) {
            return defineClass(name, data, 0, data.length);
        } else {
            throw new ClassNotFoundException();
        }
    }

    /**
     * Adds a new JAR to this ClassLoader. 
     * This may be called at any time.
     */
    public void addJar(JarInputStream stream) throws IOException {
        try {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                String name = entry.getName();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                StreamUtils.copy(stream, out);
                stream.closeEntry();
                if (name.endsWith(".class")) {
                    classes.put(getClassName(name), out.toByteArray());
                } else {
                    others.put(name, out.toByteArray());
                }
            }
        } finally {
            StreamUtils.safeClose(stream);
        }
    }

    private byte[] findClassData(String name) {
        return (byte[]) classes.remove(name);
    }

    private static String getClassName(String fileName) {
        return fileName.substring(0, fileName.length() - 6).replace('/', '.');
    }
}
