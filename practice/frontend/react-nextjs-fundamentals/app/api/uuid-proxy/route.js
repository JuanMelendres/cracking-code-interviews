import { NextResponse } from "next/server";

// The Backend-for-Frontend pattern the register calls out: a client never
// sees httpbin's raw shape or URL, only this route's reshaped JSON. In a
// Java-backend-plus-Next.js setup, this exact seam is where a call to the
// real Java service would go instead -- the browser only ever talks to
// this Next.js route, which decides what to expose.
export async function GET() {
  let upstream;
  try {
    upstream = await fetch("https://httpbin.org/uuid", { cache: "no-store" });
  } catch (reason) {
    const message = reason instanceof Error ? reason.message : "Unexpected error";
    return NextResponse.json({ error: `Upstream unreachable: ${message}` }, { status: 502 });
  }

  if (!upstream.ok) {
    return NextResponse.json({ error: `Upstream returned ${upstream.status}` }, { status: 502 });
  }

  const { uuid } = await upstream.json();
  return NextResponse.json({
    correlationId: uuid,
    source: "httpbin.org/uuid",
    reshapedBy: "app/api/uuid-proxy/route.js",
  });
}
