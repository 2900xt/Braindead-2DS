package src.Game;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;


public class Sound {

    public static boolean soundEnable;

    public static Clip playSoundFile(final String filename)
    {
        if(!soundEnable) return null;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./Assets/audio/" + filename));
            final Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP)
                        clip.close();
                }
            });
            return clip;
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
        return null;
    }
}
