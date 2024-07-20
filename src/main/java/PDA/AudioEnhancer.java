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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioEnhancer {

    public static void enhanceAudio(File inputFile, File outputFile) throws UnsupportedAudioFileException, IOException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);
        AudioFormat format = audioInputStream.getFormat();

        byte[] audioBytes = audioInputStream.readAllBytes();
        audioInputStream.close();

        double[] audioData = byteArrayToDoubleArray(audioBytes, format);
        double gain = 1.0f; // Ganancia para aumentar brillo
        applyHighShelfFilter(audioData, format.getSampleRate(), 600.0, gain);

        // Aplicar el filtro de pasa banda para aumentar medios-bajos
        applyBandPassFilter(audioData, format.getSampleRate(), 250.0, 500.0, 6.0);

        // Normalize volume
        normalizeVolume(audioData);

        byte[] enhancedBytes = doubleArrayToByteArray(audioData, format);
        ByteArrayInputStream bais = new ByteArrayInputStream(enhancedBytes);
        AudioInputStream enhancedAudioInputStream = new AudioInputStream(bais, format, audioData.length);

        AudioSystem.write(enhancedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        enhancedAudioInputStream.close();

        System.out.println("Audio enhancement complete. Output saved to: " + outputFile.getAbsolutePath());
    }

    private static double[] byteArrayToDoubleArray(byte[] audioBytes, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        int numSamples = audioBytes.length / bytesPerSample;
        double[] audioData = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            int sampleStart = i * bytesPerSample;
            int sample = 0;

            for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
                int byteValue = audioBytes[sampleStart + byteIndex];
                if (byteIndex < bytesPerSample - 1 || bytesPerSample == 1) {
                    byteValue &= 0xFF;
                }
                sample += byteValue << (byteIndex * 8);
            }

            audioData[i] = sample / Math.pow(2, format.getSampleSizeInBits() - 1);
        }

        return audioData;
    }

    private static byte[] doubleArrayToByteArray(double[] audioData, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        byte[] audioBytes = new byte[audioData.length * bytesPerSample];

        for (int i = 0; i < audioData.length; i++) {
            int sample = (int) (audioData[i] * Math.pow(2, format.getSampleSizeInBits() - 1));

            for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
                audioBytes[i * bytesPerSample + byteIndex] = (byte) ((sample >> (byteIndex * 8)) & 0xFF);
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
            double targetRMS = 0.1;
            double currentRMS = rmsProcessor.getRMS();
            double currentVolume = 20 * Math.log10(currentRMS);
            double targetVolume = 20 * Math.log10(targetRMS);
            double adjustmentFactor = targetVolume - currentVolume;

            AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            normalizationDispatcher.addAudioProcessor(new GainProcessor(1.0f));
            double sampleRate = 44100.0;
            double maxFlangerLength = 0.003;
            double wet = 0.5;
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
        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(inputFile1);
        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(inputFile2);

        AudioFormat format1 = audioInputStream1.getFormat();
        AudioFormat format2 = audioInputStream2.getFormat();

        if (!format1.matches(format2)) {
            throw new UnsupportedAudioFileException("Los formatos de los archivos de audio no coinciden.");
        }

        byte[] audioBytes1 = audioInputStream1.readAllBytes();
        byte[] audioBytes2 = audioInputStream2.readAllBytes();

        audioInputStream1.close();
        audioInputStream2.close();

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

        ByteArrayInputStream bais = new ByteArrayInputStream(mixedBytes);
        AudioInputStream mixedAudioInputStream = new AudioInputStream(bais, format1, mixedBytes.length / format1.getFrameSize());

        AudioSystem.write(mixedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        mixedAudioInputStream.close();

        System.out.println("Audio mix complete. Output saved to: " + outputFile.getAbsolutePath());
    }

    public static void main(String[] args) throws IOException {
        Path tempFilesDirectory = Paths.get("tempfiles");
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
