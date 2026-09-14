import { useState, useRef } from 'react';

// F-112a: reconciliation's central, provable claim -- when an element's
// type AND position (or key) stay the same across renders, React reuses
// the SAME real DOM node rather than destroying and recreating it.
//
// Checked here with plain identity comparison (===), not just visible
// content: `lastNodeRef` remembers the DOM node object captured on the
// PREVIOUS render; on each new render, we compare it against the CURRENT
// value of `inputRef.current`. If they're the same object reference, React
// reused the node. This is real evidence, not an assumption -- the counter
// only increments when a genuine reference match is observed.
export default function DomNodeReuseDemo() {
  const [count, setCount] = useState(0);
  const inputRef = useRef(null);
  const lastNodeRef = useRef(null);
  const sameNodeRenders = useRef(0);
  const totalRenders = useRef(0);

  totalRenders.current += 1;
  if (inputRef.current) {
    if (lastNodeRef.current === inputRef.current) {
      sameNodeRenders.current += 1;
    }
    lastNodeRef.current = inputRef.current;
  }

  return (
    <div className="demo-block">
      <h3>F-112a: same type + position → React reuses the real DOM node</h3>
      <p>Type something in the box, then click "Re-render" a few times. Your text is never lost because the identical `&lt;input&gt;` DOM node object is reused, not recreated.</p>
      <input ref={inputRef} type="text" placeholder="type here, then re-render" data-testid="reuse-input" />
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        Re-render (count: {count})
      </button>
      <p data-testid="identity-stats">
        Total renders: {totalRenders.current}, renders where the DOM node was the SAME reference as the previous render: {sameNodeRenders.current}
      </p>
    </div>
  );
}
