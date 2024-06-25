/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pruebas;

/**
 *
 * @author Somils
 */
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioProcessor {
    private byte[] audioBytes;
    private AudioFormat format;

    public AudioProcessor(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            format = audioInputStream.getFormat();
            audioBytes = audioInputStream.readAllBytes();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public AudioFormat getFormat() {
        return format;
    }
}