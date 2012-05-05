package ext.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.jblocks.sound.SoundInput;

public class VorbisCodec {

    private static final Class<?> oggLib;

    static {
        
        ClassLoader loader = new URLClassLoader(
                new URL[]{VorbisCodec.class.getResource("ogg.dat")});
        try {
            oggLib = loader.loadClass("me.jogg.OggLib");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * 
     * Writes the specified AudioInputStream ogg-vorbis encoded to the specified OutputStream. <br />
     * WARNING: This can take much time. (some minutes) <br />
     * <br />
     * Just 44100Hz 16bits stereo is supported! <br />
     * 
     * @param in - the input stream.
     * @param out - the output stream.
     * @param quality - the encoding quality (0 to 1).
     * @throws IOException - if an exception while encoding occurs.
     */
    public static void encode(AudioInputStream in, OutputStream out, float quality) throws IOException {
        try {
            Method m = oggLib.getDeclaredMethod("a", AudioInputStream.class, OutputStream.class, float.class);
            m.setAccessible(true);
            m.invoke(null, in, out, quality);
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }

    /**
     * 
     * Generates a SoundInput stream from an specified InputStream. <br />
     * 
     * @param in - the stream
     * @throws IOException - if an exception while decoding occurs.
     * @return the generated SoundInput.
     */
    public static SoundInput decode(InputStream in) throws IOException {
        try {
            Method m = oggLib.getDeclaredMethod("a", InputStream.class);
            m.setAccessible(true);
            final Object[] ret = (Object[]) m.invoke(null, in);
            final AudioFormat fmt = new AudioFormat((Integer) (ret[0]), 16, (Integer) (ret[1]), true, false);
            final InputStream input = (InputStream) ret[2];
            // ret[0] = rate
            // ret[1] = channels
            // ret[2] = InputStream
            return new SoundInput() {

                private boolean isEoF = false;

                @Override
                public AudioFormat getFormat() {
                    return fmt;
                }

                @Override
                public boolean eof() {
                    return isEoF;
                }

                @Override
                public int read(byte[] data, int off, int len) throws IOException {
                    int cnt = input.read(data, off, len);
                    if (cnt == -1) {
                        isEoF = true;
                    }
                    return cnt;
                }

                @Override
                public void close() {
                    try {
                        input.close();
                    } catch (IOException ex) {
                    }
                }
            };
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }
}
