import { useRef } from 'react';
import { useAppStore } from '../store/zustandStore';

function CounterView() {
  const count = useAppStore((state) => state.count);
  const increment = useAppStore((state) => state.increment);
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="zustand-counter">
      Count: {count} (renders: {renderCount.current})
      <button type="button" onClick={increment}>
        +1 count
      </button>
    </p>
  );
}

// Selects only `name` — same selective-re-render guarantee as Redux's
// NameView, but this entire file (store + both components) is a third
// of ReduxDemo.jsx + reduxStore.js's combined line count.
function NameView() {
  const name = useAppStore((state) => state.name);
  const setName = useAppStore((state) => state.setName);
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="zustand-name">
      Name: {name} (renders: {renderCount.current})
      <button type="button" onClick={() => setName('ada')}>
        set name
      </button>
    </p>
  );
}

export default function ZustandDemo() {
  return (
    <>
      <CounterView />
      <NameView />
    </>
  );
}
