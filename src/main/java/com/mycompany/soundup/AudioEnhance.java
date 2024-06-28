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
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AudioEnhance {

    public static String Mejorar(String ruta, int reemplazar) {
        String directoryPath = ruta; // Reemplaza con la ruta de tu directorio
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            for (Path audioFile : audioFiles) {
                File wavFile = convertToWav(audioFile.toFile());
                String normalizedFilePath = normalizeAudioVolume(wavFile, audioFile.toFile());
                deleteTemporaryFile(wavFile);

                if (reemplazar == 1) {
                    // Reemplazar archivo original con el archivo normalizado
                    Files.copy(Paths.get(normalizedFilePath), audioFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(Paths.get(normalizedFilePath)); // Eliminar el archivo temporal normalizado
                }
                // Retornar la ruta del archivo normalizado u original
                return (reemplazar == 1) ? audioFile.toString() : normalizedFilePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Retornar null en caso de error o si no se encuentra ningún archivo de audio
    }

    public static boolean necesitaNormalizacion(String ruta) {
        try {
            File audioFile = new File(ruta);
            if (!audioFile.exists() || !isAudioFile(audioFile)) {
                throw new IllegalArgumentException("El archivo no existe o no es un archivo de audio válido.");
            }

            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
            RMSProcessor rmsProcessor = new RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);

            dispatcher.run();

            double targetRMS = 0.1; // Target RMS value
            double currentRMS = rmsProcessor.getRMS();

            return currentRMS < targetRMS; // Retornar true si necesita normalización
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Retornar false en caso de error
    }

    public static boolean eliminarArchivo(String ruta) {
        Path path = Paths.get(ruta);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Archivo eliminado: " + ruta);
                return true;
            } else {
                System.out.println("El archivo no existe: " + ruta);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

    private static String normalizeAudioVolume(File audioFile, File originalFile) {
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
                File normalizedTempFile = new File(audioFile.getParent() + "/normalized_" + audioFile.getName());
                WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), normalizedTempFile.getAbsolutePath());
                normalizationDispatcher.addAudioProcessor(writer);

                normalizationDispatcher.run();

                return normalizedTempFile.getAbsolutePath(); // Retornar la ruta del archivo normalizado
            } else {
                // If adjustmentFactor <= 0, no adjustment needed
                System.out.println("Already normalized: " + originalFile.getName());
                return originalFile.getAbsolutePath(); // Retornar la ruta del archivo original si ya está normalizado
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retornar null en caso de error
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
