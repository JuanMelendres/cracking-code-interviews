import { useState } from 'react';

// F-103: useState. Each CALL SITE of <Counter /> gets its own, independent
// state -- state lives with the component INSTANCE (its position in the tree),
// not with the function definition. Rendering three <Counter /> below proves
// this directly: clicking one never affects the others.
function Counter({ label }) {
  const [count, setCount] = useState(0);
  return (
    <div className="counter-row" data-testid={`counter-${label}`}>
      <span>{label}: {count}</span>
      <button type="button" onClick={() => setCount((c) => c + 1)}>+1</button>
      <button type="button" onClick={() => setCount(0)}>reset</button>
    </div>
  );
}

export default function CounterState() {
  return (
    <div className="demo-block">
      <h3>F-103: useState — three independent instances</h3>
      <p>Click +1 on "A" a few times, then check B and C are unaffected.</p>
      <Counter label="A" />
      <Counter label="B" />
      <Counter label="C" />
    </div>
  );
}
