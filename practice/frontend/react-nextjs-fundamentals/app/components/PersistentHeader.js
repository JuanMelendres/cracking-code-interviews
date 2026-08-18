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
        <Link href="/blog/file-based-routing">Blog: file-based-routing</Link> |{" "}
        <Link href="/dashboard">Dashboard</Link> |{" "}
        <Link href="/dashboard/settings">Dashboard settings</Link> |{" "}
        <Link href="/pricing">Pricing (route group)</Link> |{" "}
        <Link href="/server-vs-client">Server vs. Client</Link> |{" "}
        <Link href="/data-fetching/default">Fetch: default</Link> |{" "}
        <Link href="/data-fetching/no-store">Fetch: no-store</Link> |{" "}
        <Link href="/data-fetching/force-cache">Fetch: force-cache</Link> |{" "}
        <Link href="/data-fetching/revalidate">Fetch: revalidate(5)</Link> |{" "}
        <Link href="/rendering-strategies/ssr">SSR (headers)</Link> |{" "}
        <Link href="/rendering-strategies/ssg/1">SSG (id=1)</Link> |{" "}
        <Link href="/rendering-strategies/ssg/999">SSG (id=999, unknown)</Link> |{" "}
        <Link href="/streaming/sibling-boundaries">Streaming: sibling boundaries</Link> |{" "}
        <Link href="/streaming/full-page">Streaming: full-page loading.js</Link> |{" "}
        <Link href="/api-demo">Route Handlers (API demo)</Link> |{" "}
        <Link href="/legacy-about">Legacy About (Proxy redirect)</Link> |{" "}
        <Link href="/products/1">Product 1 (generateMetadata)</Link> |{" "}
        <Link href="/media-optimization">Media Optimization (Image/Font)</Link>
      </nav>
    </header>
  );
}
