package com.mycompany.soundup;

import Directorios.DirectoryEntry;
import Directorios.DirectoryFiles;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.GainProcessor;
import Directorios.DirectoryTree;
import Directorios.FileEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AudioEnhanceDir {

    public static DirectoryTree tree;
    public int cantidad = 0;

    public static void main(String[] args) {
        AudioEnhanceDir ae = new AudioEnhanceDir();
        ae.MejorarDir("C:/Users/Somils/Desktop/Muestra");
        tree.printTree();
        DirectoryFiles directoryFiles = tree.getAllDirectoriesAndFiles();
        System.out.println("\nTodas las carpetas:");
        for (DirectoryEntry dir : directoryFiles.directories) {
            System.out.println("ID: " + dir.id + ", Path: " + dir.path);
        }

        System.out.println("\nTodos los archivos:");
        for (FileEntry FileEntry : directoryFiles.files) {
            System.out.println("Directory ID: " + FileEntry.directoryId + ", File Path: " + FileEntry.filePath + ", Directory Path: " + FileEntry.directoryPath);
        }

    }

    public static DirectoryFiles DirectorioCompleto() {
        AudioEnhanceDir ae = new AudioEnhanceDir();
        ae.MejorarDir("C:/Users/Somils/Desktop/Muestra");
        tree.printTree();
        DirectoryFiles directoryFiles = tree.getAllDirectoriesAndFiles();
        System.out.println("\nTodas las carpetas:");
        for (DirectoryEntry dir : directoryFiles.directories) {
            System.out.println("ID: " + dir.id + ", Path: " + dir.path);
        }

        System.out.println("\nTodos los archivos:");
        for (FileEntry FileEntry : directoryFiles.files) {
            //   System.out.println("Directory ID: " + FileEntry.directoryId + ", File Path: " + FileEntry.filePath + ", Directory Path: " + FileEntry.directoryPath);
        }
        return directoryFiles;

    }

    public void MejorarDir(String directoryPath) {
        tree = new DirectoryTree(directoryPath);
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".wav")) // Suponiendo archivos .wav
                    .collect(Collectors.toList());

            // Usa un ThreadPoolExecutor para procesar archivos en paralelo
            int numCores = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numCores);

            for (Path audioFile : audioFiles) {
                executor.submit(() -> {
                    try {
                        normalizeAudioVolume(audioFile.toFile());
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
}
