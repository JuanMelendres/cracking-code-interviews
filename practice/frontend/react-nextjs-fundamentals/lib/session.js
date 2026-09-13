import "server-only";
import { SignJWT, jwtVerify } from "jose";
import { cookies } from "next/headers";

// A real, demo-only secret -- a production app reads this from a real
// environment variable, never hardcodes it. Kept here only so this
// chapter's demo has zero setup steps for a reader cloning the repo.
const encodedKey = new TextEncoder().encode("f-211-demo-secret-do-not-use-in-production");

export async function encrypt(payload) {
  return new SignJWT(payload)
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime("1h")
    .sign(encodedKey);
}

// Real signature verification -- a tampered or forged cookie value fails
// here even though a naive `.has('session')` check (this app's original
// F-208 proxy.js check) would still see a cookie present.
export async function decrypt(session) {
  if (!session) return null;
  try {
    const { payload } = await jwtVerify(session, encodedKey, { algorithms: ["HS256"] });
    return payload;
  } catch {
    return null;
  }
}

export async function createSession(userId) {
  const expiresAt = new Date(Date.now() + 60 * 60 * 1000);
  const session = await encrypt({ userId, expiresAt });
  const cookieStore = await cookies();
  cookieStore.set("session", session, {
    httpOnly: true,
    secure: true,
    expires: expiresAt,
    sameSite: "lax",
    path: "/",
  });
}

export async function deleteSession() {
  const cookieStore = await cookies();
  cookieStore.delete("session");
}
