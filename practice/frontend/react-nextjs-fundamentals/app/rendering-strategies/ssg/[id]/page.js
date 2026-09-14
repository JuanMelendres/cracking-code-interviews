// generateStaticParams prerenders SPECIFIC param values at BUILD TIME
// (real, listed IDs "1" and "2" below) — those pages become static
// HTML shipped straight from the build, no per-request rendering at
// all. An id NOT in this list (e.g. "999") is still a valid route
// (dynamicParams defaults to true), but is generated on the FIRST
// real request to that specific path, then cached — this is the ISR
// "unknown path" behavior, distinct from the two build-time-known ids.
export async function generateStaticParams() {
  return [{ id: "1" }, { id: "2" }];
}

export default async function SsgProductPage({ params }) {
  const { id } = await params;
  return (
    <div>
      <h1>SSG — generateStaticParams(&quot;1&quot;, &quot;2&quot;)</h1>
      <p data-testid="product-id">Product id: {id}</p>
      <p>
        Build time: <span data-testid="build-timestamp">{new Date().toISOString()}</span>
      </p>
    </div>
  );
}
