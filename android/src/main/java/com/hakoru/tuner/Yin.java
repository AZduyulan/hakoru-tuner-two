package com.hakoru.tuner;

public class Yin {

    public static float detectFrequency(float[] buffer, int sampleRate) {
        int bufferSize = buffer.length;
        float[] difference = new float[bufferSize / 2];
        float[] cumulative = new float[bufferSize / 2];

        for (int tau = 0; tau < bufferSize / 2; tau++) {
            for (int i = 0; i < bufferSize / 2; i++) {
                float delta = buffer[i] - buffer[i + tau];
                difference[tau] += delta * delta;
            }
        }

        cumulative[0] = 1;
        float runningSum = 0;
        for (int tau = 1; tau < bufferSize / 2; tau++) {
            runningSum += difference[tau];
            cumulative[tau] = difference[tau] * tau / runningSum;
        }

        float threshold = 0.1f;
        int tauEstimate = -1;
        for (int tau = 2; tau < bufferSize / 2; tau++) {
            if (cumulative[tau] < threshold) {
                while (tau + 1 < bufferSize / 2 &&
                       cumulative[tau + 1] < cumulative[tau]) {
                    tau++;
                }
                tauEstimate = tau;
                break;
            }
        }

        if (tauEstimate == -1) return -1;

        float betterTau;
        if (tauEstimate > 0 && tauEstimate < bufferSize / 2 - 1) {
            float s0 = cumulative[tauEstimate - 1];
            float s1 = cumulative[tauEstimate];
            float s2 = cumulative[tauEstimate + 1];
            betterTau = tauEstimate + (s2 - s0) / (2 * (2 * s1 - s2 - s0));
        } else {
            betterTau = tauEstimate;
        }

        return sampleRate / betterTau;
    }
}