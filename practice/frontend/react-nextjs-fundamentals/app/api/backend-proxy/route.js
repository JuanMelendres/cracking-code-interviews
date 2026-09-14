import { NextResponse } from "next/server";
import { getSession } from "../../../lib/dal";

// The BFF (backend-for-frontend) pattern F-214 names: the BROWSER never
// talks to the separate Spring backend directly, and never sees
// INTERNAL_API_KEY (a server-only env var). This Route Handler is the
// ONLY thing that holds both credentials at once -- the browser's own
// Next.js session (F-211's httpOnly cookie, checked via the DAL) and the
// backend's own shared secret (checked server-to-server, never sent to
// the browser). Where should auth/session logic live when the API is a
// separate service? Here -- not duplicated into the Spring backend, and
// not exposed to the browser to attach itself.
export async function GET() {
  const session = await getSession();
  if (!session?.userId) {
    return NextResponse.json({ error: "Not signed in" }, { status: 401 });
  }

  const backendRes = await fetch("http://localhost:8080/api/internal/secret-data", {
    headers: { "X-Internal-Api-Key": process.env.INTERNAL_API_KEY },
    cache: "no-store",
  });
  const data = await backendRes.json();
  return NextResponse.json(data, { status: backendRes.status });
}
