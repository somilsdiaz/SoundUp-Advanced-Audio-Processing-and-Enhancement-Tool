package PDA;

import javax.sound.sampled.AudioFormat;

/**
 * Class for audio dynamics processing including compression and limiting.
 */
public class DynamicsProcessor {

    /**
     * Apply dynamic range compression for professional loudness
     */
    public static float[] applyDynamicCompression(float[] samples, AudioFormat format, float dynamicRange) {
        int channels = format.getChannels();
        float threshold = 0.4f;  // Increased threshold for less compression
        float ratio = 1.0f / dynamicRange;  // Compression ratio
        float attackTime = 0.01f;  // Increased from 0.005f for smoother attack
        float releaseTime = 0.15f;  // Increased from 0.050f for smoother release
        // Reduced makeup gain to prevent saturation
        float makeupGain = 1.0f + (1.0f - dynamicRange) * 0.5f;
        
        // Calculate attack and release coefficients
        float attackCoef = (float) Math.exp(-1.0 / (format.getSampleRate() * attackTime));
        float releaseCoef = (float) Math.exp(-1.0 / (format.getSampleRate() * releaseTime));
        
        // Process each channel
        for (int ch = 0; ch < channels; ch++) {
            float envelope = 0;
            
            // Apply compression
            for (int i = ch; i < samples.length; i += channels) {
                // Calculate envelope (peak detection)
                float inputAbs = Math.abs(samples[i]);
                if (inputAbs > envelope) {
                    envelope = attackCoef * envelope + (1 - attackCoef) * inputAbs;
                } else {
                    envelope = releaseCoef * envelope + (1 - releaseCoef) * inputAbs;
                }
                
                // Apply compression if envelope exceeds threshold
                float gain = 1.0f;
                if (envelope > threshold) {
                    float compressionFactor = threshold + (envelope - threshold) / ratio;
                    gain = compressionFactor / envelope;
                }
                
                // Apply gain with makeup
                samples[i] = samples[i] * gain * makeupGain;
                
                // Add hard limiting to prevent clipping
                if (samples[i] > 0.95f) samples[i] = 0.95f;
                if (samples[i] < -0.95f) samples[i] = -0.95f;
            }
        }
        
        return samples;
    }
    
    /**
     * Apply final limiter to prevent any clipping
     */
    public static float[] applyFinalLimiter(float[] samples) {
        float ceiling = 0.95f;  // Maximum allowed amplitude
        
        // Find the maximum peak
        float maxPeak = 0;
        for (float sample : samples) {
            float abs = Math.abs(sample);
            if (abs > maxPeak) {
                maxPeak = abs;
            }
        }
        
        // If we're already below ceiling, no need to limit
        if (maxPeak <= ceiling) {
            return samples;
        }
        
        // Calculate gain reduction needed
        float gainReduction = ceiling / maxPeak;
        
        // Apply smooth limiting
        float[] limited = new float[samples.length];
        for (int i = 0; i < samples.length; i++) {
            limited[i] = samples[i] * gainReduction;
        }
        
        return limited;
    }

    /**
     * Apply multiband compression to separate frequency bands
     */
    public static void applyMultibandCompression(double[] audioData, float sampleRate) {
        // Split into frequency bands
        int n = audioData.length;
        double[] lowBand = new double[n];
        double[] midBand = new double[n];
        double[] highBand = new double[n];
        
        // Crossover frequencies
        double lowCrossover = 200.0;
        double highCrossover = 2000.0;
        
        // Split the audio into frequency bands
        FilterProcessor.splitBands(audioData, lowBand, midBand, highBand, sampleRate, lowCrossover, highCrossover);
        
        // Apply compression to each band with different settings
        double threshold = -24.0;  // dB
        double ratio = 4.0;        // compression ratio
        double attackTime = 0.005; // 5ms
        double releaseTime = 0.050; // 50ms
        
        // Apply different compression settings to each band
        compressBand(lowBand, threshold, ratio * 1.5, attackTime, releaseTime); // More compression on bass
        compressBand(midBand, threshold + 3.0, ratio * 0.8, attackTime, releaseTime); // Less compression on mids
        compressBand(highBand, threshold + 6.0, ratio * 0.6, attackTime, releaseTime * 1.5); // Even less on highs with longer release
        
        // Mix the bands back together
        for (int i = 0; i < n; i++) {
            audioData[i] = lowBand[i] + midBand[i] + highBand[i];
        }
    }
    
    /**
     * Apply compression to a specific frequency band
     */
    public static void compressBand(double[] band, double threshold, double ratio, double attackTime, double releaseTime) {
        double attack = Math.exp(-1.0 / (attackTime * 44100.0));
        double release = Math.exp(-1.0 / (releaseTime * 44100.0));
        double thresholdLinear = Math.pow(10.0, threshold / 20.0);
        double envGain = 1.0; // Envelope follower for smoother gain changes

        for (int i = 0; i < band.length; i++) {
            double absample = Math.abs(band[i]);
            double targetGain;
            
            if (absample > thresholdLinear) {
                double overThreshold = absample - thresholdLinear;
                double compressed = thresholdLinear + overThreshold / ratio;
                targetGain = compressed / absample;
            } else {
                targetGain = 1.0;
            }

            // Apply attack/release envelope
            if (targetGain < envGain) {
                // Gain reduction (attack)
                envGain = attack * envGain + (1.0 - attack) * targetGain;
            } else {
                // Gain recovery (release)
                envGain = release * envGain + (1.0 - release) * targetGain;
            }
            
            band[i] *= envGain;
        }
    }
} 