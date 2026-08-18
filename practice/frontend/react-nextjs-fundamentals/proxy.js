import { NextResponse } from "next/server";
import { decrypt } from "./lib/session";

// Starting with Next.js 16, "Middleware" is deprecated and renamed to
// "Proxy" -- this file is the direct replacement for what earlier
// versions called middleware.js. It defaults to the Node.js runtime,
// not the Edge runtime (see the F-208 chapter for the real, captured
// proof that setting the Edge runtime here throws a build error).
export async function proxy(request) {
  const { pathname } = request.nextUrl;

  // A real redirect: an old URL forwarded to its current location.
  if (pathname === "/legacy-about") {
    return NextResponse.redirect(new URL("/about", request.url));
  }

  // A real "optimistic check" auth gate (F-211) -- upgraded from F-208's
  // original naive `.has('session')` presence check to a REAL JWT
  // signature verification, using the SAME decrypt() the DAL uses. The
  // framework's own docs are explicit Proxy should stay this fast/coarse
  // (cookie-only, no database round trip) and never be the ONLY
  // authorization layer -- the real check still lives in the DAL
  // (lib/dal.js), close to the data.
  if (pathname.startsWith("/dashboard")) {
    const cookie = request.cookies.get("session")?.value;
    const session = await decrypt(cookie);
    if (!session?.userId) {
      return NextResponse.redirect(new URL("/", request.url));
    }
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
