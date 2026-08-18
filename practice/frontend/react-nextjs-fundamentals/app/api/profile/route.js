import { NextResponse } from "next/server";
import { getSession } from "../../../lib/dal";
import { unauthorized } from "next/navigation";

// Route Handlers get the SAME real DAL check as Server Components (F-211)
// -- the framework's own docs treat Route Handlers as public-facing API
// endpoints (F-207) needing their own real authorization check.
export async function GET() {
  const session = await getSession();
  if (!session?.userId) {
    unauthorized();
  }
  return NextResponse.json({ userId: session.userId });
}
