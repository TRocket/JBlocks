package org.jblocks.sound;

import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author ZeroLuck
 */
public class Test {

    public static void main(String[] args) throws Exception {
        // @TRocket
        // we shouldn't put Audio-Files in the project.
        // better to store them external (maybe on a server?), because of SVN...
        
        SoundInput inp = SoundInput.fromStream(AudioSystem.getAudioInputStream(new File("C:/JTest/test.wav"))); 
        SoundInput inp2 = SoundInput.fromStream(AudioSystem.getAudioInputStream(new File("C:/JTest/test2.wav")));
        
        SoftwareMixer mix = new SoftwareMixer(inp.getFormat());
        mix.addInput(inp);
        mix.addInput(inp2);
        
        SourceDataLine line = AudioSystem.getSourceDataLine(inp.getFormat());
        line.open();
        line.start();
        int len;
        byte[] buf = new byte[line.getBufferSize()];
        while ((len = mix.read(buf, 0, buf.length)) != -1) {
            int off = 0;
            while (off < len) {
                off += line.write(buf, off, len - off);
            }
        }
        mix.close();
    }
}
