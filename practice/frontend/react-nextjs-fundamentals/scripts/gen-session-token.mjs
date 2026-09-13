// Mints a real token with lib/session.js's exact secret/algorithm, so a
// curl-driven tamper test can be run without needing to replicate the
// Server Action POST protocol. Not part of the app itself.
import { SignJWT } from "jose";

const encodedKey = new TextEncoder().encode("f-211-demo-secret-do-not-use-in-production");
const expiresAt = new Date(Date.now() + 60 * 60 * 1000);

const token = await new SignJWT({ userId: "user-42", expiresAt })
  .setProtectedHeader({ alg: "HS256" })
  .setIssuedAt()
  .setExpirationTime("1h")
  .sign(encodedKey);

console.log(token);
