package PDA;

import javax.sound.sampled.AudioFormat;
import java.util.Arrays;

/**
 * Class for audio noise processing including noise reduction.
 */
public class NoiseProcessor {

    /**
     * Apply noise reduction for cleaner sound
     */
    public static float[] applyNoiseReduction(float[] samples, AudioFormat format, float noiseReduction) {
        // Simple noise gate with spectral subtraction principles
        float threshold = 0.01f * (1 - noiseReduction);
        float[] processed = Arrays.copyOf(samples, samples.length);
        int channels = format.getChannels();
        
        // Estimate noise floor (simplified)
        float noiseFloor = 0;
        int sampleCount = Math.min(10000, samples.length / channels);
        
        for (int i = 0; i < sampleCount * channels; i += channels) {
            for (int ch = 0; ch < channels; ch++) {
                noiseFloor += Math.abs(samples[i + ch]) / sampleCount / channels;
            }
        }
        
        // Apply noise reduction
        for (int i = 0; i < samples.length; i++) {
            float absValue = Math.abs(samples[i]);
            if (absValue < noiseFloor + threshold) {
                float reductionFactor = Math.max(0, (absValue - noiseFloor) / threshold);
                processed[i] *= reductionFactor * reductionFactor; // Quadratic for smoother transition
            }
        }
        
        return processed;
    }
    
    /**
     * Apply harmonic exciter for added brilliance and presence
     */
    public static float[] applyHarmonicExciter(float[] samples, AudioFormat format, float highClarity) {
        // Reduced drive and blend to prevent saturation
        float drive = 1.2f;
        float blend = 0.2f * highClarity;
        float[] enhanced = Arrays.copyOf(samples, samples.length);
        
        // Adjust drive based on sample rate for consistent harmonic generation
        float sampleRateAdjust = 44100f / format.getSampleRate();
        drive *= sampleRateAdjust;
        
        // Apply different processing based on mono/stereo
        int channels = format.getChannels();
        
        for (int i = 0; i < samples.length; i++) {
            // Generate harmonics through soft saturation
            float harmonics = (float) Math.tanh(samples[i] * drive);
            
            // High-pass the harmonics (simplified high-pass)
            if (i > 0 && (i % channels == i - 1 % channels)) { // Only compare samples in same channel
                harmonics = 0.9f * (harmonics - samples[i-channels]);
            }
            
            // Blend with original
            enhanced[i] = samples[i] + harmonics * blend;
            
            // Add soft limiting to prevent saturation
            if (enhanced[i] > 0.95f) enhanced[i] = 0.95f - (enhanced[i] - 0.95f) * 0.5f;
            if (enhanced[i] < -0.95f) enhanced[i] = -0.95f - (enhanced[i] + 0.95f) * 0.5f;
        }
        
        return enhanced;
    }
    
    /**
     * Apply harmonic exciter for added brilliance and presence (double version)
     */
    public static void applyHarmonicExciter(double[] audioData, float sampleRate) {
        double driveAmount = 0.3;
        double mixAmount = 0.2;
        
        // Adjust drive based on sample rate for consistent harmonic generation
        double sampleRateAdjust = 44100.0 / sampleRate;
        driveAmount *= sampleRateAdjust;

        double[] harmonics = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            // Generate harmonics using wave shaping
            double shaped = Math.tanh(audioData[i] * driveAmount);
            harmonics[i] = shaped - audioData[i]; // Extract harmonics

            // Mix harmonics back with dry signal
            audioData[i] = audioData[i] + (harmonics[i] * mixAmount);
        }
    }
} 