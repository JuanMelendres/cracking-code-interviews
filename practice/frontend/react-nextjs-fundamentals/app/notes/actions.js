"use server";

import { revalidatePath } from "next/cache";
import { getSession } from "../../lib/dal";
import { addNoteRecord, deleteNoteRecord } from "../../lib/notes-store";

// Real, artificial delay so the client's optimistic update (NotesClient.js)
// has something genuine to race against, instead of resolving too fast to
// observe.
const ARTIFICIAL_DELAY_MS = 800;
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// This action is reachable by anyone who can POST here directly -- this
// version's own Server Actions docs are explicit that render-time gating
// (only rendering the form on an authenticated page) is NOT a security
// boundary. The check below is what actually protects the mutation; see
// the chapter's "bypass" evidence for what happens with this commented out.
export async function addNote(prevState, formData) {
  const session = await getSession();
  if (!session?.userId) {
    return { error: "You must be signed in to add a note." };
  }

  const text = formData.get("text");
  if (typeof text !== "string" || text.trim() === "") {
    return { error: "Note text cannot be empty." };
  }

  await sleep(ARTIFICIAL_DELAY_MS);
  await addNoteRecord({ text: text.trim(), authorId: session.userId });
  revalidatePath("/notes");
  return { ok: true };
}

// Bound with .bind(null, noteId) from the client -- the id travels as a
// real extra argument, not a form field, per this version's Forms guide.
export async function deleteNote(noteId) {
  const session = await getSession();
  if (!session?.userId) {
    return;
  }
  await deleteNoteRecord(noteId);
  revalidatePath("/notes");
}
