/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    public static void enhanceAudio(File inputFile, File outputFile) throws UnsupportedAudioFileException, IOException {
        AudioFormat format;
        double[] audioData;

        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile)) {
            format = audioInputStream.getFormat();
            byte[] audioBytes = audioInputStream.readAllBytes();
            audioData = byteArrayToDoubleArray(audioBytes, format);
        }

        double gain = 1.0f; // Ganancia para aumentar brillo

        // Comprobar memoria disponible antes de aplicar el filtro High Shelf
        if (isMemorySufficient(audioData.length)) {
            applyHighShelfFilter(audioData, format.getSampleRate(), 600.0, gain);
        } else {
            System.out.println("Memoria insuficiente para aplicar el filtro High Shelf. Se omite el proceso.");
        }

        // Comprobar memoria disponible antes de aplicar el filtro Band Pass
        if (isMemorySufficient(audioData.length)) {
            applyBandPassFilter(audioData, format.getSampleRate(), 250.0, 500.0, 6.0);
            applyLowShelfFilter(audioData, 44100, 200, 6);

        } else {
            System.out.println("Memoria insuficiente para aplicar el filtro Band Pass. Se omite el proceso.");
        }

        // Normalizar volumen
        normalizeVolume(audioData);

        byte[] enhancedBytes = doubleArrayToByteArray(audioData, format);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(enhancedBytes); AudioInputStream enhancedAudioInputStream = new AudioInputStream(bais, format, audioData.length)) {
            AudioSystem.write(enhancedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        }

        System.out.println("Audio enhancement complete. Output saved to: " + outputFile.getAbsolutePath());
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
            double wet = 0.0;
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

        String mascaraAudioPDApatch = tempFilesDirectory.toString() + "/pdaMask_" + inputFileNameWithoutExtension + ".wav"; //ESTE HAY QUE ELIMINARLO8
        File mascaraAudioPDA = new File(mascaraAudioPDApatch);

        try {
            enhanceAudio(AudioOriginalWav, mascaraAudioPDA);
            String normalized_mask = normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav);
            String normalized_temp = AudioEnhanceFile.convertToWavString(normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav));
            File AudioMidlePDA = new File(normalized_temp);

            String AudioPDApatch = tempFilesDirectory.toString() + "/PDA_" + inputFileNameWithoutExtension + ".wav";
            File AudioPDA = new File(AudioPDApatch);
            mixAudioFiles(AudioMidlePDA, AudioOriginalWav, AudioPDA);
            RMS.AudioEnhanceFile.eliminarArchivo(mascaraAudioPDApatch);
            RMS.AudioEnhanceFile.eliminarArchivo(normalized_temp);
            RMS.AudioEnhanceFile.eliminarArchivo(normalized_mask);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

}
