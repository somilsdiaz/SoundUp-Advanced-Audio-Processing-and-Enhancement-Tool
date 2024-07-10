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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class AudioEnhanceFile {

    public static void main(String[] args) {
        String rutaDirectorio = "C:/Users/Somils/Desktop/Muestra/U Roy   Dread In A Babylon   Fire In A Trenchtown[1].mp3"; // Reemplaza con la ruta de tu directorio de archivos de audio
        int reemplazar = 0; // 1 para reemplazar el archivo original, 0 para no reemplazar
        // Probar método necesitaNormalizacion
        String rutaArchivo = "C:/Users/Somils/Desktop/Muestra/U Roy   Dread In A Babylon   Fire In A Trenchtown[1].mp3"; // Reemplaza con la ruta de tu archivo de audio
        BooleanDoublePair necesitaNormalizar = AudioEnhanceFile.necesitaNormalizacion(rutaArchivo);
        System.out.println("¿Necesita normalización?: " + necesitaNormalizar.flag);

        // Llamar al método Mejorar
        if (necesitaNormalizar.flag) {
            String resultado = AudioEnhanceFile.Mejorar(rutaDirectorio, reemplazar, necesitaNormalizar.value);
            if (resultado != null) {
                System.out.println("Archivo mejorado: " + resultado);
            } else {
                System.out.println("No se pudo mejorar el archivo.");
            }

            /*   // Probar método eliminarArchivo
        boolean eliminado = AudioEnhanceFile.eliminarArchivo(rutaArchivo);
        System.out.println("¿Archivo eliminado?: " + eliminado);*/
        } else {
            System.out.println("El archivo no necesita mejora");
        }
    }

    public static String Mejorar(String ruta, int reemplazar, double currentRMS) {
        String directoryPath = ruta; // Reemplaza con la ruta de tu directorio
        try {
            List<Path> audioFiles = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isAudioFile(path.toFile()))
                    .collect(Collectors.toList());

            for (Path audioFile : audioFiles) {
                File wavFile = convertToWav(audioFile.toFile());
                String normalizedFilePath = normalizeAudioVolume(wavFile, audioFile.toFile(), currentRMS);
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

    public static BooleanDoublePair necesitaNormalizacion(String ruta) {
        try {
            File audioFile = new File(ruta);
            if (!audioFile.exists() || !isAudioFile(audioFile)) {
                throw new IllegalArgumentException("El archivo no existe o no es un archivo de audio válido.");
            }

            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), 44100, 2048, 2);
            RMSProcessor rmsProcessor = new RMSProcessor();
            dispatcher.addAudioProcessor(rmsProcessor);

            dispatcher.run();

            double targetRMS = 0.1; // Target RMS value
            double currentRMS = rmsProcessor.getRMS();
            boolean need;
            if ((currentRMS < targetRMS) && ((targetRMS - currentRMS) > 0.01)) {
                need = true;
            } else {
                need = false;
            }
            BooleanDoublePair resultado = new BooleanDoublePair(need, currentRMS);

            return resultado; // Retornar true si necesita normalización
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retornar false en caso de error
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

    public static File convertToWav(File inputFile) {
        // Obtener la ruta del archivo de salida con la extensión .wav
        String inputFileName = inputFile.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));

        // Generar la ruta del archivo de salida con "temp_" al inicio y la extensión .wav
        String outputFilePath = inputFile.getParent() + "/temp_" + inputFileNameWithoutExtension + ".wav";
        File target = new File(outputFilePath);

        // Atributos de audio mejorados para mantener la calidad
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");

        // Aquí podemos aumentar la tasa de bits para mejorar la calidad
        // Establece la tasa de bits en 320 kbps, una calidad bastante alta
        audio.setBitRate(320000);
        audio.setChannels(2); // Mantén el número de canales estéreo
        audio.setSamplingRate(44100); // Mantén la tasa de muestreo en 44.1 kHz

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

    public static String convertToWavString(String inputFilePath) {
        // Crear un objeto File a partir de la ruta del archivo de entrada
        File inputFile = new File(inputFilePath);

        // Obtener la ruta del archivo de salida con la extensión .wav
        String inputFileName = inputFile.getName();
        String inputFileNameWithoutExtension = inputFileName.substring(0, inputFileName.lastIndexOf('.'));

        // Generar la ruta del archivo de salida con "temp_" al inicio y la extensión .wav
        String outputFilePath = inputFile.getParent() + "/temp_" + inputFileNameWithoutExtension + ".wav";
        File target = new File(outputFilePath);

        // Atributos de audio mejorados para mantener la calidad
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");

        // Aquí podemos aumentar la tasa de bits para mejorar la calidad
        // Establece la tasa de bits en 320 kbps, una calidad bastante alta
        audio.setBitRate(320000);
        audio.setChannels(2); // Mantén el número de canales estéreo
        audio.setSamplingRate(44100); // Mantén la tasa de muestreo en 44.1 kHz

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

    private static String normalizeAudioVolume(File audioFile, File originalFile, double currentRMS) {
        try {

            // Calculate target RMS value
            double targetRMS = 0.1; // Target RMS value

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

    public static boolean replaceFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);

        if (!oldFile.exists()) {
            System.out.println("El archivo antiguo no existe: " + oldFilePath);
            return false;
        }

        if (!newFile.exists()) {
            System.out.println("El archivo nuevo no existe: " + newFilePath);
            return false;
        }

        // Intentar borrar el archivo antiguo
        if (!oldFile.delete()) {
            System.out.println("No se pudo eliminar el archivo antiguo: " + oldFilePath);
            return false;
        }

        // Copiar y renombrar el nuevo archivo a la ubicación del archivo antiguo
        try {
            Files.copy(newFile.toPath(), oldFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("El archivo fue reemplazado exitosamente.");
            return true;
        } catch (IOException e) {
            System.out.println("Error al copiar el archivo nuevo a la ubicación del archivo antiguo: " + e.getMessage());
            return false;
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

    public static class BooleanDoublePair {

        public boolean flag;
        public double value;

        public BooleanDoublePair(boolean flag, double value) {
            this.flag = flag;
            this.value = value;
        }

    }
}
