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
import java.io.File;
import java.io.IOException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import org.jaudiotagger.tag.TagException;
public class AudioNormalizer {

    private byte[] audioBytes;
    private AudioFormat format;

    public AudioNormalizer(String filePath) {
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

    public static int DuracionCancion(String ruta) throws TagException {
        int duracion=0;
        try {
            File file = new File(ruta); // Especifica la ruta a tu archivo de audio
            AudioFile audioFile = AudioFileIO.read(file);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            int durationInSeconds = audioHeader.getTrackLength();
            duracion = durationInSeconds;
            System.out.println("Duración: " + durationInSeconds + " segundos");
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }
        return duracion;

    }
}
