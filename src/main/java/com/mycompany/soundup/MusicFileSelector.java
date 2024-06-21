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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MusicFileSelector extends JFrame implements ActionListener {
    private JButton openButton;

    public MusicFileSelector() {
        // Configurar la ventana principal
        super("Selector de Archivos de Música");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 100);
        setLayout(new FlowLayout());

        // Crear y configurar el botón
        openButton = new JButton("Seleccionar Archivos o Carpetas");
        openButton.addActionListener(this);
        add(openButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            // Configurar los filtros de archivo
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos de Música (mp3, wav, flac)", "mp3", "wav", "flac");
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    JOptionPane.showMessageDialog(this,
                        "Seleccionaste: " + selectedFile.getAbsolutePath());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicFileSelector frame = new MusicFileSelector();
            frame.setVisible(true);
        });
    }
}
