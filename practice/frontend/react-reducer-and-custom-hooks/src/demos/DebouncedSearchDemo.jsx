import { useState, useRef } from 'react';
import { useDebouncedValue } from '../hooks/useDebouncedValue';

// F-110b: a real, useful custom hook in action. `rawValue` updates on every
// keystroke; `debouncedValue` (from useDebouncedValue, 500ms) only commits
// once typing has PAUSED for 500ms. `debouncedCommitCount` (a ref, counted
// only when the debounced value actually changes) proves a burst of rapid
// keystrokes produces exactly ONE debounced commit, not one per keystroke.
export default function DebouncedSearchDemo() {
  const [rawValue, setRawValue] = useState('');
  const debouncedValue = useDebouncedValue(rawValue, 500);
  const commitCount = useRef(0);
  const lastCommitted = useRef('');

  if (debouncedValue !== lastCommitted.current) {
    commitCount.current += 1;
    lastCommitted.current = debouncedValue;
  }

  return (
    <div className="demo-block">
      <h3>F-110b: a custom hook — useDebouncedValue, extracted and reused</h3>
      <input
        type="text"
        value={rawValue}
        onChange={(e) => setRawValue(e.target.value)}
        placeholder="type quickly..."
        data-testid="search-input"
      />
      <p>Raw value (every keystroke): <span data-testid="raw-value">"{rawValue}"</span></p>
      <p>Debounced value (500ms after typing stops): <span data-testid="debounced-value">"{debouncedValue}"</span></p>
      <p>Debounced commits so far: <span data-testid="commit-count">{commitCount.current}</span></p>
    </div>
  );
}
