/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dir;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import pruebas.AudioNormalizer;


public class AudioVisualizer2 extends JPanel implements ActionListener {
    private byte[] audioBytes;
    private Timer timer;
    private int currentIndex = 0;
    private int[] barHeights;

    public AudioVisualizer2(byte[] audioBytes) {
        this.audioBytes = audioBytes;
        this.barHeights = new int[100];
        Arrays.fill(barHeights, 0);
        //setOpaque(false);
        timer = new Timer(20, this); // Actualiza cada 20 ms (aproximadamente 50 FPS)
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

        int barWidth = getWidth() / barHeights.length;
        int halfHeight = getHeight() / 2;

        for (int i = 0; i < barHeights.length; i++) {
            int barHeight = barHeights[i];
            g.setColor(new Color(8, 7, 44));
            g.fillRect(i * barWidth, halfHeight - barHeight, barWidth - 2, 2 * barHeight);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentIndex >= audioBytes.length) {
            currentIndex = 0;
        }

        for (int i = 0; i < barHeights.length; i++) {
            int index = currentIndex + i * 10;
            if (index >= audioBytes.length) {
                break;
            }
            int value = audioBytes[index];
            int barHeight = getHeightFromAmplitude(value);
            barHeights[i] = Math.max(0, Math.min(barHeight, getHeight() / 2));
        }

        currentIndex += 100;
        repaint();
    }
    
    
   /*public static void main(String[] args) {
        JFrame frame = new JFrame("Audio Visualizer");
        AudioNormalizer audioProcessor = new AudioNormalizer("src/main/java/resources/excusa.wav");
        AudioVisualizer2 visualizer = new AudioVisualizer2(audioProcessor.getAudioBytes());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.add(visualizer);
        frame.setVisible(true);
    }*/
}
