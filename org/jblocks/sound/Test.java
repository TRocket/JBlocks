package org.jblocks.sound;

import java.io.File;

import javax.sound.sampled.AudioSystem;

import org.jblocks.soundeditor.TimedSoftwareMixer;

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
        
        TimedSoftwareMixer mix = new TimedSoftwareMixer(inp.getFormat());
        mix.addTimed(inp, 0);
        mix.addTimed(inp2, 10000);
        
        SimplePlayer p = new SimplePlayer();
        p.play(mix);
    }
}
