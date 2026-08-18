import { NextResponse } from "next/server";

// Starting with Next.js 16, "Middleware" is deprecated and renamed to
// "Proxy" -- this file is the direct replacement for what earlier
// versions called middleware.js. It defaults to the Node.js runtime,
// not the Edge runtime (see the F-208 chapter for the real, captured
// proof that setting the Edge runtime here throws a build error).
export function proxy(request) {
  const { pathname } = request.nextUrl;

  // A real redirect: an old URL forwarded to its current location.
  if (pathname === "/legacy-about") {
    return NextResponse.redirect(new URL("/about", request.url));
  }

  // A real, deliberately simple "optimistic check" auth gate -- the
  // framework's own docs are explicit that Proxy should not be the ONLY
  // authorization layer (no fast, reliable way to hit a database here),
  // but is fine for a fast, coarse redirect before a page even renders.
  if (pathname.startsWith("/dashboard") && !request.cookies.has("session")) {
    return NextResponse.redirect(new URL("/", request.url));
  }

  // A real response header, provable with curl -- proof Proxy code
  // genuinely ran before this response left the server.
  const response = NextResponse.next();
  response.headers.set("x-proxy-hit", "true");
  return response;
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
