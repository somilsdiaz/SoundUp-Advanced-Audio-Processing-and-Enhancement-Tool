package com.mycompany.soundup;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.GainProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AudioNormalizer {

    public static void main(String[] args) {
        String directoryPath = "C:/Users/Somils/Desktop/Muestra"; // Reemplaza con la ruta de tu directorio
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".wav")) // Suponiendo archivos .wav
                    .collect(Collectors.toList());

            for (Path audioFile : audioFiles) {
                normalizeAudioVolume(audioFile.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private static void normalizeAudioVolume(File audioFile) {
    try {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);

        // Processor to calculate RMS
        RMSProcessor rmsProcessor = new RMSProcessor();
        dispatcher.addAudioProcessor(rmsProcessor);

        dispatcher.run();

        // Calculate target RMS value
        double targetRMS = 0.1; // Target RMS value
        double currentRMS = rmsProcessor.getRMS();

        // Calculate current volume in dB
        double currentVolume = 20 * Math.log10(currentRMS);
        // Calculate target volume in dB
        double targetVolume = 20 * Math.log10(targetRMS);
        // Calculate adjustment factor in dB
        double adjustmentFactor = targetVolume - currentVolume;

        // Apply normalization
        if (adjustmentFactor > 0) {
            // If adjustmentFactor > 0, we need to increase volume
            AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            normalizationDispatcher.addAudioProcessor(new GainProcessor(adjustmentFactor));

            String outputFilePath = audioFile.getParent() + "/normalized_" + audioFile.getName();
            WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), outputFilePath);
            normalizationDispatcher.addAudioProcessor(writer);

            normalizationDispatcher.run();
            System.out.println("Normalized: " + audioFile.getName());
        } else {
            // If adjustmentFactor <= 0, no adjustment needed
            System.out.println("Already normalized: " + audioFile.getName());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    // RMSProcessor to calculate the RMS value of the audio
    private static class RMSProcessor implements AudioProcessor {
        private double rms = 0;
        private long sampleCount = 0;

        @Override
        public boolean process(AudioEvent audioEvent) {
            float[] buffer = audioEvent.getFloatBuffer();
            double sum = 0;
            for (float sample : buffer) {
                sum += sample * sample;
            }
            rms += sum;
            sampleCount += buffer.length;
            return true;
        }

        @Override
        public void processingFinished() {
            rms = Math.sqrt(rms / sampleCount);
        }

        public double getRMS() {
            return rms;
        }
    }
}