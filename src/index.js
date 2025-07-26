import { NativeModules, NativeEventEmitter } from 'react-native';

const { HakoruTunerTwo } = NativeModules;
const emitter = new NativeEventEmitter(HakoruTunerTwo);

export function start(options = { a4: 440 }) {
  HakoruTunerTwo.start(options.a4);
}

export function stop() {
  HakoruTunerTwo.stop();
}

export function onPitchDetected(callback) {
  const sub = emitter.addListener("onPitchDetected", callback);
  return {
    remove: () => sub.remove()
  };
}
