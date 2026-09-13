// F-302: `bg-fuchsia-700` is a real, deliberately unusual utility class,
// actually applied here, used specifically so it's easy to grep for in
// the real, built production CSS. A genuinely never-mentioned utility
// (never appears as a className, a comment, or any string anywhere in
// this app's source -- see the chapter's own README for the real,
// unexpected finding about what happens when a class name DOES appear
// as plain text, even unapplied) should never be GENERATED at all.
export default function TailwindDemo() {
  return (
    <div className="border-2 border-fuchsia-700 p-2 rounded-md">
      <h2 className="text-fuchsia-700 font-bold text-xl">Tailwind CSS</h2>
      <span className="bg-fuchsia-700 text-white px-2 py-1 rounded">bg-fuchsia-700 (used)</span>
      <p className="text-sm text-slate-600">See this app's README for the real purge test.</p>
    </div>
  );
}
