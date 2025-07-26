export interface PitchResult {
  freq: number;
  note: string;
  cents: number;
  isInTune: boolean;
}

export function start(options?: { a4: number }): Promise<boolean>;
export function stop(): void;
export function onPitchDetected(
  callback: (data: PitchResult) => void
): { remove: () => void };