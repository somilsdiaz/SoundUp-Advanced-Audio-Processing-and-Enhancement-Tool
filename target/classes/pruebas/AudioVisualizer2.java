/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pruebas;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AudioVisualizer2 extends JPanel {
    private byte[] audioBytes;
    private int[] audioData;

    public AudioVisualizer2(String audioFilePath) throws UnsupportedAudioFileException, IOException {
        // Leer el archivo de audio
        File audioFile = new File(audioFilePath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioInputStream.getFormat();
        audioBytes = audioInputStream.readAllBytes();

        // Convertir bytes de audio a datos de audio
        audioData = new int[audioBytes.length / format.getFrameSize()];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = audioBytes[i * format.getFrameSize()];
        }

        setPreferredSize(new Dimension(840, 140));
        setOpaque(false);  // Hacer el fondo transparente
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f)); // Transparencia total
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Restaurar opacidad

        g2d.setColor(Color.GREEN);

        int midHeight = getHeight() / 2;
        for (int i = 0; i < audioData.length - 1; i++) {
            int x1 = (int) ((i / (float) audioData.length) * getWidth());
            int x2 = (int) (((i + 1) / (float) audioData.length) * getWidth());
            int y1 = midHeight - (audioData[i] / 128 * midHeight);
            int y2 = midHeight - (audioData[i + 1] / 128 * midHeight);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame("Visualizador de Audio");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 150);
            frame.setLayout(new BorderLayout());
            
            // Crear un JPanel para mostrar el visualizador
            JPanel visualizerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Pintar el fondo transparente
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            };
            visualizerPanel.setPreferredSize(new Dimension(840, 140));
            visualizerPanel.setOpaque(false);

            AudioVisualizer2 visualizer = new AudioVisualizer2("src/main/java/resources/excusa.wav");
            visualizer.setOpaque(false); // Asegurarse de que el visualizador sea transparente también
            
            visualizerPanel.add(visualizer);
            frame.add(visualizerPanel, BorderLayout.CENTER);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
