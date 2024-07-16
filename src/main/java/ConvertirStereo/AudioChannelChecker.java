/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConvertirStereo;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Somils
 */
public class AudioChannelChecker {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java AudioChannelChecker <audio-file>");
            System.exit(1);
        }

        File audioFile = new File(args[0]);
        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audioFile);
            AudioFormat format = fileFormat.getFormat();
            int channels = format.getChannels();

            if (channels == 1) {
                System.out.println("The audio file is mono.");
            } else if (channels == 2) {
                System.out.println("The audio file is stereo.");
            } else {
                System.out.println("The audio file has " + channels + " channels.");
            }
        } catch (IOException | UnsupportedAudioFileException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        }
    }
}
