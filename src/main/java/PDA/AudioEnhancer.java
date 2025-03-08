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

public class AudioEnhancer {

    // Add professional audio processing constants
    private static final double WARM_BASS_FREQ = 120.0;
    private static final double PRESENCE_FREQ = 5000.0;
    private static final double AIR_FREQ = 12000.0;
    private static final double CLARITY_FREQ = 2500.0;

    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    // EQ band frequencies (Hz)
    private static final float[] EQ_BANDS = {60, 150, 400, 1000, 2500, 6000, 12000};
    
    // Default enhancement settings
    private static float bassBoost = 1.2f;      // Reduced from 1.5f to prevent saturation
    private static float midPresence = 1.1f;    // Reduced from 1.2f to prevent harshness
    private static float highClarity = 1.15f;   // Reduced from 1.3f to prevent harshness
    private static float stereoWidth = 1.2f;    // Reduced from 1.4f to prevent phase issues
    private static float dynamicRange = 0.8f;   // Increased from 0.7f for less compression
    private static float spatialDepth = 1.1f;   // Reduced from 1.3f to prevent echo
    private static float noiseReduction = 0.85f; // Noise floor reduction factor

    // Enhanced audio processing method
    public static void enhanceAudio(File inputFile, File outputFile) throws UnsupportedAudioFileException, IOException {
        AudioFormat format;
        double[] audioData;

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile)) {
            format = audioInputStream.getFormat();
            byte[] audioBytes = audioInputStream.readAllBytes();
            audioData = byteArrayToDoubleArray(audioBytes, format);
        }

        // Apply noise reduction first to clean up the signal
        float[] floatAudioData = new float[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            floatAudioData[i] = (float) audioData[i];
        }
        
        // Apply float-based processing chain
        floatAudioData = applyNoiseReduction(floatAudioData, format);
        floatAudioData = applyMultiBandEQ(floatAudioData, format);
        floatAudioData = applyHarmonicExciter(floatAudioData, format);
        
        // Only apply spatial enhancement for stereo files
        if (format.getChannels() == 2) {
            floatAudioData = applySpatialEnhancement(floatAudioData, format);
            floatAudioData = enhanceStereoField(floatAudioData, format);
        }
        
        floatAudioData = applyDynamicCompression(floatAudioData, format);
        
        // Add final limiter to prevent any clipping
        floatAudioData = applyFinalLimiter(floatAudioData);
        
        // Convert back to double array
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = floatAudioData[i];
        }

        // Continue with double-based processing - with reduced gains
        applyHarmonicExciter(audioData, format.getSampleRate());
        applyMultibandCompression(audioData, format.getSampleRate());
        
        // Apply band-pass filter for vocal clarity (1kHz-5kHz range) - reduced gain
        applyBandPassFilter(audioData, format.getSampleRate(), 1000.0, 5000.0, 1.5);
        
        if (format.getChannels() == 2) {
            applyStereoWidening(audioData);
        }
        
        // Stage 2: Presence and Clarity - reduced gains
        applyParametricEQ(audioData, format.getSampleRate(), PRESENCE_FREQ, 1.2, 0.7); // Presence
        applyParametricEQ(audioData, format.getSampleRate(), CLARITY_FREQ, 1.5, 0.5); // Clarity
        
        // Stage 1: Frequency Balance - reduced gains
        applyLowShelfFilter(audioData, format.getSampleRate(), WARM_BASS_FREQ, 2.0); // Warm bass
        applyHighShelfFilter(audioData, format.getSampleRate(), AIR_FREQ, 1.5); // Air and brightness
        
        // Apply final hard limiting to prevent any clipping
        for (int i = 0; i < audioData.length; i++) {
            if (audioData[i] > 0.95) audioData[i] = 0.95;
            if (audioData[i] < -0.95) audioData[i] = -0.95;
        }
        
        byte[] enhancedBytes = doubleArrayToByteArray(audioData, format);
        writeEnhancedAudio(enhancedBytes, format, outputFile);
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

    private static void applyHighShelfFilter(double[] audioData, float sampleRate, double cutoffFrequency, double gain) {
        int n = audioData.length;
        double A = Math.pow(10, gain / 40);
        double w0 = 2 * Math.PI * cutoffFrequency / sampleRate;
        double cosw0 = Math.cos(w0);
        double alpha = Math.sin(w0) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0 = (A + 1) + (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha;
        double a1 = -2 * ((A - 1) + (A + 1) * cosw0);
        double a2 = (A + 1) + (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha;
        double b0 = A * ((A + 1) - (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha);
        double b1 = 2 * A * ((A - 1) - (A + 1) * cosw0);
        double b2 = A * ((A + 1) - (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha);

        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    private static void applyBandPassFilter(double[] audioData, float sampleRate, double lowCutoff, double highCutoff, double gain) {
        int n = audioData.length;
        double A = Math.pow(10, gain / 40);

        // Parámetros para el filtro de paso bajo (lowCutoff)
        double w0Low = 2 * Math.PI * lowCutoff / sampleRate;
        double cosw0Low = Math.cos(w0Low);
        double alphaLow = Math.sin(w0Low) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0Low = (A + 1) - (A - 1) * cosw0Low + 2 * Math.sqrt(A) * alphaLow;
        double a1Low = 2 * ((A - 1) - (A + 1) * cosw0Low);
        double a2Low = (A + 1) - (A - 1) * cosw0Low - 2 * Math.sqrt(A) * alphaLow;
        double b0Low = A * ((A + 1) + (A - 1) * cosw0Low + 2 * Math.sqrt(A) * alphaLow);
        double b1Low = -2 * A * ((A - 1) + (A + 1) * cosw0Low);
        double b2Low = A * ((A + 1) + (A - 1) * cosw0Low - 2 * Math.sqrt(A) * alphaLow);

        // Parámetros para el filtro de paso alto (highCutoff)
        double w0High = 2 * Math.PI * highCutoff / sampleRate;
        double cosw0High = Math.cos(w0High);
        double alphaHigh = Math.sin(w0High) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0High = (A + 1) - (A - 1) * cosw0High + 2 * Math.sqrt(A) * alphaHigh;
        double a1High = 2 * ((A - 1) - (A + 1) * cosw0High);
        double a2High = (A + 1) - (A - 1) * cosw0High - 2 * Math.sqrt(A) * alphaHigh;
        double b0High = A * ((A + 1) + (A - 1) * cosw0High + 2 * Math.sqrt(A) * alphaHigh);
        double b1High = -2 * A * ((A - 1) + (A + 1) * cosw0High);
        double b2High = A * ((A + 1) + (A - 1) * cosw0High - 2 * Math.sqrt(A) * alphaHigh);

        double[] filteredData = new double[n];
        filteredData[0] = (b0Low / a0Low) * audioData[0];
        filteredData[1] = (b0Low / a0Low) * audioData[1] + (b1Low / a0Low) * audioData[0] - (a1Low / a0Low) * filteredData[0];

        for (int i = 2; i < n; i++) {
            double lowPassOutput = (b0Low / a0Low) * audioData[i] + (b1Low / a0Low) * audioData[i - 1] + (b2Low / a0Low) * audioData[i - 2]
                    - (a1Low / a0Low) * filteredData[i - 1] - (a2Low / a0Low) * filteredData[i - 2];

            double highPassOutput = (b0High / a0High) * lowPassOutput + (b1High / a0High) * filteredData[i - 1] + (b2High / a0High) * filteredData[i - 2]
                    - (a1High / a0High) * filteredData[i - 1] - (a2High / a0High) * filteredData[i - 2];

            filteredData[i] = highPassOutput;
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    private static void applyLowShelfFilter(double[] audioData, float sampleRate, double cutoffFrequency, double gain) {
        int n = audioData.length;

        // Convert gain from dB to linear scale
        double A = Math.pow(10, gain / 40); // Gain factor

        // Compute normalized angular frequency
        double w0 = 2 * Math.PI * cutoffFrequency / sampleRate;
        double cosw0 = Math.cos(w0);
        double sinw0 = Math.sin(w0);

        // Compute intermediate terms
        double alpha = sinw0 / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        // Compute filter coefficients
        double a0 = (A + 1) + (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha;
        double a1 = -2 * ((A - 1) + (A + 1) * cosw0);
        double a2 = (A + 1) + (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha;

        double b0 = A * ((A + 1) - (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha);
        double b1 = 2 * A * ((A - 1) - (A + 1) * cosw0);
        double b2 = A * ((A + 1) - (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha);

        // Normalize coefficients by a0
        double normA0 = 1 / a0;
        b0 *= normA0;
        b1 *= normA0;
        b2 *= normA0;
        a1 *= normA0;
        a2 *= normA0;

        // Apply the filter
        double[] filteredData = new double[n];
        filteredData[0] = b0 * audioData[0];
        filteredData[1] = b0 * audioData[1] + b1 * audioData[0] - a1 * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = b0 * audioData[i] + b1 * audioData[i - 1] + b2 * audioData[i - 2]
                    - a1 * filteredData[i - 1] - a2 * filteredData[i - 2];
        }

        // Copy the filtered data back to the original array
        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    public static String normalizeAudioVolume(File audioFile, File originalFile) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 2);
            AudioEnhanceFile.RMSProcessor rmsProcessor = new AudioEnhanceFile.RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);

            dispatcher.run();
            AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            
            // Apply a moderate gain instead of aggressive gain
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.0f));
            
            // Remove the flanger effect which was causing echo
            // double sampleRate = 44100.0;
            // double maxFlangerLength = 0.003;
            // double wet = 0.0;
            // double lfoFrequency = 0.25;
            // FlangerEffect flangerEffect = new FlangerEffect(maxFlangerLength, wet, sampleRate, lfoFrequency);
            // normalizationDispatcher.addAudioProcessor(flangerEffect);
            
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

    // New professional-grade processing methods
    private static void applyParametricEQ(double[] audioData, float sampleRate, double centerFreq, double gain, double q) {
        double w0 = 2.0 * Math.PI * centerFreq / sampleRate;
        double alpha = Math.sin(w0) / (2.0 * q);
        double A = Math.pow(10.0, gain / 40.0);

        double b0 = 1.0 + alpha * A;
        double b1 = -2.0 * Math.cos(w0);
        double b2 = 1.0 - alpha * A;
        double a0 = 1.0 + alpha / A;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha / A;

        applyBiquadFilter(audioData, b0, b1, b2, a0, a1, a2);
    }

    private static void applyMultibandCompression(double[] audioData, float sampleRate) {
        // Split into frequency bands
        int n = audioData.length;
        double[] lowBand = new double[n];
        double[] midBand = new double[n];
        double[] highBand = new double[n];
        
        // Crossover frequencies
        double lowCrossover = 200.0;
        double highCrossover = 2000.0;
        
        // Split the audio into frequency bands
        splitBands(audioData, lowBand, midBand, highBand, sampleRate, lowCrossover, highCrossover);
        
        // Apply compression to each band with different settings
        double threshold = -24.0;  // dB
        double ratio = 4.0;        // compression ratio
        double attackTime = 0.005; // 5ms
        double releaseTime = 0.050; // 50ms
        
        // Apply different compression settings to each band
        compressBand(lowBand, threshold, ratio * 1.5, attackTime, releaseTime); // More compression on bass
        compressBand(midBand, threshold + 3.0, ratio * 0.8, attackTime, releaseTime); // Less compression on mids
        compressBand(highBand, threshold + 6.0, ratio * 0.6, attackTime, releaseTime * 1.5); // Even less on highs with longer release
        
        // Mix the bands back together
        for (int i = 0; i < n; i++) {
            audioData[i] = lowBand[i] + midBand[i] + highBand[i];
        }
    }

    /**
     * Apply harmonic exciter for added brilliance and presence (double version)
     */
    private static void applyHarmonicExciter(double[] audioData, float sampleRate) {
        double driveAmount = 0.3;
        double mixAmount = 0.2;
        
        // Adjust drive based on sample rate for consistent harmonic generation
        double sampleRateAdjust = 44100.0 / sampleRate;
        driveAmount *= sampleRateAdjust;

        double[] harmonics = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            // Generate harmonics using wave shaping
            double shaped = Math.tanh(audioData[i] * driveAmount);
            harmonics[i] = shaped - audioData[i]; // Extract harmonics

            // Mix harmonics back with dry signal
            audioData[i] = audioData[i] + (harmonics[i] * mixAmount);
        }
    }
    
    /**
     * Apply harmonic exciter for added brilliance and presence (float version)
     */
    private static float[] applyHarmonicExciter(float[] samples, AudioFormat format) {
        // Reduced drive and blend to prevent saturation
        float drive = 1.2f;
        float blend = 0.2f * highClarity;
        float[] enhanced = Arrays.copyOf(samples, samples.length);
        
        // Adjust drive based on sample rate for consistent harmonic generation
        float sampleRateAdjust = 44100f / format.getSampleRate();
        drive *= sampleRateAdjust;
        
        // Apply different processing based on mono/stereo
        int channels = format.getChannels();
        
        for (int i = 0; i < samples.length; i++) {
            // Generate harmonics through soft saturation
            float harmonics = (float) Math.tanh(samples[i] * drive);
            
            // High-pass the harmonics (simplified high-pass)
            if (i > 0 && (i % channels == i - 1 % channels)) { // Only compare samples in same channel
                harmonics = 0.9f * (harmonics - samples[i-channels]);
            }
            
            // Blend with original
            enhanced[i] = samples[i] + harmonics * blend;
            
            // Add soft limiting to prevent saturation
            if (enhanced[i] > 0.95f) enhanced[i] = 0.95f - (enhanced[i] - 0.95f) * 0.5f;
            if (enhanced[i] < -0.95f) enhanced[i] = -0.95f - (enhanced[i] + 0.95f) * 0.5f;
        }
        
        return enhanced;
    }

    private static void applyStereoWidening(double[] audioData) {
        double widthAmount = 0.3;

        for (int i = 0; i < audioData.length - 1; i += 2) {
            double left = audioData[i];
            double right = audioData[i + 1];

            // Mid-Side processing
            double mid = (left + right) * 0.5;
            double side = (left - right) * 0.5;

            // Enhance stereo width
            side *= (1.0 + widthAmount);

            // Convert back to Left-Right
            audioData[i] = mid + side;
            audioData[i + 1] = mid - side;
        }
    }

    /**
     * Apply spatial enhancement for immersive sound
     * Modified to reduce echo effect
     */
    private static float[] applySpatialEnhancement(float[] samples, AudioFormat format) {
        if (format.getChannels() != 2) {
            return samples; // Only process stereo
        }
        
        int sampleRate = (int) format.getSampleRate();
        // Reduced delay time to prevent echo (from 20ms to 8ms)
        int delayLength = (int) (sampleRate * 0.008); 
        float[] delayBuffer = new float[delayLength];
        float[] enhanced = Arrays.copyOf(samples, samples.length);
        float depth = spatialDepth * 0.5f; // Reduced depth to minimize echo
        
        // Apply subtle Haas effect for spatial enhancement
        for (int i = 0; i < samples.length; i += 2) {
            // Apply to right channel with reduced effect
            if (i + 1 < samples.length && i/2 >= delayLength) {
                // Reduced mix level from 0.2f to 0.1f
                enhanced[i + 1] += samples[i] * 0.1f * depth * delayBuffer[i/2 % delayLength];
            }
            
            // Update delay buffer with reduced feedback
            if (i < samples.length) {
                delayBuffer[i/2 % delayLength] = samples[i] * 0.15f; // Reduced from 0.3f
            }
        }
        
        return enhanced;
    }

    /**
     * Enhance stereo field for wider, more immersive sound
     * Modified to prevent phase issues
     */
    private static float[] enhanceStereoField(float[] samples, AudioFormat format) {
        if (format.getChannels() != 2) {
            return samples; // Only process stereo
        }
        
        float[] enhanced = new float[samples.length];
        // Reduced width factor to prevent phase issues
        float width = 1.0f + (stereoWidth - 1.0f) * 0.7f;
        
        // Apply mid-side processing for stereo enhancement
        for (int i = 0; i < samples.length; i += 2) {
            float left = samples[i];
            float right = samples[i + 1];
            
            // Convert to mid-side
            float mid = (left + right) * 0.5f;
            float side = (left - right) * 0.5f;
            
            // Enhance side signal with more conservative width
            side *= width;
            
            // Convert back to left-right
            enhanced[i] = mid + side;
            enhanced[i + 1] = mid - side;
            
            // Add correlation check to prevent phase issues
            // If left and right are too out of phase, reduce the effect
            float correlation = left * right;
            if (correlation < 0) {
                // Signals are out of phase, blend back toward original
                float blendFactor = 0.7f;
                enhanced[i] = enhanced[i] * blendFactor + samples[i] * (1-blendFactor);
                enhanced[i+1] = enhanced[i+1] * blendFactor + samples[i+1] * (1-blendFactor);
            }
        }
        
        return enhanced;
    }

    private static void applyBiquadFilter(double[] audioData, double b0, double b1, double b2, double a0, double a1, double a2) {
        int n = audioData.length;
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    private static void splitBands(double[] audioData, double[] lowBand, double[] midBand, double[] highBand, float sampleRate, double lowCrossover, double highCrossover) {
        int n = audioData.length;
        double[] lowPass = new double[n];
        double[] highPass = new double[n];

        // Low-pass filter for low band
        applyLowPassFilter(audioData, lowPass, sampleRate, lowCrossover);

        // High-pass filter for high band
        applyHighPassFilter(audioData, highPass, sampleRate, highCrossover);

        // Mid band is the difference between original and low+high bands
        for (int i = 0; i < n; i++) {
            lowBand[i] = lowPass[i];
            highBand[i] = highPass[i];
            midBand[i] = audioData[i] - lowPass[i] - highPass[i];
        }
    }

    private static void applyLowPassFilter(double[] audioData, double[] lowPass, float sampleRate, double cutoffFrequency) {
        int n = audioData.length;
        double w0 = 2.0 * Math.PI * cutoffFrequency / sampleRate;
        double alpha = Math.sin(w0) / 2.0;
        double b0 = (1.0 - Math.cos(w0)) / 2.0;
        double b1 = 1.0 - Math.cos(w0);
        double b2 = (1.0 - Math.cos(w0)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha;

        // Create a temporary array for processing
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        if (n > 1) {
            filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];
        }

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        // Copy the filtered data to the lowPass output array
        System.arraycopy(filteredData, 0, lowPass, 0, n);
    }

    private static void applyHighPassFilter(double[] audioData, double[] highPass, float sampleRate, double cutoffFrequency) {
        int n = audioData.length;
        double w0 = 2.0 * Math.PI * cutoffFrequency / sampleRate;
        double alpha = Math.sin(w0) / 2.0;
        double b0 = (1.0 + Math.cos(w0)) / 2.0;
        double b1 = -(1.0 + Math.cos(w0));
        double b2 = (1.0 + Math.cos(w0)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha;

        // Create a temporary array for processing
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        if (n > 1) {
            filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];
        }

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        // Copy the filtered data to the highPass output array
        System.arraycopy(filteredData, 0, highPass, 0, n);
    }

    private static void compressBand(double[] band, double threshold, double ratio, double attackTime, double releaseTime) {
        double attack = Math.exp(-1.0 / (attackTime * 44100.0));
        double release = Math.exp(-1.0 / (releaseTime * 44100.0));
        double thresholdLinear = Math.pow(10.0, threshold / 20.0);
        double envGain = 1.0; // Envelope follower for smoother gain changes

        for (int i = 0; i < band.length; i++) {
            double absample = Math.abs(band[i]);
            double targetGain;
            
            if (absample > thresholdLinear) {
                double overThreshold = absample - thresholdLinear;
                double compressed = thresholdLinear + overThreshold / ratio;
                targetGain = compressed / absample;
            } else {
                targetGain = 1.0;
            }

            // Apply attack/release envelope
            if (targetGain < envGain) {
                // Gain reduction (attack)
                envGain = attack * envGain + (1.0 - attack) * targetGain;
            } else {
                // Gain recovery (release)
                envGain = release * envGain + (1.0 - release) * targetGain;
            }
            
            band[i] *= envGain;
        }
    }

    private static void writeEnhancedAudio(byte[] enhancedBytes, AudioFormat format, File outputFile) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(enhancedBytes); AudioInputStream enhancedAudioInputStream = new AudioInputStream(bais, format, enhancedBytes.length / format.getFrameSize())) {
            AudioSystem.write(enhancedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }

        System.out.println("Audio enhancement complete. Output saved to: " + outputFile.getAbsolutePath());
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
    
    /**
     * Apply multi-band equalization for professional tonal balance
     * Modified to be more gentle
     */
    private static float[] applyMultiBandEQ(float[] samples, AudioFormat format) {
        int channels = format.getChannels();
        int samplesPerChannel = samples.length / channels;
        
        // Create frequency domain processor for each band
        for (int ch = 0; ch < channels; ch++) {
            // Extract single channel for processing
            float[] channelSamples = new float[samplesPerChannel];
            for (int i = 0; i < samplesPerChannel; i++) {
                channelSamples[i] = samples[i * channels + ch];
            }
            
            // Convert float array to double array for filter methods
            double[] doubleChannelSamples = new double[channelSamples.length];
            for (int i = 0; i < channelSamples.length; i++) {
                doubleChannelSamples[i] = channelSamples[i];
            }
            
            // Apply low-shelf filter for bass (below 150Hz) - reduced gain
            applyLowShelfFilter(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[0], 
                                1.0 + (bassBoost - 1.0) * 0.7);
            
            // Apply parametric EQ for low-mids (250-500Hz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[2], 0.9, 1.2);
            
            // Apply parametric EQ for mids (500-2kHz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[3],
                           1.0 + (midPresence - 1.0) * 0.7, 1.5);
            
            // Apply parametric EQ for high-mids (2k-5kHz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[4],
                           1.0 + (midPresence - 1.0) * 0.6, 1.0);
            
            // Apply high-shelf filter for highs (above 6kHz) - reduced gain
            applyHighShelfFilter(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[5], 
                                1.0 + (highClarity - 1.0) * 0.7);
            
            // Convert back to float array
            for (int i = 0; i < channelSamples.length; i++) {
                channelSamples[i] = (float) doubleChannelSamples[i];
            }
            
            // Copy processed channel back to main samples array
            for (int i = 0; i < samplesPerChannel; i++) {
                samples[i * channels + ch] = channelSamples[i];
            }
        }
        
        return samples;
    }
    
    /**
     * Apply dynamic range compression for professional loudness
     * Modified to be more gentle and prevent saturation
     */
    private static float[] applyDynamicCompression(float[] samples, AudioFormat format) {
        int channels = format.getChannels();
        float threshold = 0.4f;  // Increased threshold for less compression
        float ratio = 1.0f / dynamicRange;  // Compression ratio
        float attackTime = 0.01f;  // Increased from 0.005f for smoother attack
        float releaseTime = 0.15f;  // Increased from 0.050f for smoother release
        // Reduced makeup gain to prevent saturation
        float makeupGain = 1.0f + (1.0f - dynamicRange) * 0.5f;
        
        // Calculate attack and release coefficients
        float attackCoef = (float) Math.exp(-1.0 / (format.getSampleRate() * attackTime));
        float releaseCoef = (float) Math.exp(-1.0 / (format.getSampleRate() * releaseTime));
        
        // Process each channel
        for (int ch = 0; ch < channels; ch++) {
            float envelope = 0;
            
            // Apply compression
            for (int i = ch; i < samples.length; i += channels) {
                // Calculate envelope (peak detection)
                float inputAbs = Math.abs(samples[i]);
                if (inputAbs > envelope) {
                    envelope = attackCoef * envelope + (1 - attackCoef) * inputAbs;
                } else {
                    envelope = releaseCoef * envelope + (1 - releaseCoef) * inputAbs;
                }
                
                // Apply compression if envelope exceeds threshold
                float gain = 1.0f;
                if (envelope > threshold) {
                    float compressionFactor = threshold + (envelope - threshold) / ratio;
                    gain = compressionFactor / envelope;
                }
                
                // Apply gain with makeup
                samples[i] = samples[i] * gain * makeupGain;
                
                // Add hard limiting to prevent clipping
                if (samples[i] > 0.95f) samples[i] = 0.95f;
                if (samples[i] < -0.95f) samples[i] = -0.95f;
            }
        }
        
        return samples;
    }
    
    /**
     * Apply noise reduction for cleaner sound
     */
    private static float[] applyNoiseReduction(float[] samples, AudioFormat format) {
        // Simple noise gate with spectral subtraction principles
        float threshold = 0.01f * (1 - noiseReduction);
        float[] processed = Arrays.copyOf(samples, samples.length);
        int channels = format.getChannels();
        
        // Estimate noise floor (simplified)
        float noiseFloor = 0;
        int sampleCount = Math.min(10000, samples.length / channels);
        
        for (int i = 0; i < sampleCount * channels; i += channels) {
            for (int ch = 0; ch < channels; ch++) {
                noiseFloor += Math.abs(samples[i + ch]) / sampleCount / channels;
            }
        }
        
        // Apply noise reduction
        for (int i = 0; i < samples.length; i++) {
            float absValue = Math.abs(samples[i]);
            if (absValue < noiseFloor + threshold) {
                float reductionFactor = Math.max(0, (absValue - noiseFloor) / threshold);
                processed[i] *= reductionFactor * reductionFactor; // Quadratic for smoother transition
            }
        }
        
        return processed;
    }
    
    /**
     * Apply final limiter to prevent any clipping
     * New method to ensure no saturation occurs
     */
    private static float[] applyFinalLimiter(float[] samples) {
        float ceiling = 0.95f;  // Maximum allowed amplitude
        // Remove unused variable
        // float lookAhead = 0.005f;  // 5ms look-ahead for smoother limiting
        
        // Find the maximum peak
        float maxPeak = 0;
        for (float sample : samples) {
            float abs = Math.abs(sample);
            if (abs > maxPeak) {
                maxPeak = abs;
            }
        }
        
        // If we're already below ceiling, no need to limit
        if (maxPeak <= ceiling) {
            return samples;
        }
        
        // Calculate gain reduction needed
        float gainReduction = ceiling / maxPeak;
        
        // Apply smooth limiting
        float[] limited = new float[samples.length];
        for (int i = 0; i < samples.length; i++) {
            limited[i] = samples[i] * gainReduction;
        }
        
        return limited;
    }
}
