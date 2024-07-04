/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.soundup;

/**
 *
 * @author Somils
 */
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.AudioEvent;

public class GainProcessor implements AudioProcessor {

    private final double gainDB;

    public GainProcessor(double gainDB) {
        this.gainDB = gainDB;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] buffer = audioEvent.getFloatBuffer();
        double gainFactor = Math.pow(10.0, gainDB / 20.0); // Convertir dB a factor de ganancia
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] *= gainFactor;
        }
        return true;
    }

    @Override
    public void processingFinished() {
    }
}
