"use client";

import { useFormStatus } from "react-dom";

// useFormStatus only works inside a <form> it's nested under -- it reads
// the status of the nearest ancestor form via context, not a prop. This is
// a SEPARATE pending signal from useActionState's own `pending` return
// value; NotesClient.js uses both side by side for a direct comparison.
export default function SubmitButton({ children }) {
  const { pending } = useFormStatus();
  return (
    <button type="submit" disabled={pending}>
      {pending ? "Working..." : children}
    </button>
  );
}
