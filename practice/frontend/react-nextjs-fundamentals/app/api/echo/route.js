import { NextResponse } from "next/server";

// Demonstrates NextRequest's nextUrl convenience over raw Request, and
// that request headers arriving at a Route Handler are real, inspectable
// server-side data -- not something a Client Component could read for a
// cross-origin request.
export async function GET(request) {
  const name = request.nextUrl.searchParams.get("name") ?? "anonymous";
  return NextResponse.json({
    greeting: `Hello, ${name}`,
    pathname: request.nextUrl.pathname,
    userAgent: request.headers.get("user-agent"),
  });
}
