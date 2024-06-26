package pruebas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;


public class AudioVisualizer2 extends JPanel implements ActionListener {

    private byte[] audioBytes;
    private Timer timer;
    private int currentIndex = 0;
    private int[] barHeights;
    private int sampleRate = 44100; // Supón que la tasa de muestreo es de 44.1 kHz
    private int fps = 60; // Cuadros por segundo
    private int bytesPerFrame = sampleRate / fps; // Bytes por cuadro
    private long startTime;

    public AudioVisualizer2(byte[] audioBytes) {
        this.audioBytes = audioBytes;
        this.barHeights = new int[100];
        Arrays.fill(barHeights, 0);
        timer = new Timer(1000 / fps, this); // Actualiza a la tasa de cuadros por segundo especificada
        startTime = System.currentTimeMillis();
        timer.start();
    }

    private int getHeightFromAmplitude(int amplitude) {
        return (int) ((amplitude / 128.0) * getHeight() / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (audioBytes == null) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int numBars = Math.min(panelWidth / 2, barHeights.length); // Número de barras basado en el ancho del panel
        int barWidth = Math.max(1, panelWidth / numBars); // Asegúrate de que el ancho de la barra sea al menos 1
        int halfHeight = panelHeight / 2;

        for (int i = 0; i < numBars; i++) {
            int barHeight = barHeights[i];
            g.setColor(new Color(255, 0, 255));
            g.fillRect(i * barWidth, halfHeight - barHeight, barWidth - 1, 2 * barHeight); // Ajuste de -1 para espacio entre barras
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        currentIndex = (int)((elapsedTime / 1000.0) * sampleRate);

        if (currentIndex >= audioBytes.length) {
            timer.stop(); // Detén el temporizador cuando se termine el audio
            return;
        }

        int panelWidth = getWidth();
        int numBars = Math.min(panelWidth / 2, barHeights.length); // Número de barras basado en el ancho del panel

        for (int i = 0; i < numBars; i++) {
            int index = currentIndex + i * bytesPerFrame / numBars;
            if (index >= audioBytes.length) {
                barHeights[i] = 0; // Establece la altura de la barra a 0 cuando el audio ha terminado
            } else {
                int value = audioBytes[index];
                int barHeight = getHeightFromAmplitude(value);
                barHeights[i] = Math.max(0, Math.min(barHeight, getHeight() / 2));
            }
        }

        repaint();
    }
}

