import { Suspense } from "react";

// force-dynamic so this page is genuinely rendered (and streamed) fresh
// per request, not prerendered once at build time — the artificial
// delays below need to happen at REQUEST time for this demo to prove
// anything about streaming.
export const dynamic = "force-dynamic";

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function FastWidget() {
  await delay(300);
  return <p data-testid="fast-widget">Fast widget resolved (300ms)</p>;
}

async function MediumWidget() {
  await delay(1200);
  return <p data-testid="medium-widget">Medium widget resolved (1200ms)</p>;
}

async function SlowWidget() {
  await delay(2500);
  return <p data-testid="slow-widget">Slow widget resolved (2500ms)</p>;
}

// Three SIBLING Suspense boundaries, each with a genuinely different
// artificial delay. Per this Next.js version's own streaming docs,
// each boundary is an independent streaming point — they should
// resolve (and stream into the page) in whatever order their own
// async work finishes, not blocking each other. README.md documents
// a real chunk-timing observer script proving this directly, not just
// the total page load time.
export default function SiblingBoundariesPage() {
  return (
    <div>
      <h1>Streaming — sibling Suspense boundaries</h1>
      <Suspense fallback={<p data-testid="fast-fallback">Loading fast widget...</p>}>
        <FastWidget />
      </Suspense>
      <Suspense fallback={<p data-testid="medium-fallback">Loading medium widget...</p>}>
        <MediumWidget />
      </Suspense>
      <Suspense fallback={<p data-testid="slow-fallback">Loading slow widget...</p>}>
        <SlowWidget />
      </Suspense>
    </div>
  );
}
