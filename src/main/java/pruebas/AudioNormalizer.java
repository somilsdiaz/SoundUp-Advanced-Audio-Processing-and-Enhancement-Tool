package pruebas;

import java.io.BufferedInputStream;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class AudioNormalizer {

    private byte[] audioBytes;
    private AudioFormat format;
    private static AdvancedPlayer mp3Player;
    private static SourceDataLine audioLine;
    private static Thread playbackThread;
    private static boolean isPlaying = false;

    public AudioNormalizer(URL filePath) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/resources/excusa.wav");
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
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
        int duracion = 0;
        try {
            File file = new File(ruta);
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

    public static void reproducirCancion(String filePath) {
        detenerCancion(); // Asegurarse de detener cualquier reproducción anterior

        playbackThread = new Thread(() -> {
            try {
                File file = new File(filePath);
                String extension = getFileExtension(file);

                if (extension.equalsIgnoreCase("mp3")) {
                    try {
                        reproducirMP3(file);
                    } catch (JavaLayerException ex) {
                        Logger.getLogger(AudioNormalizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    reproducirConJavaxSound(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(AudioNormalizer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(AudioNormalizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        playbackThread.start();
    }

    private static void reproducirMP3(File file) throws JavaLayerException, IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            mp3Player = new AdvancedPlayer(inputStream);
            mp3Player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    isPlaying = false;
                    System.out.println("Playback finished.");
                }
            });
            isPlaying = true;
            mp3Player.play();
        }
    }

    private static void reproducirConJavaxSound(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(format);
        audioLine.start();

        byte[] bytesBuffer = new byte[4096];
        int bytesRead;

        isPlaying = true;
        while ((bytesRead = audioInputStream.read(bytesBuffer)) != -1 && isPlaying) {
            audioLine.write(bytesBuffer, 0, bytesRead);
        }

        audioLine.drain();
        audioLine.close();
        audioInputStream.close();
        isPlaying = false;
    }

    public static void detenerCancion() {
        if (mp3Player != null) {
            mp3Player.close();
        }
        if (audioLine != null) {
            audioLine.stop();
            audioLine.close();
        }
        isPlaying = false;
    }

    public static void restablecerCancion(String filePath) {
        detenerCancion();
        reproducirCancion(filePath);
    }

    public static void finalizarProcesoCancion(String filePath) {
        detenerCancion();
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }
        playbackThread = null;
        System.gc(); // Forzar la recolección de basura para liberar recursos
    }

    public static void reproducirCancionDesde(String filePath, int segundos) {
        detenerCancion(); // Asegurarse de detener cualquier reproducción anterior

        playbackThread = new Thread(() -> {
            try {
                File file = new File(filePath);
                String extension = getFileExtension(file);

                if (extension.equalsIgnoreCase("mp3")) {
                    reproducirMP3Desde(file, segundos);
                } else {
                    reproducirConJavaxSoundDesde(file, segundos);
                }
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | JavaLayerException e) {
                e.printStackTrace();
            }
        });
        playbackThread.start();
    }

    private static void reproducirMP3Desde(File file, int segundos) throws JavaLayerException, IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            Bitstream bitstream = new Bitstream(inputStream);
            int frameSize = 0;
            int frameCount = 0;
            int framesToSkip = segundos * 38; // Aproximadamente 38 frames por segundo
            while (frameCount < framesToSkip) {
                bitstream.readFrame();
                frameCount++;
            }

            mp3Player = new AdvancedPlayer(inputStream);
            mp3Player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    isPlaying = false;
                    System.out.println("Playback finished.");
                }
            });
            isPlaying = true;
            mp3Player.play();
        }
    }

    private static void reproducirConJavaxSoundDesde(File file, int segundos) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioInputStream.getFormat();
        long framesToSkip = (long) (segundos * format.getFrameRate());
        audioInputStream.skip(framesToSkip * format.getFrameSize());

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        audioLine = (SourceDataLine) AudioSystem.getLine(info);
        audioLine.open(format);
        audioLine.start();

        byte[] bytesBuffer = new byte[4096];
        int bytesRead;

        isPlaying = true;
        while ((bytesRead = audioInputStream.read(bytesBuffer)) != -1 && isPlaying) {
            audioLine.write(bytesBuffer, 0, bytesRead);
        }

        audioLine.drain();
        audioLine.close();
        audioInputStream.close();
        isPlaying = false;
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

}
