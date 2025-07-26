hakoru-tuner-two
⚡ Real-time tuner module for React Native (Expo) apps.

Uses native AudioRecord + YIN pitch detection.
Supports calibration (A4=440Hz etc.)
Emits pitch + note + cents info live to JS.
Currently supports Android only.

Installation
npm install hakoru-tuner-two

⚠️ Requires a custom Expo dev client build.
Setup for Expo

Add the module to your project:
npm install hakoru-tuner-two


Add microphone permission to app.json:
{
  "expo": {
    "android": {
      "permissions": ["RECORD_AUDIO"]
    }
  }
}


Create a custom dev client:
eas build --platform android --profile development


Run your app with the dev client:
expo start --dev-client



Permissions
This module requires the RECORD_AUDIO permission. You can request it using expo-av (recommended for Expo SDK 49+) or expo-permissions:
// Using expo-av
import { Audio } from 'expo-av';
const { status } = await Audio.requestPermissionsAsync();

// Using expo-permissions (for older SDK versions)
import * as Permissions from 'expo-permissions';
const { status } = await Permissions.askAsync(Permissions.AUDIO_RECORDING);

Usage
import { Audio } from 'expo-av';
import { start, stop, onPitchDetected } from 'hakoru-tuner-two';

async function startTuning() {
  const { status } = await Audio.requestPermissionsAsync();
  if (status !== 'granted') {
    console.log('Microphone permission denied');
    return;
  }

  const subscription = onPitchDetected(({ freq, note, cents, isInTune }) => {
    console.log(`Frequency: ${freq}Hz, Note: ${note}, Cents: ${cents}, In Tune: ${isInTune}`);
  });

  try {
    await start({ a4: 440 });
  } catch (error) {
    console.error('Failed to start tuning:', error);
  }

  // Later, stop tuning
  // stop();
  // subscription.remove();
}

startTuning();

API

start(options?: { a4: number }): Promise<boolean> - Starts the tuner with optional A4 frequency (default: 440Hz).
stop(): void - Stops the tuner.
onPitchDetected(callback: (data: PitchResult) => void): { remove: () => void } - Subscribes to pitch detection events.

PitchResult
interface PitchResult {
  freq: number; // Detected frequency in Hz
  note: string; // Note name (e.g., "A4")
  cents: number; // Cents offset from the target note
  isInTune: boolean; // Whether the note is in tune (within ±5 cents)
}

Notes

Ensure you have a custom Expo dev client for Android.
Microphone permission is required for the module to work.
iOS support is not yet available.

License
MIT