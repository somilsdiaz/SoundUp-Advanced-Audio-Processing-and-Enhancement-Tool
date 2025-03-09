package PDA;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Utility class for audio file operations and format conversions.
 */
public class AudioUtils {
    
    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    /**
     * Converts a byte array to a double array based on the audio format
     */
    public static double[] byteArrayToDoubleArray(byte[] audioBytes, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int numSamples = audioBytes.length / bytesPerSample;
        double[] audioData = new double[numSamples];
        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < numSamples; i++) {
            int sample = switch (bytesPerSample) {
                case 1 -> byteBuffer.get() & 0xFF;
                case 2 -> byteBuffer.getShort();
                case 3 -> {
                    int s = byteBuffer.get() & 0xFF;
                    s |= (byteBuffer.get() & 0xFF) << 8;
                    s |= (byteBuffer.get() << 16);
                    yield s;
                }
                case 4 -> byteBuffer.getInt();
                default -> 0; // Handle unexpected bytesPerSample
            };

            audioData[i] = sample / Math.pow(2, format.getSampleSizeInBits() - 1);
        }

        return audioData;
    }

    /**
     * Converts a double array to a byte array based on the audio format
     */
    public static byte[] doubleArrayToByteArray(double[] audioData, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        byte[] audioBytes = new byte[audioData.length * bytesPerSample];
        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < audioData.length; i++) {
            int sample = (int) (audioData[i] * Math.pow(2, format.getSampleSizeInBits() - 1));

            switch (bytesPerSample) {
                case 1 -> byteBuffer.put((byte) (sample & 0xFF));
                case 2 -> byteBuffer.putShort((short) sample);
                case 3 -> {
                    byteBuffer.put((byte) (sample & 0xFF));
                    byteBuffer.put((byte) ((sample >> 8) & 0xFF));
                    byteBuffer.put((byte) ((sample >> 16) & 0xFF));
                }
                case 4 -> byteBuffer.putInt(sample);
                default -> { /* Handle unexpected bytesPerSample */ }
            }
        }

        return audioBytes;
    }

    /**
     * Reads all bytes from an audio input stream
     */
    public static byte[] readAllBytes(AudioInputStream audioInputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tempBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
            buffer.write(tempBuffer, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    /**
     * Writes enhanced audio data to an output file
     */
    public static void writeEnhancedAudio(byte[] enhancedBytes, AudioFormat format, File outputFile) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(enhancedBytes); 
             AudioInputStream enhancedAudioInputStream = new AudioInputStream(bais, format, enhancedBytes.length / format.getFrameSize())) {
            AudioSystem.write(enhancedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }

        System.out.println("Audio enhancement complete. Output saved to: " + outputFile.getAbsolutePath());
    }

    /**
     * Mixes two audio files into an output file
     */
    public static void mixAudioFiles(File inputFile1, File inputFile2, File outputFile) throws UnsupportedAudioFileException, IOException {
        try (AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(inputFile1); 
             AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(inputFile2)) {

            AudioFormat format1 = audioInputStream1.getFormat();
            AudioFormat format2 = audioInputStream2.getFormat();

            if (!format1.matches(format2)) {
                throw new UnsupportedAudioFileException("Los formatos de los archivos de audio no coinciden.");
            }

            byte[] audioBytes1 = readAllBytes(audioInputStream1);
            byte[] audioBytes2 = readAllBytes(audioInputStream2);

            int maxLength = Math.max(audioBytes1.length, audioBytes2.length);
            byte[] mixedBytes = new byte[maxLength];

            // Improved mixing with proper level control to prevent saturation
            if (format1.getSampleSizeInBits() == 16) {
                // For 16-bit audio (most common)
                for (int i = 0; i < maxLength; i += 2) {
                    if (i + 1 < maxLength) {
                        short sample1 = 0;
                        if (i < audioBytes1.length - 1) {
                            sample1 = (short) ((audioBytes1[i] & 0xFF) | (audioBytes1[i + 1] << 8));
                        }
                        
                        short sample2 = 0;
                        if (i < audioBytes2.length - 1) {
                            sample2 = (short) ((audioBytes2[i] & 0xFF) | (audioBytes2[i + 1] << 8));
                        }
                        
                        // Mix with level control (0.7 factor to prevent clipping)
                        float mixFactor = 0.7f;
                        int mixedSample = (int)((sample1 * mixFactor) + (sample2 * mixFactor));
                        
                        // Apply hard limiting to prevent clipping
                        if (mixedSample > Short.MAX_VALUE) mixedSample = Short.MAX_VALUE;
                        if (mixedSample < Short.MIN_VALUE) mixedSample = Short.MIN_VALUE;
                        
                        mixedBytes[i] = (byte) (mixedSample & 0xFF);
                        mixedBytes[i + 1] = (byte) ((mixedSample >> 8) & 0xFF);
                    }
                }
            } else {
                // For 8-bit audio or other formats, use simpler mixing with limiting
                for (int i = 0; i < maxLength; i++) {
                    int sample1 = i < audioBytes1.length ? audioBytes1[i] : 0;
                    int sample2 = i < audioBytes2.length ? audioBytes2[i] : 0;

                    // Mix with level control
                    float mixFactor = 0.7f;
                    int mixedSample = (int)((sample1 * mixFactor) + (sample2 * mixFactor));
                    
                    // Apply limiting
                    mixedSample = Math.min(mixedSample, Byte.MAX_VALUE);
                    mixedSample = Math.max(mixedSample, Byte.MIN_VALUE);

                    mixedBytes[i] = (byte) mixedSample;
                }
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(mixedBytes); 
                 AudioInputStream mixedAudioInputStream = new AudioInputStream(bais, format1, mixedBytes.length / format1.getFrameSize())) {
                AudioSystem.write(mixedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
            }

            System.out.println("Audio mix complete. Output saved to: " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Ensure the temporary directory exists
     */
    public static void ensureTempDirectory() throws IOException {
        if (!Files.exists(tempFilesDirectory)) {
            Files.createDirectory(tempFilesDirectory);
        }
    }
} 