"use client";

import { useEffect, useRef, useState } from "react";

// Generic version of PersistentHeader's counter, reused by every
// nested layout added for F-202 so each layout LEVEL (root, dashboard,
// marketing route group) can be measured independently for the same
// "does this layout remount on navigation" question.
export default function MountCounter({ label, testId }) {
  const mountCount = useRef(0);
  const [, forceRender] = useState(0);

  useEffect(() => {
    mountCount.current += 1;
    forceRender((n) => n + 1);
  }, []);

  return (
    <p data-testid={testId}>
      {label} mount count: {mountCount.current}
    </p>
  );
}
