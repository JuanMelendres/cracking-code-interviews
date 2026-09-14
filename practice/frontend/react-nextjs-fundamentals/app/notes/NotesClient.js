"use client";

import { useActionState, useOptimistic } from "react";
import { addNote, deleteNote } from "./actions";
import SubmitButton from "../components/SubmitButton";

const initialState = { error: null, ok: false };

export default function NotesClient({ notes, userId }) {
  const [state, dispatch, pending] = useActionState(addNote, initialState);

  // Distinct from `pending` above: this list updates INSTANTLY on submit,
  // before the real 800ms artificial delay in the addNote action resolves.
  // React discards the optimistic entry and swaps in the real server list
  // (with its real id) once the action's response actually arrives.
  const [optimisticNotes, addOptimisticNote] = useOptimistic(
    notes,
    (state, newText) => [
      ...state,
      { id: `optimistic-${Date.now()}`, text: newText, authorId: userId, pending: true },
    ]
  );

  async function formAction(formData) {
    const text = formData.get("text");
    if (typeof text === "string" && text.trim() !== "") {
      addOptimisticNote(text.trim());
    }
    dispatch(formData);
  }

  return (
    <div>
      <ul data-testid="notes-list">
        {optimisticNotes.map((note) => (
          <li key={note.id} data-testid="note-item" style={note.pending ? { opacity: 0.5 } : undefined}>
            {note.text}
            {!note.pending && (
              <form action={deleteNote.bind(null, note.id)} style={{ display: "inline" }}>
                <SubmitButton>Delete</SubmitButton>
              </form>
            )}
          </li>
        ))}
      </ul>

      <form action={formAction}>
        <input type="text" name="text" placeholder="New note" required />
        <SubmitButton>Add note</SubmitButton>
      </form>

      {pending && <p data-testid="action-pending">useActionState pending: true</p>}
      {state.error && <p data-testid="action-error">{state.error}</p>}
    </div>
  );
}
