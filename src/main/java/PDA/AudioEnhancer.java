package PDA;

import RMS.AudioEnhanceFile;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.effects.FlangerEffect;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Arrays;

/**
 * Main class for enhancing audio with professional-grade processing.
 */
public class AudioEnhancer {

    // Remove unused audio processing constants

    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    // Default enhancement settings
    private static float bassBoost = 1.2f;      // Reduced from 1.5f to prevent saturation
    private static float midPresence = 1.1f;    // Reduced from 1.2f to prevent harshness
    private static float highClarity = 1.15f;   // Reduced from 1.3f to prevent harshness
    private static float stereoWidth = 1.2f;    // Reduced from 1.4f to prevent phase issues
    private static float dynamicRange = 0.8f;   // Increased from 0.7f for less compression
    private static float spatialDepth = 1.1f;   // Reduced from 1.3f to prevent echo
    private static float noiseReduction = 0.85f; // Noise floor reduction factor

    /**
     * Enhanced audio processing method
     */
    public static void enhanceAudio(File inputFile, File outputFile) throws UnsupportedAudioFileException, IOException {
        AudioFormat format;
        double[] audioData;

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile)) {
            format = audioInputStream.getFormat();
            byte[] audioBytes = audioInputStream.readAllBytes();
            audioData = AudioUtils.byteArrayToDoubleArray(audioBytes, format);
        }

        // Apply noise reduction first to clean up the signal
        float[] floatAudioData = new float[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            floatAudioData[i] = (float) audioData[i];
        }
        
        // Apply float-based processing chain using our new classes
        floatAudioData = NoiseProcessor.applyNoiseReduction(floatAudioData, format, noiseReduction);
        floatAudioData = FilterProcessor.applyMultiBandEQ(floatAudioData, format, bassBoost, midPresence, highClarity);
        floatAudioData = NoiseProcessor.applyHarmonicExciter(floatAudioData, format, highClarity);
        
        // Only apply spatial enhancement for stereo files
        if (format.getChannels() == 2) {
            floatAudioData = SpatialProcessor.applySpatialEnhancement(floatAudioData, format, spatialDepth);
            floatAudioData = SpatialProcessor.enhanceStereoField(floatAudioData, format, stereoWidth);
        }
        
        floatAudioData = DynamicsProcessor.applyDynamicCompression(floatAudioData, format, dynamicRange);
        
        // Add final limiter to prevent any clipping
        floatAudioData = DynamicsProcessor.applyFinalLimiter(floatAudioData);
        
        // Convert back to double array
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = floatAudioData[i];
        }

        // Continue with double-based processing - with reduced gains
        NoiseProcessor.applyHarmonicExciter(audioData, format.getSampleRate());
        DynamicsProcessor.applyMultibandCompression(audioData, format.getSampleRate());
        
        // Apply band-pass filter for vocal clarity (1kHz-5kHz range) - reduced gain
        FilterProcessor.applyBandPassFilter(audioData, format.getSampleRate(), 1000.0, 5000.0, 1.5);
        
        if (format.getChannels() == 2) {
            SpatialProcessor.applyStereoWidening(audioData);
        }
        
        // Stage 2: Presence and Clarity - reduced gains
        FilterProcessor.applyParametricEQ(audioData, format.getSampleRate(), 5000.0, 1.2, 0.7); // Presence
        FilterProcessor.applyParametricEQ(audioData, format.getSampleRate(), 2500.0, 1.5, 0.5); // Clarity
        
        // Stage 1: Frequency Balance - reduced gains
        FilterProcessor.applyLowShelfFilter(audioData, format.getSampleRate(), 120.0, 2.0); // Warm bass
        FilterProcessor.applyHighShelfFilter(audioData, format.getSampleRate(), 12000.0, 1.5); // Air and brightness
        
        // Apply final hard limiting to prevent any clipping
        for (int i = 0; i < audioData.length; i++) {
            if (audioData[i] > 0.95) audioData[i] = 0.95;
            if (audioData[i] < -0.95) audioData[i] = -0.95;
        }
        
        byte[] enhancedBytes = AudioUtils.doubleArrayToByteArray(audioData, format);
        AudioUtils.writeEnhancedAudio(enhancedBytes, format, outputFile);
    }

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
     * Normalize audio volume with moderate gain
     */
    public static String normalizeAudioVolume(File audioFile, File originalFile) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 2);
            AudioEnhanceFile.RMSProcessor rmsProcessor = new AudioEnhanceFile.RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);

            dispatcher.run();
            AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            
            // Apply a moderate gain instead of aggressive gain
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.0f));
            
            // Reduce the final gain to prevent saturation
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.2f));
            
            File normalizedTempFile = new File(audioFile.getParent() + "/normalized_" + audioFile.getName());
            WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), normalizedTempFile.getAbsolutePath());
            normalizationDispatcher.addAudioProcessor(writer);

            normalizationDispatcher.run();

            return normalizedTempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void mixAudioFiles(File inputFile1, File inputFile2, File outputFile) throws UnsupportedAudioFileException, IOException {
        try (AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(inputFile1); AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(inputFile2)) {

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

            try (ByteArrayInputStream bais = new ByteArrayInputStream(mixedBytes); AudioInputStream mixedAudioInputStream = new AudioInputStream(bais, format1, mixedBytes.length / format1.getFrameSize())) {
                AudioSystem.write(mixedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
            }

            System.out.println("Audio mix complete. Output saved to: " + outputFile.getAbsolutePath());
        }
    }

    private static byte[] readAllBytes(AudioInputStream audioInputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tempBuffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
            buffer.write(tempBuffer, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    /**
     * Delete temporary PDA files
     */
    public static void eliminarArchivosPDA() {
        try {
            String directoryPath = tempFilesDirectory.toString();
            Files.walkFileTree(Paths.get(directoryPath), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith("PDA_temp_")) {
                        Files.delete(file);
                        System.out.println("Deleted: " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Path localTempDir = Paths.get(System.getProperty("user.home"), "tempfiles");
        if (!Files.exists(localTempDir)) {
            Files.createDirectory(localTempDir);
        }

        String AudioOriginal = AudioEnhanceFile.convertToWavString("C:/Users/Somils/Music/SALSAS/excusa.mp3");
        File AudioOriginalWav = new File(AudioOriginal);
        String inputFileName = AudioOriginalWav.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));

        String mascaraAudioPDApatch = localTempDir.toString() + "/pdaMask_" + inputFileNameWithoutExtension + ".wav";
        File mascaraAudioPDA = new File(mascaraAudioPDApatch);

        try {
            // Enhance the audio
            enhanceAudio(AudioOriginalWav, mascaraAudioPDA);

            // Normalize the enhanced audio
            String normalizedMaskPath = normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav);
            File normalizedMaskFile = new File(normalizedMaskPath);

            // Convert the normalized audio to WAV format
            String normalizedTempPath = AudioEnhanceFile.convertToWavString(normalizedMaskFile.getAbsolutePath());
            File AudioMidlePDA = new File(normalizedTempPath);

            // Mix the normalized audio with the original audio
            String AudioPDApatch = localTempDir.toString() + "/PDA_" + inputFileNameWithoutExtension + ".wav";
            File AudioPDA = new File(AudioPDApatch);
            mixAudioFiles(AudioMidlePDA, AudioOriginalWav, AudioPDA);

            // Clean up temporary files
            RMS.AudioEnhanceFile.eliminarArchivo(mascaraAudioPDApatch);
            RMS.AudioEnhanceFile.eliminarArchivo(normalizedTempPath);
            RMS.AudioEnhanceFile.eliminarArchivo(normalizedMaskPath);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure enhancement parameters
     * 
     * @param bass Bass enhancement (1.0 = neutral)
     * @param mid Mid-range enhancement (1.0 = neutral)
     * @param high High-frequency enhancement (1.0 = neutral)
     * @param stereo Stereo width enhancement (1.0 = neutral, stereo only)
     * @param dynamics Dynamic range (0-1, lower = more compression)
     * @param spatial Spatial depth (1.0 = neutral, stereo only)
     * @param noiseFloor Noise reduction amount (0-1)
     */
    public static void configureEnhancement(float bass, float mid, float high, 
                                           float stereo, float dynamics, 
                                           float spatial, float noiseFloor) {
        bassBoost = bass;
        midPresence = mid;
        highClarity = high;
        stereoWidth = stereo;
        dynamicRange = Math.max(0.1f, Math.min(1.0f, dynamics)); // Clamp between 0.1 and 1.0
        spatialDepth = spatial;
        noiseReduction = noiseFloor;
    }
}
