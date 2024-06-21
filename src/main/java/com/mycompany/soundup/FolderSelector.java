/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.soundup;

/**
 *
 * @author Somils
 */
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FolderSelector {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Seleccionar Carpeta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);

        JButton button = new JButton("Seleccionar Carpeta");

        button.addActionListener(e -> {
            try {
                // Copiar el archivo selectFolder.vbs a una ubicación temporal si es necesario
                String scriptPath = "src/main/java/resources/selectFolder.vbs";
                String tempScriptPath = "selectFolderTemp.vbs";
                Files.copy(Paths.get(scriptPath), Paths.get(tempScriptPath));

                Process process = Runtime.getRuntime().exec("cscript //NoLogo " + tempScriptPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();
                String selectedFolderPath = output.toString().trim();
                if (!selectedFolderPath.equals("Cancelado")) {
                    JOptionPane.showMessageDialog(frame, "Carpeta seleccionada: " + selectedFolderPath);
                } else {
                    JOptionPane.showMessageDialog(frame, "Selección de carpeta cancelada");
                }

                // Eliminar el archivo temporal
                Files.delete(Paths.get(tempScriptPath));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel();
        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);
    }
}
