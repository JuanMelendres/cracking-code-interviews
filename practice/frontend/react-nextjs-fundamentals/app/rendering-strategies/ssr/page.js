import { headers } from "next/headers";

// headers() is a Request-time API: its return value cannot be known
// ahead of time, so using it genuinely opts this route into dynamic
// rendering (SSR) — this page is re-rendered fresh on the server for
// EVERY real request, not once at build time. Rendering the raw
// User-Agent header directly proves it: two requests with different
// User-Agent strings should render two different values.
export default async function SsrPage() {
  const headersList = await headers();
  const userAgent = headersList.get("user-agent") ?? "unknown";
  return (
    <div>
      <h1>SSR — reads headers() on every request</h1>
      <p data-testid="rendered-user-agent">{userAgent}</p>
    </div>
  );
}
