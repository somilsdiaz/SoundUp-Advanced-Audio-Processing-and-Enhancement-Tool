package PDA;

import javax.sound.sampled.AudioFormat;

/**
 * Class for audio filtering operations including parametric EQ, shelf filters, and bandpass filters.
 */
public class FilterProcessor {
    // EQ band frequencies (Hz)
    private static final float[] EQ_BANDS = {60, 150, 400, 1000, 2500, 6000, 12000};
    
    // Add professional audio processing constants
    private static final double WARM_BASS_FREQ = 120.0;
    private static final double PRESENCE_FREQ = 5000.0;
    private static final double AIR_FREQ = 12000.0;
    private static final double CLARITY_FREQ = 2500.0;

    /**
     * Apply multi-band equalization for professional tonal balance
     */
    public static float[] applyMultiBandEQ(float[] samples, AudioFormat format, float bassBoost, float midPresence, float highClarity) {
        int channels = format.getChannels();
        int samplesPerChannel = samples.length / channels;
        
        // Create frequency domain processor for each band
        for (int ch = 0; ch < channels; ch++) {
            // Extract single channel for processing
            float[] channelSamples = new float[samplesPerChannel];
            for (int i = 0; i < samplesPerChannel; i++) {
                channelSamples[i] = samples[i * channels + ch];
            }
            
            // Convert float array to double array for filter methods
            double[] doubleChannelSamples = new double[channelSamples.length];
            for (int i = 0; i < channelSamples.length; i++) {
                doubleChannelSamples[i] = channelSamples[i];
            }
            
            // Apply low-shelf filter for bass (below 150Hz) - reduced gain
            applyLowShelfFilter(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[0], 
                                1.0 + (bassBoost - 1.0) * 0.7);
            
            // Apply parametric EQ for low-mids (250-500Hz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[2], 0.9, 1.2);
            
            // Apply parametric EQ for mids (500-2kHz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[3],
                           1.0 + (midPresence - 1.0) * 0.7, 1.5);
            
            // Apply parametric EQ for high-mids (2k-5kHz) - reduced gain
            applyParametricEQ(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[4],
                           1.0 + (midPresence - 1.0) * 0.6, 1.0);
            
            // Apply high-shelf filter for highs (above 6kHz) - reduced gain
            applyHighShelfFilter(doubleChannelSamples, format.getSampleRate(), EQ_BANDS[5], 
                                1.0 + (highClarity - 1.0) * 0.7);
            
            // Convert back to float array
            for (int i = 0; i < channelSamples.length; i++) {
                channelSamples[i] = (float) doubleChannelSamples[i];
            }
            
            // Copy processed channel back to main samples array
            for (int i = 0; i < samplesPerChannel; i++) {
                samples[i * channels + ch] = channelSamples[i];
            }
        }
        
        return samples;
    }

    /**
     * Apply a low shelf filter to boost or cut low frequencies
     */
    public static void applyLowShelfFilter(double[] audioData, float sampleRate, double cutoffFrequency, double gain) {
        int n = audioData.length;

        // Convert gain from dB to linear scale
        double A = Math.pow(10, gain / 40); // Gain factor

        // Compute normalized angular frequency
        double w0 = 2 * Math.PI * cutoffFrequency / sampleRate;
        double cosw0 = Math.cos(w0);
        double sinw0 = Math.sin(w0);

        // Compute intermediate terms
        double alpha = sinw0 / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        // Compute filter coefficients
        double a0 = (A + 1) + (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha;
        double a1 = -2 * ((A - 1) + (A + 1) * cosw0);
        double a2 = (A + 1) + (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha;

        double b0 = A * ((A + 1) - (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha);
        double b1 = 2 * A * ((A - 1) - (A + 1) * cosw0);
        double b2 = A * ((A + 1) - (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha);

        // Normalize coefficients by a0
        double normA0 = 1 / a0;
        b0 *= normA0;
        b1 *= normA0;
        b2 *= normA0;
        a1 *= normA0;
        a2 *= normA0;

        // Apply the filter
        double[] filteredData = new double[n];
        filteredData[0] = b0 * audioData[0];
        filteredData[1] = b0 * audioData[1] + b1 * audioData[0] - a1 * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = b0 * audioData[i] + b1 * audioData[i - 1] + b2 * audioData[i - 2]
                    - a1 * filteredData[i - 1] - a2 * filteredData[i - 2];
        }

        // Copy the filtered data back to the original array
        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    /**
     * Apply a high shelf filter to boost or cut high frequencies
     */
    public static void applyHighShelfFilter(double[] audioData, float sampleRate, double cutoffFrequency, double gain) {
        int n = audioData.length;
        double A = Math.pow(10, gain / 40);
        double w0 = 2 * Math.PI * cutoffFrequency / sampleRate;
        double cosw0 = Math.cos(w0);
        double alpha = Math.sin(w0) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0 = (A + 1) + (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha;
        double a1 = -2 * ((A - 1) + (A + 1) * cosw0);
        double a2 = (A + 1) + (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha;
        double b0 = A * ((A + 1) - (A - 1) * cosw0 + 2 * Math.sqrt(A) * alpha);
        double b1 = 2 * A * ((A - 1) - (A + 1) * cosw0);
        double b2 = A * ((A + 1) - (A - 1) * cosw0 - 2 * Math.sqrt(A) * alpha);

        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }

    /**
     * Apply a parametric EQ filter to boost or cut a specific frequency band
     */
    public static void applyParametricEQ(double[] audioData, float sampleRate, double centerFreq, double gain, double q) {
        double w0 = 2.0 * Math.PI * centerFreq / sampleRate;
        double alpha = Math.sin(w0) / (2.0 * q);
        double A = Math.pow(10.0, gain / 40.0);

        double b0 = 1.0 + alpha * A;
        double b1 = -2.0 * Math.cos(w0);
        double b2 = 1.0 - alpha * A;
        double a0 = 1.0 + alpha / A;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha / A;

        applyBiquadFilter(audioData, b0, b1, b2, a0, a1, a2);
    }

    /**
     * Apply a band-pass filter to allow only a specific frequency range
     */
    public static void applyBandPassFilter(double[] audioData, float sampleRate, double lowCutoff, double highCutoff, double gain) {
        int n = audioData.length;
        double A = Math.pow(10, gain / 40);

        // Parameters for low-pass filter (highCutoff)
        double w0Low = 2 * Math.PI * lowCutoff / sampleRate;
        double cosw0Low = Math.cos(w0Low);
        double alphaLow = Math.sin(w0Low) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0Low = (A + 1) - (A - 1) * cosw0Low + 2 * Math.sqrt(A) * alphaLow;
        double a1Low = 2 * ((A - 1) - (A + 1) * cosw0Low);
        double a2Low = (A + 1) - (A - 1) * cosw0Low - 2 * Math.sqrt(A) * alphaLow;
        double b0Low = A * ((A + 1) + (A - 1) * cosw0Low + 2 * Math.sqrt(A) * alphaLow);
        double b1Low = -2 * A * ((A - 1) + (A + 1) * cosw0Low);
        double b2Low = A * ((A + 1) + (A - 1) * cosw0Low - 2 * Math.sqrt(A) * alphaLow);

        // Parameters for high-pass filter (lowCutoff)
        double w0High = 2 * Math.PI * highCutoff / sampleRate;
        double cosw0High = Math.cos(w0High);
        double alphaHigh = Math.sin(w0High) / 2 * Math.sqrt((A + 1 / A) * (1 / 0.9 - 1) + 2);

        double a0High = (A + 1) - (A - 1) * cosw0High + 2 * Math.sqrt(A) * alphaHigh;
        double a1High = 2 * ((A - 1) - (A + 1) * cosw0High);
        double a2High = (A + 1) - (A - 1) * cosw0High - 2 * Math.sqrt(A) * alphaHigh;
        double b0High = A * ((A + 1) + (A - 1) * cosw0High + 2 * Math.sqrt(A) * alphaHigh);
        double b1High = -2 * A * ((A - 1) + (A + 1) * cosw0High);
        double b2High = A * ((A + 1) + (A - 1) * cosw0High - 2 * Math.sqrt(A) * alphaHigh);

        double[] filteredData = new double[n];
        filteredData[0] = (b0Low / a0Low) * audioData[0];
        filteredData[1] = (b0Low / a0Low) * audioData[1] + (b1Low / a0Low) * audioData[0] - (a1Low / a0Low) * filteredData[0];

        for (int i = 2; i < n; i++) {
            double lowPassOutput = (b0Low / a0Low) * audioData[i] + (b1Low / a0Low) * audioData[i - 1] + (b2Low / a0Low) * audioData[i - 2]
                    - (a1Low / a0Low) * filteredData[i - 1] - (a2Low / a0Low) * filteredData[i - 2];

            double highPassOutput = (b0High / a0High) * lowPassOutput + (b1High / a0High) * filteredData[i - 1] + (b2High / a0High) * filteredData[i - 2]
                    - (a1High / a0High) * filteredData[i - 1] - (a2High / a0High) * filteredData[i - 2];

            filteredData[i] = highPassOutput;
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }
    
    /**
     * Apply a generic biquad filter with given coefficients
     */
    public static void applyBiquadFilter(double[] audioData, double b0, double b1, double b2, double a0, double a1, double a2) {
        int n = audioData.length;
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        System.arraycopy(filteredData, 0, audioData, 0, n);
    }
    
    /**
     * Split audio into multiple frequency bands
     */
    public static void splitBands(double[] audioData, double[] lowBand, double[] midBand, double[] highBand, 
                          float sampleRate, double lowCrossover, double highCrossover) {
        int n = audioData.length;
        double[] lowPass = new double[n];
        double[] highPass = new double[n];

        // Low-pass filter for low band
        applyLowPassFilter(audioData, lowPass, sampleRate, lowCrossover);

        // High-pass filter for high band
        applyHighPassFilter(audioData, highPass, sampleRate, highCrossover);

        // Mid band is the difference between original and low+high bands
        for (int i = 0; i < n; i++) {
            lowBand[i] = lowPass[i];
            highBand[i] = highPass[i];
            midBand[i] = audioData[i] - lowPass[i] - highPass[i];
        }
    }
    
    /**
     * Apply a low-pass filter
     */
    public static void applyLowPassFilter(double[] audioData, double[] lowPass, float sampleRate, double cutoffFrequency) {
        int n = audioData.length;
        double w0 = 2.0 * Math.PI * cutoffFrequency / sampleRate;
        double alpha = Math.sin(w0) / 2.0;
        double b0 = (1.0 - Math.cos(w0)) / 2.0;
        double b1 = 1.0 - Math.cos(w0);
        double b2 = (1.0 - Math.cos(w0)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha;

        // Create a temporary array for processing
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        if (n > 1) {
            filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];
        }

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        // Copy the filtered data to the lowPass output array
        System.arraycopy(filteredData, 0, lowPass, 0, n);
    }

    /**
     * Apply a high-pass filter
     */
    public static void applyHighPassFilter(double[] audioData, double[] highPass, float sampleRate, double cutoffFrequency) {
        int n = audioData.length;
        double w0 = 2.0 * Math.PI * cutoffFrequency / sampleRate;
        double alpha = Math.sin(w0) / 2.0;
        double b0 = (1.0 + Math.cos(w0)) / 2.0;
        double b1 = -(1.0 + Math.cos(w0));
        double b2 = (1.0 + Math.cos(w0)) / 2.0;
        double a0 = 1.0 + alpha;
        double a1 = -2.0 * Math.cos(w0);
        double a2 = 1.0 - alpha;

        // Create a temporary array for processing
        double[] filteredData = new double[n];
        filteredData[0] = (b0 / a0) * audioData[0];
        if (n > 1) {
            filteredData[1] = (b0 / a0) * audioData[1] + (b1 / a0) * audioData[0] - (a1 / a0) * filteredData[0];
        }

        for (int i = 2; i < n; i++) {
            filteredData[i] = (b0 / a0) * audioData[i] + (b1 / a0) * audioData[i - 1] + (b2 / a0) * audioData[i - 2]
                    - (a1 / a0) * filteredData[i - 1] - (a2 / a0) * filteredData[i - 2];
        }

        // Copy the filtered data to the highPass output array
        System.arraycopy(filteredData, 0, highPass, 0, n);
    }
} 