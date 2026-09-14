import { useState, useRef } from 'react';

// F-106b: a ref holds a mutable value that survives across renders WITHOUT
// causing a re-render when it changes -- unlike state.
//
// `renderCountRef` is incremented directly in the render body (every call to
// this function IS one render) -- this is a safe, well-known pattern for
// counting renders. An earlier version of this demo tried counting renders
// via a dependency-less `useEffect` that called `setState` on every run;
// that setState triggered another render, which re-ran the effect, which
// called setState again -- a real, reproduced "Maximum update depth
// exceeded" infinite loop, caught live in the browser console while
// building this demo. Mutating a ref during render avoids that entirely,
// since it never schedules a new render itself.

export default function RefMutableValueDemo() {
  const clicksRef = useRef(0);
  const renderCountRef = useRef(0);
  renderCountRef.current += 1;

  const [displayedClicks, setDisplayedClicks] = useState(0);

  return (
    <div className="demo-block">
      <h3>F-106b: mutating a ref does NOT trigger a re-render</h3>
      <p>Renders so far: <strong data-testid="render-count">{renderCountRef.current}</strong></p>
      <button
        type="button"
        onClick={() => {
          clicksRef.current += 1; // mutating a ref -- NO re-render happens here
          console.log('ref is now', clicksRef.current, '(no re-render triggered)');
        }}
      >
        Increment ref (click me several times, watch "Renders so far" NOT change)
      </button>
      <button
        type="button"
        onClick={() => setDisplayedClicks(clicksRef.current)} // THIS causes a render
      >
        Reveal ref's real current value (this DOES trigger a render)
      </button>
      <p data-testid="revealed-value">Last revealed ref value: {displayedClicks}</p>
    </div>
  );
}
