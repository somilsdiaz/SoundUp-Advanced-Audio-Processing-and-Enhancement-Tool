package pruebas;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class AudioVisualizer extends JPanel implements ActionListener {

    private byte[] audioBytes;
    private Timer timer;
    private int currentIndex = 0;
    private int[] barHeights;
    private BufferedImage backgroundImage;

    public AudioVisualizer(byte[] audioBytes) {
        this.audioBytes = audioBytes;
        this.barHeights = new int[100];
        Arrays.fill(barHeights, 0);
        setOpaque(true); // Hacer el fondo opaco para que se dibuje la imagen
        timer = new Timer(50, this); // Actualiza cada 50 ms (aproximadamente 20 FPS)
        timer.start();
        try {
            InputStream imageSrc = getClass().getResourceAsStream("/resources/groundVisual.png");
            backgroundImage = ImageIO.read(imageSrc); // Carga tu imagen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getHeightFromAmplitude(int amplitude) {
        return (int) ((Math.abs(amplitude) / 128.0) * getHeight() / 1.2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (audioBytes == null) {
            return;
        }

        // Dibujar la imagen de fondo
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        int barWidth = getWidth() / barHeights.length;
        int maxHeight = getHeight();

        for (int i = 0; i < barHeights.length; i++) {
            int barHeight = barHeights[i];
            g.setColor(new Color(255, 0, 255)); // Color rosa
            g.fillRect(i * barWidth, maxHeight - barHeight, barWidth - 2, barHeight);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentIndex >= audioBytes.length) {
            currentIndex = 0;
        }

        for (int i = 0; i < barHeights.length; i++) {
            int index = currentIndex + i * (audioBytes.length / barHeights.length);
            if (index >= audioBytes.length) {
                break;
            }
            int value = audioBytes[index];
            int barHeight = getHeightFromAmplitude(value);
            barHeights[i] = Math.max(barHeights[i] - 10, barHeight); // Restar un valor para hacer el efecto de bajada
        }

        currentIndex += 10; // Avanza menos muestras por actualización
        repaint();
    }

    /* public static void main(String[] args) {
        JFrame frame = new JFrame("Audio Visualizer");
        AudioProcessor audioProcessor = new AudioProcessor("src/main/java/resources/excusa.wav");
        AudioVisualizer visualizer = new AudioVisualizer(audioProcessor.getAudioBytes());
        frame.add(visualizer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }*/
}
