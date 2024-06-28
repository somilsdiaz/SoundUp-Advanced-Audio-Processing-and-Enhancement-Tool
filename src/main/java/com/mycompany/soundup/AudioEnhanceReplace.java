/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.soundup;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.GainProcessor;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AudioEnhanceReplace {

    public static void MejorarYreplace(String ruta) {
        String directoryPath = ruta; // Reemplaza con la ruta de tu directorio
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            for (Path audioFile : audioFiles) {
                File wavFile = convertToWav(audioFile.toFile());
                normalizeAudioVolume(wavFile, audioFile.toFile());
                deleteTemporaryFile(wavFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isAudioFile(File file) {
        String[] audioExtensions = {".wav", ".mp3", ".flac", ".ogg", ".m4a"};
        String fileName = file.getName().toLowerCase();
        for (String ext : audioExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private static File convertToWav(File inputFile) {
        File outputWavFile = new File(inputFile.getParent(), "temp_" + inputFile.getName() + ".wav");
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(16000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(inputFile, outputWavFile, attrs);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        return outputWavFile;
    }

    private static void normalizeAudioVolume(File audioFile, File originalFile) {
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

                // Write the normalized audio to a temporary file
                File normalizedTempFile = new File(audioFile.getParent(), "normalized_temp.wav");
                WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), normalizedTempFile.getAbsolutePath());
                normalizationDispatcher.addAudioProcessor(writer);

                normalizationDispatcher.run();

                // Replace the original file with the normalized file
                Files.move(normalizedTempFile.toPath(), originalFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Normalized and replaced: " + originalFile.getName());
            } else {
                // If adjustmentFactor <= 0, no adjustment needed
                System.out.println("Already normalized: " + originalFile.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteTemporaryFile(File file) {
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Temporary file deleted: " + file.getName());
            } else {
                System.out.println("Failed to delete temporary file: " + file.getName());
            }
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
