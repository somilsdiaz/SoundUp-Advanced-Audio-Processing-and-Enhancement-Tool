package RMS;

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
import java.util.concurrent.ForkJoinPool;
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
    static Path tempFilesDirectory = Paths.get("tempfiles");

    public static int returnNumeroActual() {
        return totalAudioFiles.get();
    }

    /*   public static void main(String[] args) {

        String ruta = "C:/Users/Somils/Desktop/muestra";
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

        /*   if (estanMejorados != null && !estanMejorados.isEmpty()) {
            for (Rutas rutas : estanMejorados) {
                System.out.println("Ruta original: " + rutas.rutaOriginal + "Ruta mejorada: " + rutas.rutaMejorada);
            }

        }*/
    //   eliminarArchivosNormalizados(ruta);
    //}
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

    public static ListasRMS_PDA EncontrarNecesitanNormalizar(String ruta) {
        tree = new DirectoryTree(ruta);
        treePDA = new DirectoryTree(ruta);
        totalAudioFiles = new AtomicInteger(contarArchivosDeAudio(ruta));

        List<RutaRmsPar> necesitaNormalizacion = Collections.synchronizedList(new ArrayList<>());
        List<Rutas> goPDA = Collections.synchronizedList(new ArrayList<>());

        ListasRMS_PDA listas = new ListasRMS_PDA(necesitaNormalizacion, goPDA);

        try {
            List<Path> audioFiles = Files.walk(Paths.get(ruta))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            int numCores = Runtime.getRuntime().availableProcessors();
            ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(audioFiles.size(), numCores));

            customThreadPool.submit(() -> audioFiles.parallelStream().forEach(audioFile -> {
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
            })).get();

            listas.listaRMS = necesitaNormalizacion;
            listas.listaPDA = goPDA;
            return listas;
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void processAudioFile(Path audioFile, List<Rutas> goPDA, DirectoryTree treePDA) {
        try {
            String AudioOriginal = AudioEnhanceFile.convertToWavString(audioFile.toAbsolutePath().toString());
            File AudioOriginalWav = new File(AudioOriginal);
            String inputFileNameWithoutExtension = AudioOriginalWav.getName().replaceFirst("[.][^.]+$", "");

            String mascaraAudioPDApatch = tempFilesDirectory.toString() + "/pdaMask_" + inputFileNameWithoutExtension + ".wav";
            File mascaraAudioPDA = new File(mascaraAudioPDApatch);

            try {
                PDA.AudioEnhancer.enhanceAudio(AudioOriginalWav, mascaraAudioPDA);
                String normalized_mask = PDA.AudioEnhancer.normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav);
                String normalized_temp = AudioEnhanceFile.convertToWavString(PDA.AudioEnhancer.normalizeAudioVolume(mascaraAudioPDA, AudioOriginalWav));
                File AudioMidlePDA = new File(normalized_temp);

                String AudioPDApatch = tempFilesDirectory.toString() + "/PDA_" + inputFileNameWithoutExtension + ".wav";
                File AudioPDA = new File(AudioPDApatch);
                PDA.AudioEnhancer.mixAudioFiles(AudioMidlePDA, AudioOriginalWav, AudioPDA);

                RMS.AudioEnhanceFile.eliminarArchivo(mascaraAudioPDApatch);
                RMS.AudioEnhanceFile.eliminarArchivo(normalized_temp);
                RMS.AudioEnhanceFile.eliminarArchivo(normalized_mask);
                RMS.AudioEnhanceFile.eliminarArchivo(AudioOriginal);

                goPDA.add(new Rutas(audioFile.toAbsolutePath().toString(), AudioPDApatch, 0.0));
                treePDA.addFile(audioFile.toAbsolutePath().toString());
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(AudioEnhanceDir.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AudioEnhanceDir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List<Rutas> vamosAmejorar(List<RutaRmsPar> necesitanNormalizar) {
        List<Rutas> estanNormalizados = Collections.synchronizedList(new ArrayList<>());

        ForkJoinPool customThreadPool = new ForkJoinPool(Math.min(necesitanNormalizar.size(), Runtime.getRuntime().availableProcessors()));

        try {
            customThreadPool.submit(()
                    -> necesitanNormalizar.parallelStream().forEach(archivo -> {
                        String rutaOriginal = archivo.rutaOriginal;
                        String rutaMejorada = AudioEnhanceFile.Mejorar(rutaOriginal, 0, archivo.RMS);
                        Rutas ruta = new Rutas(rutaOriginal, rutaMejorada, archivo.RMS);
                        estanNormalizados.add(ruta);
                    })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return estanNormalizados;
    }

    public static void RemplazarMuchosArchivos(List<Rutas> rutas) {
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

    public static boolean isAudioFile(File file) {
        String[] audioExtensions = {".wav", ".mp3", ".flac", ".ogg", ".m4a"};
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
            String directoryPath = tempFilesDirectory.toString();
            Files.walkFileTree(Paths.get(directoryPath), EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith("temp_normalized_temp_")) {
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

    public static class RutasPDA {

        public String Original;
        public String OrginalWav;
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
