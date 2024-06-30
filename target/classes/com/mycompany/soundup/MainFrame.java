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
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {

    private JLabel statusLabel;
    private JButton startButton;
    private JProgressBar progressBar;

    public MainFrame() {
        setTitle("Proceso en segundo plano");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        statusLabel = new JLabel("Presiona el botón para iniciar el proceso.");
        startButton = new JButton("Iniciar proceso");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Indeterminate progress bar

        startButton.addActionListener(e -> startBackgroundProcess());

        add(statusLabel);
        add(startButton);
        add(progressBar);
        progressBar.setVisible(false); // Initially hidden
    }

    private void startBackgroundProcess() {
        statusLabel.setText("Proceso en ejecución...");
        startButton.setEnabled(false); // Disable the button to prevent multiple starts
        progressBar.setVisible(true); // Show progress bar

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Aquí va el proceso que puede tardar un tiempo desconocido.
                // Este es solo un ejemplo que simula un trabajo.
                while (!isCancelled()) {
                    Thread.sleep(1000); // Simula el proceso en segundo plano
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setVisible(false); // Hide progress bar
                startButton.setEnabled(true); // Enable the button
                try {
                    get(); // Esta línea es importante para manejar excepciones
                    statusLabel.setText("Proceso terminado.");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error en el proceso.");
                }
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
    
}
