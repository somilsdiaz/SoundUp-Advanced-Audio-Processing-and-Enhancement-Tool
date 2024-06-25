/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pruebas;

import javax.swing.*;
import java.awt.*;

public class StaticAudioVisualizer extends JPanel {
    private byte[] audioBytes;
    private int[] barHeights;

    public StaticAudioVisualizer(byte[] audioBytes) {
        this.audioBytes = audioBytes;
        this.barHeights = new int[100];
        processAudio();
    }

    private void processAudio() {
        int segmentLength = audioBytes.length / barHeights.length;
        for (int i = 0; i < barHeights.length; i++) {
            int sum = 0;
            for (int j = 0; j < segmentLength; j++) {
                sum += Math.abs(audioBytes[i * segmentLength + j]);
            }
            barHeights[i] = sum / segmentLength;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int barWidth = getWidth() / barHeights.length;
        int maxHeight = getHeight();

        for (int i = 0; i < barHeights.length; i++) {
            int barHeight = (int) ((barHeights[i] / 128.0) * maxHeight);
            g.setColor(new Color(255, 0, 255)); // Color rosa
            g.fillRect(i * barWidth, maxHeight - barHeight, barWidth - 2, barHeight);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Static Audio Visualizer");
        AudioProcessor audioProcessor = new AudioProcessor("src/main/java/resources/excusa.wav");
        StaticAudioVisualizer visualizer = new StaticAudioVisualizer(audioProcessor.getAudioBytes());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(visualizer);
        frame.setVisible(true);
    }
}