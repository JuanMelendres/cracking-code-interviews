"use client";

import Link from "next/link";
import { useEffect, useRef, useState } from "react";

// Lives in the root layout, which wraps every route. The App Router's
// stated guarantee is that layouts "preserve state, remain interactive,
// and do not rerender" on navigation between sibling pages — this
// component makes that guarantee measurable: mountCount only increments
// on a genuine mount (StrictMode's double-invoke aside), not on every
// client-side navigation between pages that share this layout.
export default function PersistentHeader() {
  const mountCount = useRef(0);
  const [, forceRender] = useState(0);

  useEffect(() => {
    mountCount.current += 1;
    forceRender((n) => n + 1);
  }, []);

  return (
    <header>
      <p data-testid="layout-mount-count">Layout mount count: {mountCount.current}</p>
      <nav>
        <Link href="/">Home</Link> | <Link href="/about">About</Link> |{" "}
        <Link href="/blog/hello-world">Blog: hello-world</Link> |{" "}
        <Link href="/blog/file-based-routing">Blog: file-based-routing</Link>
      </nav>
    </header>
  );
}
