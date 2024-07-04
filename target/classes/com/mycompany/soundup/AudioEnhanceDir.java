package com.mycompany.soundup;

import Directorios.DirectoryTree;

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
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AudioEnhanceDir {

    public static DirectoryTree tree;
    public int cantidad = 0;
    public static AtomicInteger totalAudioFiles = new AtomicInteger();

public static int returnNumeroActual () {
    return totalAudioFiles.get();
}
    public static void main(String[] args) {

        String ruta = "C:/Users/Somils/Music/SALSAS  ROMANTICAS";
        totalAudioFiles.set(contarArchivosDeAudio(ruta));

        List<RutaRmsPar> NecesitaNormalizacion = EncontrarNecesitanNormalizar(ruta);
        System.out.println("Cantidad de archivos que necesitan ser normalizados: " + NecesitaNormalizacion.size());
        System.out.println("Cantidad de archivos restantes: " + totalAudioFiles.get());
        if (NecesitaNormalizacion != null && !NecesitaNormalizacion.isEmpty()) {
            System.out.println("Archivos que necesitan normalización:");
            for (RutaRmsPar archivo : NecesitaNormalizacion) {
                System.out.println("Ruta: " + archivo.rutaOriginal + ", Valor RMS: " + archivo.RMS);
            }
        } else {
            System.out.println("No se encontraron archivos que necesiten normalización o hubo un error.");
        }
        System.out.println("Vamos a mejorar los audios que necesitan normalizacion");
        List<Rutas> estanMejorados = vamosAmejorar(NecesitaNormalizacion);

        if (estanMejorados != null && !estanMejorados.isEmpty()) {
            for (Rutas rutas : estanMejorados) {
                System.out.println("Ruta original: " + rutas.rutaOriginal + "Ruta mejorada: " + rutas.rutaMejorada);
            }

        }
        eliminarArchivosNormalizados(ruta);
    }

    public static int contarArchivosDeAudio(String ruta) {
        AtomicInteger audioFileCount = new AtomicInteger(0);
        String directoryPath = ruta;

        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(audioFiles.size(), Runtime.getRuntime().availableProcessors()));
            customThreadPool.submit(()
                    -> audioFiles.parallelStream().forEach(audioFile -> {
                        audioFileCount.incrementAndGet();
                    })
            ).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return audioFileCount.get();
    }

    public static List<RutaRmsPar> EncontrarNecesitanNormalizar(String ruta) {
        tree = new DirectoryTree(ruta);
        
        
        totalAudioFiles.set(contarArchivosDeAudio(ruta));
        List<RutaRmsPar> necesitaNormalizacion = Collections.synchronizedList(new ArrayList<>());
        String directoryPath = ruta;

        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(audioFiles.size(), Runtime.getRuntime().availableProcessors()));

            customThreadPool.submit(()
                    -> audioFiles.parallelStream().forEach(audioFile -> {
                        AudioEnhanceFile.BooleanDoublePair need = AudioEnhanceFile.necesitaNormalizacion(audioFile.toAbsolutePath().toString());
                        if (need.flag) {
                            RutaRmsPar necesita = new RutaRmsPar(audioFile.toAbsolutePath().toString(), need.value);
                            necesitaNormalizacion.add(necesita);
                            tree.addFile(audioFile.toAbsolutePath().toString());
                        }
                        totalAudioFiles.decrementAndGet();
                    })
            ).get();

            return necesitaNormalizacion;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Rutas> vamosAmejorar(List<RutaRmsPar> necesitanNormalizar) {
        List<Rutas> estanNormalizados = Collections.synchronizedList(new ArrayList<>());

        ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(necesitanNormalizar.size(), Runtime.getRuntime().availableProcessors()));

        try {
            customThreadPool.submit(()
                    -> necesitanNormalizar.parallelStream().forEach(archivo -> {
                        String rutaOriginal = archivo.rutaOriginal;
                        String rutaMejorada = AudioEnhanceFile.Mejorar(rutaOriginal, 0, archivo.RMS);
                        Rutas ruta = new Rutas(rutaOriginal, rutaMejorada);
                        estanNormalizados.add(ruta);
                    })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return estanNormalizados;
    }

    public static void RemplazarNormalizados(List<Rutas> rutas) {
        ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(rutas.size(), Runtime.getRuntime().availableProcessors()));

        try {
            customThreadPool.submit(()
                    -> rutas.parallelStream().forEach(ruta -> {
                        AudioEnhanceFile.replaceFile(ruta.rutaOriginal, ruta.rutaMejorada);
                    })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
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

    public static void eliminarArchivosNormalizados(String directoryPath) {
        try {
            Files.walkFileTree(Paths.get(directoryPath), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith("normalized_temp_")) {
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

    public static class RutaRmsPar {

        public String rutaOriginal;
        public double RMS;

        public RutaRmsPar(String rutaOriginal, double RMS) {
            this.rutaOriginal = rutaOriginal;
            this.RMS = RMS;
        }

    }

    public static class Rutas {

        public String rutaOriginal;
        public String rutaMejorada;

        public Rutas(String rutaOriginal, String rutaMejorada) {
            this.rutaOriginal = rutaOriginal;
            this.rutaMejorada = rutaMejorada;

        }

    }
}
