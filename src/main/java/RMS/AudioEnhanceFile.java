package RMS;

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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AudioEnhanceFile {

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final double TARGET_RMS = 0.1;
    private static final Path TEMP_FILES_DIRECTORY = Paths.get(System.getProperty("user.home"), "tempfiles");

    static {
        try {
            if (!Files.exists(TEMP_FILES_DIRECTORY)) {
                Files.createDirectory(TEMP_FILES_DIRECTORY);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String Mejorar(String ruta, int reemplazar, double currentRMS) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try {
            List<Path> audioFiles = Files.walk(Paths.get(ruta))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            List<Future<String>> futures = audioFiles.stream()
                    .map(audioFile -> executor.submit(() -> processFile(audioFile, reemplazar, currentRMS)))
                    .collect(Collectors.toList());

            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    if (result != null) {
                        return result;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return null;
    }

    private static String processFile(Path audioFile, int reemplazar, double currentRMS) throws IOException {
        File wavFile = convertToWav(audioFile.toFile());
        String normalizedFilePath = normalizeAudioVolume(wavFile, audioFile.toFile(), currentRMS);
        deleteTemporaryFile(wavFile);

        if (reemplazar == 1) {
            Files.copy(Paths.get(normalizedFilePath), audioFile, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(Paths.get(normalizedFilePath));
            return audioFile.toString();
        }
        return normalizedFilePath;
    }

    public static BooleanDoublePair necesitaNormalizacion(String ruta) {
        File audioFile = new File(ruta);
        if (!audioFile.exists() || !isAudioFile(audioFile)) {
            throw new IllegalArgumentException("El archivo no existe o no es un archivo de audio válido.");
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<BooleanDoublePair> future = executor.submit(() -> {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 2);
            RMSProcessor rmsProcessor = new RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);
            dispatcher.run();

            double currentRMS = rmsProcessor.getRMS();
            boolean needNormalization = (currentRMS < TARGET_RMS) && ((TARGET_RMS - currentRMS) > 0.01);
            return new BooleanDoublePair(needNormalization, currentRMS);
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        return null;
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

    public static File convertToWav(File inputFile) throws IOException {
        String inputFileName = inputFile.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
        String outputFilePath = TEMP_FILES_DIRECTORY.toString() + "/temp_" + inputFileNameWithoutExtension + ".wav";
        File target = new File(outputFilePath);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(320000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(inputFile, target, attrs);
            return target;
        } catch (EncoderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertToWavString(String inputFilePath) throws IOException {
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists() || !isAudioFile(inputFile)) {
            throw new IllegalArgumentException("El archivo no existe o no es un archivo de audio válido.");
        }

        String inputFileName = inputFile.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
        String outputFilePath = TEMP_FILES_DIRECTORY.toString() + "/temp_" + inputFileNameWithoutExtension + ".wav";
        File target = new File(outputFilePath);

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(320000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("wav");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(inputFile, target, attrs);
            return target.getAbsolutePath();
        } catch (EncoderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String normalizeAudioVolume(File audioFile, File originalFile, double currentRMS) {
        try {
            double currentVolume = 20 * Math.log10(currentRMS);
            double targetVolume = 20 * Math.log10(TARGET_RMS);
            double adjustmentFactor = targetVolume - currentVolume;

            if (adjustmentFactor > 0) {
                ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
                Future<String> future = executor.submit(() -> {
                    AudioDispatcher normalizationDispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 1024, 0);
                    normalizationDispatcher.addAudioProcessor(new GainProcessor(adjustmentFactor));

                    File normalizedTempFile = new File(TEMP_FILES_DIRECTORY.toString() + "/normalized_" + audioFile.getName());
                    WaveformWriter writer = new WaveformWriter(normalizationDispatcher.getFormat(), normalizedTempFile.getAbsolutePath());
                    normalizationDispatcher.addAudioProcessor(writer);

                    normalizationDispatcher.run();
                    String stereoNormalizedPath = convertToWavString(normalizedTempFile.getAbsolutePath());
                    deleteTemporaryFile(normalizedTempFile);
                    return stereoNormalizedPath;
                });

                String result = future.get();
                executor.shutdown();
                return result;
            } else {
                System.out.println("Already normalized: " + originalFile.getName());
                return originalFile.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public static boolean replaceFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);

        if (!oldFile.exists() || !newFile.exists()) {
            System.out.println("Uno de los archivos no existe.");
            return false;
        }

        try {
            Files.deleteIfExists(oldFile.toPath());
            Files.copy(newFile.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("El archivo fue reemplazado exitosamente.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class RMSProcessor implements AudioProcessor {

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

    public static class BooleanDoublePair {

        public boolean flag;
        public double value;

        public BooleanDoublePair(boolean flag, double value) {
            this.flag = flag;
            this.value = value;
        }
    }
}
