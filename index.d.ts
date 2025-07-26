export interface PitchResult {
  freq: number;
  note: string;
  cents: number;
  isInTune: boolean;
}

export function start(options?: { a4: number }): void;
export function stop(): void;
export function onPitchDetected(
  callback: (data: string) => void
): { remove: () => void };
