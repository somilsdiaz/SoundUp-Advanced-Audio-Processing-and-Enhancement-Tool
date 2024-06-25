package com.mycompany.soundup;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

public class SongVisualizer extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BAR_WIDTH = 5;
    private static final int SPACE_BETWEEN_BARS = 2;

    private int[] frequencies;
    private boolean playing;

    public SongVisualizer() {
        super("Song Visualizer");

        this.frequencies = new int[WIDTH / (BAR_WIDTH + SPACE_BETWEEN_BARS)];
        this.playing = false;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setVisible(true);
    }

    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.GREEN);

        for (int i = 0; i < frequencies.length; i++) {
            int barHeight = frequencies[i];
            int x = i * (BAR_WIDTH + SPACE_BETWEEN_BARS);
            int y = HEIGHT - barHeight;

            g.fillRect(x, y, BAR_WIDTH, barHeight);
        }
    }

    public void visualize(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            AudioFormat format = audioInputStream.getFormat();
            float sampleRate = format.getSampleRate();
            int bufferSize = (int) sampleRate * format.getFrameSize();
            byte[] audioData = new byte[bufferSize];
            int bytesRead;

            clip.start();
            playing = true;

            while (playing && (bytesRead = audioInputStream.read(audioData)) != -1) {
                processAudioData(audioData, bytesRead);
                repaint();
                Thread.sleep(30);
            }

            clip.stop();
            clip.close();
            audioInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processAudioData(byte[] audioData, int bytesRead) {
        int numBars = frequencies.length;
        int samplesPerBar = bytesRead / (numBars * 2);
        int[] maxAmplitudes = new int[numBars];

        for (int i = 0; i < numBars; i++) {
            for (int j = 0; j < samplesPerBar; j++) {
                int amplitude = Math.abs((audioData[(i * samplesPerBar + j) * 2] << 8) | (audioData[(i * samplesPerBar + j) * 2 + 1] & 0xff));
                maxAmplitudes[i] = Math.max(maxAmplitudes[i], amplitude);
            }
        }

        for (int i = 0; i < numBars; i++) {
            frequencies[i] = maxAmplitudes[i] * HEIGHT / Short.MAX_VALUE;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        render(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SongVisualizer visualizer = new SongVisualizer();
            visualizer.visualize("/resources/excusa.wav");  // Ajusta la ruta del archivo de audio
        });
    }
}

