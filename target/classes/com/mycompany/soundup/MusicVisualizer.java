
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.soundup;

/**
 *
 * @author Somils
 */
 
 /*
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MusicVisualizer extends Application {

    private static final String AUDIO_FILE_PATH = "ruta/a/tu/archivo.mp3";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int NUM_BARS = 60;
    private static final int BAR_WIDTH = WIDTH / NUM_BARS;

    private double[] magnitudes = new double[NUM_BARS];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new javafx.scene.Group(canvas));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Visualizer");
        primaryStage.show();

        new Thread(() -> {
            try {
                playAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new AnimationThread(gc).start();
    }

    private void playAudio() throws JavaLayerException, IOException {
        FileInputStream fis = new FileInputStream(AUDIO_FILE_PATH);
        Bitstream bitstream = new Bitstream(fis);
        Decoder decoder = new Decoder();
        AdvancedPlayer player = new AdvancedPlayer(fis, decoder);

        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent evt) {
                Arrays.fill(magnitudes, 0);
            }

            @Override
            public void playbackFrame(int frameNo, javazoom.jl.decoder.Frame frame) {
                try {
                    SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, bitstream);
                    short[] buffer = output.getBuffer();
                    int[] magnitudes = calculateMagnitudes(buffer);
                    synchronized (MusicVisualizer.this.magnitudes) {
                        for (int i = 0; i < magnitudes.length; i++) {
                            MusicVisualizer.this.magnitudes[i] = magnitudes[i];
                        }
                    }
                    bitstream.closeFrame();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        });

        player.play();
    }

    private int[] calculateMagnitudes(short[] buffer) {
        int[] magnitudes = new int[NUM_BARS];
        int samplesPerBar = buffer.length / NUM_BARS;
        for (int i = 0; i < NUM_BARS; i++) {
            int sum = 0;
            for (int j = 0; j < samplesPerBar; j++) {
                sum += Math.abs(buffer[i * samplesPerBar + j]);
            }
            magnitudes[i] = sum / samplesPerBar;
        }
        return magnitudes;
    }

    private class AnimationThread extends Thread {
        private GraphicsContext gc;

        public AnimationThread(GraphicsContext gc) {
            this.gc = gc;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (magnitudes) {
                    gc.clearRect(0, 0, WIDTH, HEIGHT);
                    for (int i = 0; i < NUM_BARS; i++) {
                        double height = magnitudes[i] / 500.0 * HEIGHT;
                        gc.setFill(Color.BLUE);
                        gc.fillRect(i * BAR_WIDTH, HEIGHT - height, BAR_WIDTH - 2, height);
                    }
                }
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
*/