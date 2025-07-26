package com.hakoru.tuner;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class HakoruTunerTwoModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private AudioRecord recorder;
    private Thread thread;
    private boolean isRecording = false;
    private int sampleRate = 44100;
    private int a4 = 440;

    public HakoruTunerTwoModule(ReactApplicationContext context) {
        super(context);
        this.reactContext = context;
    }

    @Override
    public String getName() {
        return "HakoruTunerTwo";
    }

    @ReactMethod
    public void start(int a4Input, Promise promise) {
        if (ContextCompat.checkSelfPermission(reactContext, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            promise.reject("PERMISSION_DENIED", "Microphone permission is required");
            return;
        }

        this.a4 = a4Input;
        int bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        );

        try {
            recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            );

            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                promise.reject("AUDIO_RECORD_ERROR", "Failed to initialize AudioRecord");
                return;
            }

            recorder.startRecording();
            isRecording = true;

            thread = new Thread(() -> {
                short[] buffer = new short[bufferSize];
                while (isRecording) {
                    int read = recorder.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        float[] floatBuffer = new float[read];
                        for (int i = 0; i < read; i++) {
                            floatBuffer[i] = buffer[i] / 32768f;
                        }
                        float freq = Yin.detectFrequency(floatBuffer, sampleRate);
                        if (freq > 20 && freq < 2000) {
                            emitPitch(freq);
                        }
                    } else {
                        // Hata loglama
                    }
                }
            });

            thread.start();
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("AUDIO_RECORD_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void stop() {
        isRecording = false;
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
            } catch (Exception e) {
                // Hata loglama
            }
            recorder = null;
        }
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Hata loglama
            }
            thread = null;
        }
    }

    private void emitPitch(float frequency) {
        double midi = 69 + 12 * Math.log(frequency / a4) / Math.log(2);
        int roundedMidi = (int) Math.round(midi);
        String[] noteNames = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        String note = noteNames[roundedMidi % 12];
        int octave = (roundedMidi / 12) - 1;
        double targetFreq = a4 * Math.pow(2, (roundedMidi - 69) / 12.0);
        double cents = 1200 * Math.log(frequency / targetFreq) / Math.log(2);

        if (calculateRMS(frequency) < 0.005) return;

        String result = String.format("{\"freq\":%.2f,\"note\":\"%s%d\",\"cents\":%.1f,\"isInTune\":%s}",
                frequency, note, octave, cents, Math.abs(cents) < 5 ? "true" : "false");

        new Handler(Looper.getMainLooper()).post(() ->
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onPitchDetected", result)
        );
    }

    private double calculateRMS(double value) {
        return Math.sqrt(value * value);
    }
}