package PDA;

import javax.sound.sampled.AudioFormat;

/**
 * Class for spatial audio processing including stereo enhancement and width.
 */
public class SpatialProcessor {

    /**
     * Apply spatial enhancement for immersive sound
     * Modified to reduce echo effect
     */
    public static float[] applySpatialEnhancement(float[] samples, AudioFormat format, float spatialDepth) {
        if (format.getChannels() != 2) {
            return samples; // Only process stereo
        }
        
        int sampleRate = (int) format.getSampleRate();
        // Reduced delay time to prevent echo (from 20ms to 8ms)
        int delayLength = (int) (sampleRate * 0.008); 
        float[] delayBuffer = new float[delayLength];
        float[] enhanced = new float[samples.length];
        System.arraycopy(samples, 0, enhanced, 0, samples.length);
        
        float depth = spatialDepth * 0.5f; // Reduced depth to minimize echo
        
        // Apply subtle Haas effect for spatial enhancement
        for (int i = 0; i < samples.length; i += 2) {
            // Apply to right channel with reduced effect
            if (i + 1 < samples.length && i/2 >= delayLength) {
                // Reduced mix level from 0.2f to 0.1f
                enhanced[i + 1] += samples[i] * 0.1f * depth * delayBuffer[i/2 % delayLength];
            }
            
            // Update delay buffer with reduced feedback
            if (i < samples.length) {
                delayBuffer[i/2 % delayLength] = samples[i] * 0.15f; // Reduced from 0.3f
            }
        }
        
        return enhanced;
    }

    /**
     * Enhance stereo field for wider, more immersive sound
     * Modified to prevent phase issues
     */
    public static float[] enhanceStereoField(float[] samples, AudioFormat format, float stereoWidth) {
        if (format.getChannels() != 2) {
            return samples; // Only process stereo
        }
        
        float[] enhanced = new float[samples.length];
        // Reduced width factor to prevent phase issues
        float width = 1.0f + (stereoWidth - 1.0f) * 0.7f;
        
        // Apply mid-side processing for stereo enhancement
        for (int i = 0; i < samples.length; i += 2) {
            float left = samples[i];
            float right = samples[i + 1];
            
            // Convert to mid-side
            float mid = (left + right) * 0.5f;
            float side = (left - right) * 0.5f;
            
            // Enhance side signal with more conservative width
            side *= width;
            
            // Convert back to left-right
            enhanced[i] = mid + side;
            enhanced[i + 1] = mid - side;
            
            // Add correlation check to prevent phase issues
            // If left and right are too out of phase, reduce the effect
            float correlation = left * right;
            if (correlation < 0) {
                // Signals are out of phase, blend back toward original
                float blendFactor = 0.7f;
                enhanced[i] = enhanced[i] * blendFactor + samples[i] * (1-blendFactor);
                enhanced[i+1] = enhanced[i+1] * blendFactor + samples[i+1] * (1-blendFactor);
            }
        }
        
        return enhanced;
    }

    /**
     * Apply stereo widening to a double array (mainly for old double-based code)
     */
    public static void applyStereoWidening(double[] audioData) {
        double widthAmount = 0.3;

        for (int i = 0; i < audioData.length - 1; i += 2) {
            double left = audioData[i];
            double right = audioData[i + 1];

            // Mid-Side processing
            double mid = (left + right) * 0.5;
            double side = (left - right) * 0.5;

            // Enhance stereo width
            side *= (1.0 + widthAmount);

            // Convert back to Left-Right
            audioData[i] = mid + side;
            audioData[i + 1] = mid - side;
        }
    }
} 