import { verifySession } from "../../lib/dal";
import { getNotes } from "../../lib/notes-store";
import NotesClient from "./NotesClient";

// A Server Component reads the data directly (no fetch(), no /api/notes
// Route Handler) and a Server Action mutates it directly -- the whole
// CRUD cycle has no separate API layer at all, unlike F-207's widgets demo.
export default async function NotesPage() {
  const session = await verifySession();
  const notes = await getNotes();

  return (
    <main>
      <h1>Notes (F-212: Server Actions)</h1>
      <p data-testid="notes-user">Signed in as: {session.userId}</p>
      <NotesClient notes={notes} userId={session.userId} />
    </main>
  );
}
