import { useState, useEffect } from 'react';

// F-110b: a real, useful custom hook -- extracts the "wait N ms after the
// last change before committing" pattern (this chapter's F-105 useEffect
// material, reused rather than re-derived) into a single reusable unit.
// Every render that changes `value` schedules a new timeout AND cleans up
// the previous one (the same cleanup discipline covered in
// react-hooks-useeffect-and-useref.md) -- so only the LAST value in a burst
// of rapid changes ever actually gets committed to `debouncedValue`.
export function useDebouncedValue(value, delayMs) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const id = setTimeout(() => setDebouncedValue(value), delayMs);
    return () => clearTimeout(id);
  }, [value, delayMs]);

  return debouncedValue;
}
