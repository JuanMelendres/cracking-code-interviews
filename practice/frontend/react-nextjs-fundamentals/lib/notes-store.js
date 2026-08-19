import "server-only";
import { readFile, writeFile, mkdir } from "node:fs/promises";
import path from "node:path";

// A real, disk-backed JSON file, not module-level memory -- unlike F-207's
// widgets-store, this survives a server RESTART (next start -> stop -> next
// start again), which the no-JS/CSRF curl tests below rely on running
// against a freshly started server with no prior in-memory state.
const DATA_DIR = path.join(process.cwd(), "data");
const DATA_FILE = path.join(DATA_DIR, "notes.json");

const SEED = [
  { id: "1", text: "First note (seeded)", authorId: "user-42" },
  { id: "2", text: "Second note (seeded)", authorId: "user-42" },
];

async function ensureFile() {
  await mkdir(DATA_DIR, { recursive: true });
  try {
    await readFile(DATA_FILE, "utf8");
  } catch {
    await writeFile(DATA_FILE, JSON.stringify(SEED, null, 2));
  }
}

export async function getNotes() {
  await ensureFile();
  const raw = await readFile(DATA_FILE, "utf8");
  return JSON.parse(raw);
}

export async function addNoteRecord({ text, authorId }) {
  const notes = await getNotes();
  const note = { id: String(Date.now()), text, authorId };
  notes.push(note);
  await writeFile(DATA_FILE, JSON.stringify(notes, null, 2));
  return note;
}

export async function deleteNoteRecord(id) {
  const notes = await getNotes();
  const next = notes.filter((n) => n.id !== id);
  await writeFile(DATA_FILE, JSON.stringify(next, null, 2));
}
