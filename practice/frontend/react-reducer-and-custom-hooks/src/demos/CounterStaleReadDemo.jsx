import { useState, useReducer } from 'react';

// F-109b: useReducer beats useState when one update needs to derive a NEW
// piece of state FROM another piece of state changing AT THE SAME TIME.
// A reducer receives the full previous state as an argument and computes
// the next state in one atomic step; two separate useState calls in one
// handler can't do this correctly without care, because each setter's
// updater only sees ITS OWN previous value, not the other state's
// just-requested new value.
//
// The bug below is made deterministic (not timing-dependent on how fast a
// human or a browser-automation tool clicks) by calling the increment logic
// twice inside ONE handler -- both calls share the exact same closure, so
// the staleness is guaranteed on the very first click, every time.

// ---- Buggy: two related useState calls, one derives from a stale read ----
function StateBasedCounter() {
  const [count, setCount] = useState(0);
  const [lastAction, setLastAction] = useState('none yet');

  function incrementOnce() {
    setCount((c) => c + 1);
    // BUG: `count` here is this render's closed-over value -- correct for a
    // single call, but if this function runs twice in the same handler
    // (same closure), the second call still sees the FIRST call's `count`.
    setLastAction(`incremented to ${count + 1}`);
  }

  function doubleIncrement() {
    incrementOnce();
    incrementOnce(); // same closure both times -- this is what exposes the bug
  }

  return (
    <div>
      <strong>useState version (lastAction reads a stale `count`):</strong>
      <div>count: <span data-testid="state-count">{count}</span></div>
      <div>lastAction: <span data-testid="state-last-action">{lastAction}</span></div>
      <button type="button" onClick={doubleIncrement}>Double increment (calls the handler twice in one click)</button>
    </div>
  );
}

// ---- Fixed: useReducer computes both fields from the SAME previous state ----
function counterReducer(state, action) {
  switch (action.type) {
    case 'INCREMENT': {
      const nextCount = state.count + 1;
      return { count: nextCount, lastAction: `incremented to ${nextCount}` };
    }
    default:
      return state;
  }
}

function ReducerBasedCounter() {
  const [state, dispatch] = useReducer(counterReducer, { count: 0, lastAction: 'none yet' });

  function doubleIncrement() {
    dispatch({ type: 'INCREMENT' });
    dispatch({ type: 'INCREMENT' }); // each dispatch gets the LATEST state, no staleness possible
  }

  return (
    <div>
      <strong>useReducer version (each dispatch computes from the latest state):</strong>
      <div>count: <span data-testid="reducer-count">{state.count}</span></div>
      <div>lastAction: <span data-testid="reducer-last-action">{state.lastAction}</span></div>
      <button type="button" onClick={doubleIncrement}>Double increment (dispatches twice in one click)</button>
    </div>
  );
}

export default function CounterStaleReadDemo() {
  return (
    <div className="demo-block">
      <h3>F-109b: deriving one field from another, atomically, under a double update</h3>
      <p>Click "Double increment" once on each. Compare `count` to what `lastAction` claims.</p>
      <StateBasedCounter />
      <ReducerBasedCounter />
    </div>
  );
}
