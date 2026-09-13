"use client";

import { useState } from "react";

// A CLIENT COMPONENT: the "use client" directive is required here
// specifically because this component uses useState/onClick — real,
// interactive, browser-only concerns a Server Component structurally
// cannot have (Server Components render once, on the server, and never
// re-render in response to browser events at all).
export default function ClientCounter() {
  const [count, setCount] = useState(0);
  return (
    <div data-testid="client-counter">
      <p data-testid="client-counter-value">Client count: {count}</p>
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        +1 (client-side state)
      </button>
    </div>
  );
}
