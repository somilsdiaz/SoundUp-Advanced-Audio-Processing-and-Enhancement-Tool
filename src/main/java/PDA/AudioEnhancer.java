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

public class AudioEnhancer {
    // Add professional audio processing constants
    private static final double WARM_BASS_FREQ = 120.0;
    private static final double PRESENCE_FREQ = 5000.0;
    private static final double AIR_FREQ = 12000.0;
    private static final double CLARITY_FREQ = 2500.0;

    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    // Enhanced audio processing method
    public static void enhanceAudio(File inputFile, File outputFile) throws UnsupportedAudioFileException, IOException {
        AudioFormat format;
        double[] audioData;

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile)) {
            format = audioInputStream.getFormat();
            byte[] audioBytes = audioInputStream.readAllBytes();
            audioData = byteArrayToDoubleArray(audioBytes, format);
        }

        // Professional multi-stage processing pipeline
        if (isMemorySufficient(audioData.length)) {
            /*
            // Stage 1: Frequency Balance
            applyLowShelfFilter(audioData, format.getSampleRate(), WARM_BASS_FREQ, 3.0); // Warm bass
            applyHighShelfFilter(audioData, format.getSampleRate(), AIR_FREQ, 2.0); // Air and brightness

            // Stage 2: Presence and Clarity
            applyParametricEQ(audioData, format.getSampleRate(), PRESENCE_FREQ, 1.5, 0.7); // Presence
            applyParametricEQ(audioData, format.getSampleRate(), CLARITY_FREQ, 2.0, 0.5); // Clarity

            // Stage 3: Dynamic Processing
            applyMultibandCompression(audioData, format.getSampleRate());
            applyHarmonicExciter(audioData, format.getSampleRate());

            // Stage 4: Stereo Enhancement
            if (format.getChannels() == 2) {
                applyStereoWidening(audioData);
            }

            // Final Stage: Mastering
            applyLimiter(audioData, -0.3); // Prevent clipping
            normalizeVolume(audioData);*/
        } else {
            System.out.println("No pasa No pasa No pasa");
        }

        byte[] enhancedBytes = doubleArrayToByteArray(audioData, format);
        writeEnhancedAudio(enhancedBytes, format, outputFile);
    }

    private static boolean isMemorySufficient(int dataLength) {
        // Calcular memoria necesaria para procesar el audio (aproximación)
        long memoryNeeded = dataLength * Double.BYTES * 1; // Doble del tamaño para procesamiento intermedio
        long freeMemory = Runtime.getRuntime().freeMemory();
        return freeMemory > memoryNeeded;
    }

    public static double[] byteArrayToDoubleArray(byte[] audioBytes, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int numSamples = audioBytes.length / bytesPerSample;
        double[] audioData = new double[numSamples];
        ByteBuffer byteBuffer = ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < numSamples; i++) {
            int sample = 0;
            if (bytesPerSample == 1) {
                sample = byteBuffer.get() & 0xFF;
            } else if (bytesPerSample == 2) {
                sample = byteBuffer.getShort();
            } else if (bytesPerSample == 3) {
                sample = byteBuffer.get() & 0xFF;
                sample |= (byteBuffer.get() & 0xFF) << 8;
                sample |= (byteBuffer.get() << 16);
            } else if (bytesPerSample == 4) {
                sample = byteBuffer.getInt();
            }

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

            if (bytesPerSample == 1) {
                byteBuffer.put((byte) (sample & 0xFF));
            } else if (bytesPerSample == 2) {
                byteBuffer.putShort((short) sample);
            } else if (bytesPerSample == 3) {
                byteBuffer.put((byte) (sample & 0xFF));
                byteBuffer.put((byte) ((sample >> 8) & 0xFF));
                byteBuffer.put((byte) ((sample >> 16) & 0xFF));
            } else if (bytesPerSample == 4) {
                byteBuffer.putInt(sample);
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

    private static void normalizeVolume(double[] audioData) {
        double max = 0.0;
        for (double sample : audioData) {
            if (Math.abs(sample) > max) {
                max = Math.abs(sample);
            }
        }

        if (max > 0) {
            double targetMax = 1.0;
            for (int i = 0; i < audioData.length; i++) {
                audioData[i] = (audioData[i] / max) * targetMax;
            }
        }
    }

    public static String normalizeAudioVolume(File audioFile, File originalFile) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 2);
            AudioEnhanceFile.RMSProcessor rmsProcessor = new AudioEnhanceFile.RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);

            dispatcher.run();
            AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.0f));
            double sampleRate = 44100.0;
            double maxFlangerLength = 0.003;
            double wet = 0.25;
            double lfoFrequency = 0.25;
            FlangerEffect flangerEffect = new FlangerEffect(maxFlangerLength, wet, sampleRate, lfoFrequency);
            normalizationDispatcher.addAudioProcessor(flangerEffect);
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.5f));
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

            for (int i = 0; i < maxLength; i++) {
                int sample1 = i < audioBytes1.length ? audioBytes1[i] : 0;
                int sample2 = i < audioBytes2.length ? audioBytes2[i] : 0;

                int mixedSample = sample1 + sample2;
                mixedSample = Math.min(mixedSample, Byte.MAX_VALUE);
                mixedSample = Math.max(mixedSample, Byte.MIN_VALUE);

                mixedBytes[i] = (byte) mixedSample;
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
        Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");
        if (!Files.exists(tempFilesDirectory)) {
            Files.createDirectory(tempFilesDirectory);
        }

        String AudioOriginal = AudioEnhanceFile.convertToWavString("C:/Users/Somils/Music/SALSAS/excusa.mp3");
        File AudioOriginalWav = new File(AudioOriginal);
        String inputFileName = AudioOriginalWav.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));

        String mascaraAudioPDApatch = tempFilesDirectory.toString() + "/pdaMask_" + inputFileNameWithoutExtension + ".wav";
        File mascaraAudioPDA = new File(mascaraAudioPDApatch);

        try {
            // Enhance the audio
            enhanceAudio(AudioOriginalWav, mascaraAudioPDA);

            // Normalize the enhanced audio
            String normalizedMaskPath = normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav);
            File normalizedMaskFile = new File(normalizedMaskPath);

            // Convert the normalized audio to WAV format
            String normalizedTempPath = AudioEnhanceFile.convertToWavString(normalizedMaskPath);
            File AudioMidlePDA = new File(normalizedTempPath);

            // Mix the normalized audio with the original audio
            String AudioPDApatch = tempFilesDirectory.toString() + "/PDA_" + inputFileNameWithoutExtension + ".wav";
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
        double[] lowBand = new double[audioData.length];
        double[] midBand = new double[audioData.length];
        double[] highBand = new double[audioData.length];

        // Crossover frequencies
        double lowCrossover = 200.0;
        double highCrossover = 2000.0;

        // Split into frequency bands
        splitBands(audioData, lowBand, midBand, highBand, sampleRate, lowCrossover, highCrossover);

        // Apply compression to each band
        compressBand(lowBand, -24.0, 4.0, 0.1, 0.2); // Bass
        compressBand(midBand, -18.0, 3.0, 0.1, 0.15); // Mids
        compressBand(highBand, -12.0, 2.0, 0.05, 0.1); // Highs

        // Sum the bands back together
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = lowBand[i] + midBand[i] + highBand[i];
        }
    }

    private static void applyHarmonicExciter(double[] audioData, float sampleRate) {
        double driveAmount = 0.3;
        double mixAmount = 0.2;

        double[] harmonics = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            // Generate harmonics using wave shaping
            double shaped = Math.tanh(audioData[i] * driveAmount);
            harmonics[i] = shaped - audioData[i]; // Extract harmonics

            // Mix harmonics back with dry signal
            audioData[i] = audioData[i] + (harmonics[i] * mixAmount);
        }
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

    private static void applyLimiter(double[] audioData, double threshold) {
        double releaseTime = 0.1; // seconds
        double ceiling = Math.pow(10.0, threshold / 20.0);
        double release = Math.exp(-1.0 / (releaseTime * 44100.0));

        double gain = 1.0;
        for (int i = 0; i < audioData.length; i++) {
            double absample = Math.abs(audioData[i]);
            if (absample * gain > ceiling) {
                gain = ceiling / absample;
            } else {
                gain = gain * release + (1.0 - release);
            }
            audioData[i] *= gain;
        }
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

        applyBiquadFilter(audioData, b0, b1, b2, a0, a1, a2);
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

        applyBiquadFilter(audioData, b0, b1, b2, a0, a1, a2);
    }

    private static void compressBand(double[] band, double threshold, double ratio, double attackTime, double releaseTime) {
        double attack = Math.exp(-1.0 / (attackTime * 44100.0));
        double release = Math.exp(-1.0 / (releaseTime * 44100.0));
        double gain = 1.0;
        double thresholdLinear = Math.pow(10.0, threshold / 20.0);

        for (int i = 0; i < band.length; i++) {
            double absample = Math.abs(band[i]);
            if (absample > thresholdLinear) {
                double overThreshold = absample - thresholdLinear;
                double compressed = thresholdLinear + overThreshold / ratio;
                gain = compressed / absample;
            } else {
                gain = 1.0;
            }

            gain = gain * attack + (1.0 - attack);
            band[i] *= gain;
        }
    }

    private static void writeEnhancedAudio(byte[] enhancedBytes, AudioFormat format, File outputFile) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(enhancedBytes); AudioInputStream enhancedAudioInputStream = new AudioInputStream(bais, format, enhancedBytes.length / format.getFrameSize())) {
            AudioSystem.write(enhancedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }

        System.out.println("Audio enhancement complete. Output saved to: " + outputFile.getAbsolutePath());
    }
}
