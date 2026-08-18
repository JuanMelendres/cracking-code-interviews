"use server";

import { revalidateTag } from "next/cache";

// A real Server Action, invoked from a real Client Component button.
// Proves on-demand revalidation doesn't require waiting for any timer.
export async function revalidateTimeTag() {
  revalidateTag("uuid-tag");
}
