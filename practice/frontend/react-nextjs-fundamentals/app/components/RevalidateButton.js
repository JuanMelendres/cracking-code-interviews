"use client";

import { revalidateTimeTag } from "../actions";

// A real Client Component invoking a real Server Action. Clicking this
// calls revalidateTag('uuid-tag') on the server; the effect is only
// observable on the NEXT real navigation/reload of a page that fetches
// with that tag — this button itself doesn't re-render new data.
export default function RevalidateButton() {
  return (
    <button type="button" onClick={() => revalidateTimeTag()}>
      Revalidate &apos;uuid-tag&apos; on-demand
    </button>
  );
}
