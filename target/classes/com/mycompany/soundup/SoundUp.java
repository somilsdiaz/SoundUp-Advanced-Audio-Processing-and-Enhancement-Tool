/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.soundup;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Somils
 */
public class SoundUp {

    public static void main(String[] args) {
        Path tempFilesDirectory = Paths.get("tempfiles");

        try {
            // Crear la carpeta si no existe
            if (!Files.exists(tempFilesDirectory)) {
                Files.createDirectory(tempFilesDirectory);
            }

            // Eliminar todos los archivos dentro de la carpeta
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempFilesDirectory)) {
                for (Path file : stream) {
                    Files.delete(file);
                }
            }
            System.out.println("Todos los archivos en la carpeta 'tempfiles' han sido eliminados.");
        } catch (IOException e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
        }
        StartMenu inicio = new StartMenu();
        inicio.setVisible(true);
    }
}
