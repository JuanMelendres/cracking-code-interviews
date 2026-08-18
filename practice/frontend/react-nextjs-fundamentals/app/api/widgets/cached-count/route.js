import { NextResponse } from "next/server";
import { getAll } from "../../../../lib/widgets-store";

// GET Route Handlers are not cached by default -- this line opts THIS
// one in, per this version's documented route config option. The demo's
// real point: this count is computed once, at build time, and frozen --
// runtime POST/DELETE calls against /api/widgets change the live store
// but this endpoint keeps returning the build-time snapshot.
export const dynamic = "force-static";

export async function GET() {
  return NextResponse.json({ count: getAll().length, note: "captured at build time" });
}
