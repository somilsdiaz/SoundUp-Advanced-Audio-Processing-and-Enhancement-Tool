package com.mycompany.soundup;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.GainProcessor;
import Directorios.DirectoryTree;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AudioEnhanceDir {

    public static DirectoryTree tree;
    public int cantidad = 0;
    private static List<File> convertedFiles = new ArrayList<>();

    public static void EliminarDuplicadosCovertidos() {
        // Eliminar los archivos convertidos
        for (File convertedFile : convertedFiles) {
            if (!convertedFile.delete()) {
                System.err.println("Error deleting file: " + convertedFile.getAbsolutePath());
            }
        }
    }

    public void MejorarDir(String directoryPath) {
        tree = new DirectoryTree(directoryPath);
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String ext = getFileExtension(path.toString()).toLowerCase();
                        return ext.equals("wav") || ext.equals("mp3") || ext.equals("flac") || ext.equals("ogg") || ext.equals("m4a");
                    })
                    .collect(Collectors.toList());

            // Usa un ThreadPoolExecutor para procesar archivos en paralelo
            int numCores = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores);

            for (Path audioFile : audioFiles) {
                executor.submit(() -> {
                    try {
                        File wavFile = convertToWav(audioFile.toFile());
                        if (wavFile != null) {
                            normalizeAudioVolume(wavFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private File convertToWav(File audioFile) {
        String ext = getFileExtension(audioFile.getName()).toLowerCase();
        if (ext.equals("wav")) {
            return audioFile;
        }
        String wavFileName = audioFile.getParent() + "/" + removeFileExtension(audioFile.getName()) + ".wav";
        File target = new File(wavFileName);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();

        try {
            encoder.encode(audioFile, target, attrs);
            if (target.exists()) {
                convertedFiles.add(target);
                return target;
            } else {
                System.err.println("Error converting " + audioFile.getName() + " to WAV format.");
                return null;
            }
        } catch (IllegalArgumentException | EncoderException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void normalizeAudioVolume(File audioFile) throws IOException {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 0);

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
                AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 0);
                normalizationDispatcher.addAudioProcessor(new GainProcessor(adjustmentFactor));

                String outputFilePath = audioFile.getParent() + "/normalized_" + audioFile.getName();
                WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), outputFilePath);
                normalizationDispatcher.addAudioProcessor(writer);

                normalizationDispatcher.run();
                System.out.println("Normalized: " + audioFile.getName());
                tree.addFile(audioFile.getAbsolutePath());
                cantidad = cantidad + 1;
            } else {
                // If adjustmentFactor <= 0, no adjustment needed
                System.out.println("Already normalized: " + audioFile.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarArchivosNormalizados(String directoryPath) {
        try {
            Files.walkFileTree(Paths.get(directoryPath), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith("normalized_")) {
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

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String[] parts = fileName.split("\\.");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }

    private String removeFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex);
    }
}
