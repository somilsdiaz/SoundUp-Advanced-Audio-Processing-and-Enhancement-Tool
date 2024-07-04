/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.soundup;

/**
 *
 * @author Somils
 */
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioNormalizer {

    private static final Logger LOGGER = Logger.getLogger(AudioNormalizer.class.getName());

    private static class GainProcessor implements AudioProcessor {

        private final double gainDB;

        public GainProcessor(double gainDB) {
            this.gainDB = gainDB;
        }

        @Override
        public boolean process(AudioEvent audioEvent) {
            float[] buffer = audioEvent.getFloatBuffer();
            double gainFactor = Math.pow(10.0, gainDB / 20.0); // Convertir dB a factor de ganancia
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] *= gainFactor;
            }
            return true;
        }

        @Override
        public void processingFinished() {
        }
    }

    private static double calculateRMS(String audioFilePath) throws UnsupportedAudioFileException, IOException {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFilePath, 44100, 1024, 0);
        RMSProcessor rmsProcessor = new RMSProcessor();
        dispatcher.addAudioProcessor(rmsProcessor);
        dispatcher.run();
        return rmsProcessor.getRMS();
    }

    private static void applyGain(String inputFilePath, String outputFilePath, double gainDB) throws UnsupportedAudioFileException, IOException {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(inputFilePath, 44100, 1024, 0);
        dispatcher.addAudioProcessor(new GainProcessor(gainDB));
        WaveformWriter writer = new WaveformWriter(dispatcher.getFormat(), outputFilePath);
        dispatcher.addAudioProcessor(writer);
        dispatcher.run();
    }

    private static String normalizeAudioVolume(File audioFile, File originalFile) {
        try {
            String inputFilePath = audioFile.getAbsolutePath();
            String outputFilePath = audioFile.getParent() + "/normalized_" + audioFile.getName();

            double currentRMS = calculateRMS(inputFilePath);
            double targetRMS = 0.2; // Target RMS value

            double currentVolume = 20 * Math.log10(currentRMS);
            double targetVolume = 20 * Math.log10(targetRMS);
            double adjustmentFactor = targetVolume - currentVolume;

            LOGGER.log(Level.INFO, "Current RMS: {0}", currentRMS);
            LOGGER.log(Level.INFO, "Target RMS: {0}", targetRMS);
            LOGGER.log(Level.INFO, "Current Volume (dB): {0}", currentVolume);
            LOGGER.log(Level.INFO, "Target Volume (dB): {0}", targetVolume);
            LOGGER.log(Level.INFO, "Adjustment Factor (dB): {0}", adjustmentFactor);

            if (adjustmentFactor > 0) {
                LOGGER.log(Level.INFO, "Applying gain adjustment: {0} dB", adjustmentFactor);
                applyGain(inputFilePath, outputFilePath, adjustmentFactor);

                double newRMS = calculateRMS(outputFilePath);
                double newVolume = 20 * Math.log10(newRMS);
                LOGGER.log(Level.INFO, "New RMS: {0}", newRMS);
                LOGGER.log(Level.INFO, "New Volume (dB): {0}", newVolume);

                return outputFilePath; // Retornar la ruta del archivo normalizado
            } else {
                LOGGER.log(Level.INFO, "No adjustment needed, volume already adequate: {0}", originalFile.getName());
                return originalFile.getAbsolutePath(); // Retornar la ruta del archivo original si no se necesita ajuste o si se requeriría reducir el volumen
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error normalizing audio volume", e);
        }
        return null; // Retornar null en caso de error
    }

    public static void main(String[] args) {
        // Prueba del método de normalización
        File audioFile = new File("C:/Users/Somils/Desktop/Muestra/Amor y Control.mp3");
        File originalFile = new File("C:/Users/Somils/Desktop/Muestra/Amor y Control.mp3");
        String normalizedFilePath = normalizeAudioVolume(audioFile, originalFile);

        if (normalizedFilePath != null) {
            System.out.println("Archivo normalizado: " + normalizedFilePath);
        } else {
            System.out.println("Error al normalizar el archivo.");
        }
    }
    
    
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
