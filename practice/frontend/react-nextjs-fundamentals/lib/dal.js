import "server-only";
import { cache } from "react";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { decrypt } from "./session";

// React's cache() dedupes this across every call within one render pass
// -- multiple components on the same page calling getSession() trigger
// only one real cookie read + JWT verify, not one per call.
export const getSession = cache(async () => {
  const cookie = (await cookies()).get("session")?.value;
  return decrypt(cookie);
});

// For Server Components that should redirect a signed-out visitor.
export async function verifySession() {
  const session = await getSession();
  if (!session?.userId) {
    redirect("/login");
  }
  return session;
}
