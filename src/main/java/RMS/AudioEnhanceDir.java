package RMS;

import Directorios.DirectoryTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioEnhanceDir {

    public static int TotalCanciones;
    public static DirectoryTree tree;
    public static DirectoryTree treePDA;
    public int cantidad = 0;
    public static AtomicInteger totalAudioFiles = new AtomicInteger();
    static Path tempFilesDirectory = Paths.get(System.getProperty("user.home"), "tempfiles");

    public static int returnNumeroActual() {
        return totalAudioFiles.get();
    }

    public static int contarArchivosDeAudio(String ruta) {
        AtomicInteger audioFileCount = new AtomicInteger(0);

        try {
            List<Path> audioFiles = Files.walk(Paths.get(ruta))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            audioFileCount.set(audioFiles.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return audioFileCount.get();
    }

    public static ListasRMS_PDA EncontrarNecesitanNormalizar(String ruta) {
        tree = new DirectoryTree(ruta);
        treePDA = new DirectoryTree(ruta);
        totalAudioFiles = new AtomicInteger(contarArchivosDeAudio(ruta));

        List<RutaRmsPar> necesitaNormalizacion = new CopyOnWriteArrayList<>();
        List<Rutas> goPDA = new CopyOnWriteArrayList<>();

        ListasRMS_PDA listas = new ListasRMS_PDA(necesitaNormalizacion, goPDA);

        try {
            List<Path> audioFiles = Files.walk(Paths.get(ruta))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            int numCores = Runtime.getRuntime().availableProcessors();
            long maxMemory = Runtime.getRuntime().maxMemory();
            int chunkSize = calculateChunkSize(maxMemory, audioFiles.size(), numCores);

            ExecutorService executorService = Executors.newFixedThreadPool(numCores);

            for (int i = 0; i < audioFiles.size(); i += chunkSize) {
                List<Path> chunk = audioFiles.subList(i, Math.min(audioFiles.size(), i + chunkSize));

                List<CompletableFuture<Void>> futures = chunk.stream()
                        .map(audioFile -> CompletableFuture.runAsync(() -> {
                            AudioEnhanceFile.BooleanDoublePair need = AudioEnhanceFile.necesitaNormalizacion(audioFile.toAbsolutePath().toString());
                            if (need.flag) {
                                necesitaNormalizacion.add(new RutaRmsPar(audioFile.toAbsolutePath().toString(), need.value));
                                tree.addFile(audioFile.toAbsolutePath().toString());
                                synchronized (AudioEnhanceDir.class) {
                                    TotalCanciones++;
                                }
                            } else {
                                processAudioFile(audioFile, goPDA, treePDA);
                            }
                            totalAudioFiles.decrementAndGet();
                        }, executorService))
                        .collect(Collectors.toList());

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }

            listas.listaRMS = necesitaNormalizacion;
            listas.listaPDA = goPDA;
            executorService.shutdown();
            return listas;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int calculateChunkSize(long maxMemory, int totalFiles, int numCores) {
        int baseChunkSize = Math.max(1, totalFiles / (numCores * 2));
        long estimatedMemoryPerFile = maxMemory / (totalFiles + 1);
        long maxChunkSizeBasedOnMemory = maxMemory / estimatedMemoryPerFile;

        return (int) Math.min(baseChunkSize, maxChunkSizeBasedOnMemory);
    }

    private static void processAudioFile(Path audioFile, List<Rutas> goPDA, DirectoryTree treePDA) {
        try {
            String audioOriginalPath = audioFile.toAbsolutePath().toString();
            String audioOriginalWavPath = AudioEnhanceFile.convertToWavString(audioOriginalPath);
            File audioOriginalWav = new File(audioOriginalWavPath);
            String inputFileNameWithoutExtension = getFileNameWithoutExtension(audioOriginalWav);

            String tempDirectory = tempFilesDirectory.toString();
            String pdaMaskPath = String.format("%s/pdaMask_%s.wav", tempDirectory, inputFileNameWithoutExtension);
            File pdaMaskFile = new File(pdaMaskPath);

            try {
                PDA.AudioEnhancer.enhanceAudio(audioOriginalWav, pdaMaskFile);
                String normalizedMaskPath = PDA.AudioEnhancer.normalizeAudioVolume(pdaMaskFile, audioOriginalWav);
                String normalizedTempPath = AudioEnhanceFile.convertToWavString(normalizedMaskPath);
                File audioMidlePDA = new File(normalizedTempPath);

                String pdaOutputPath = String.format("%s/PDA_%s.wav", tempDirectory, inputFileNameWithoutExtension);
                File audioPDA = new File(pdaOutputPath);
                PDA.AudioEnhancer.mixAudioFiles(audioMidlePDA, audioOriginalWav, audioPDA);

                deleteTempFiles(pdaMaskPath, normalizedTempPath, normalizedMaskPath, audioOriginalWavPath);

                goPDA.add(new Rutas(audioOriginalPath, pdaOutputPath, 0.0));
                treePDA.addFile(audioOriginalPath);
            } catch (UnsupportedAudioFileException | IOException ex) {
                Logger.getLogger(AudioEnhanceDir.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AudioEnhanceDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private static void deleteTempFiles(String... filePaths) {
        for (String filePath : filePaths) {
            RMS.AudioEnhanceFile.eliminarArchivo(filePath);
        }
    }

    public static List<Rutas> vamosAmejorar(List<RutaRmsPar> necesitanNormalizar) {
        List<Rutas> estanNormalizados = new CopyOnWriteArrayList<>();

        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numCores);

        try {
            List<CompletableFuture<Void>> futures = necesitanNormalizar.stream()
                    .map(archivo -> CompletableFuture.runAsync(() -> {
                        String rutaOriginal = archivo.rutaOriginal;
                        String rutaMejorada = AudioEnhanceFile.Mejorar(rutaOriginal, 0, archivo.RMS);
                        Rutas ruta = new Rutas(rutaOriginal, rutaMejorada, archivo.RMS);
                        estanNormalizados.add(ruta);
                    }, executorService))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        return estanNormalizados;
    }

    public static void RemplazarMuchosArchivos(List<Rutas> rutas) {
        int numCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(numCores);

        try {
            List<CompletableFuture<Void>> futures = rutas.stream()
                    .map(ruta -> CompletableFuture.runAsync(() -> AudioEnhanceFile.replaceFile(ruta.rutaOriginal, ruta.rutaMejorada), executorService))
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    public static boolean isAudioFile(File file) {
        String[] audioExtensions = {".wav", ".mp3", ".flac", ".ogg", ".m4a", ".wma"};
        String fileName = file.getName().toLowerCase();
        for (String ext : audioExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static void eliminarArchivosNormalizados() {
        try {
            Files.walkFileTree(tempFilesDirectory, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith("temp_normalized_temp_")) {
                        Files.delete(file);
                        System.out.println("Deleted: " + file);
                    }
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

    public static class ListasRMS_PDA {
        public List<RutaRmsPar> listaRMS;
        public List<Rutas> listaPDA;

        public ListasRMS_PDA(List<RutaRmsPar> listaRMS, List<Rutas> listaPDA) {
            this.listaRMS = listaRMS;
            this.listaPDA = listaPDA;
        }
    }

    public static class Rutas {
        public String rutaOriginal;
        public String rutaMejorada;
        public double RMS;

        public Rutas(String rutaOriginal, String rutaMejorada, double RMS) {
            this.rutaOriginal = rutaOriginal;
            this.rutaMejorada = rutaMejorada;
            this.RMS = RMS;
        }
    }
}
