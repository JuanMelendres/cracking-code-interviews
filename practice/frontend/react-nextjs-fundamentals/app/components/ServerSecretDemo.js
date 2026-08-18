// A SERVER COMPONENT (no "use client" directive, no hooks). It reads
// process.env directly — something a Client Component genuinely cannot
// do for a non-NEXT_PUBLIC_-prefixed variable, since Client Component
// code is bundled and shipped to the browser. This chapter's README
// documents the real proof: this component's CODE (including this
// literal env-var access) never appears in any client JS bundle, but
// its RENDERED OUTPUT (the secret's value, once read) does appear in
// the actual HTML the server sends — a genuine, easy-to-miss distinction
// between "server-only code" and "server-only output."
export default function ServerSecretDemo() {
  const secret = process.env.SERVER_SECRET_DEMO ?? "not-set";
  return (
    <div data-testid="server-secret-demo">
      <p>
        Server Component reading <code>process.env.SERVER_SECRET_DEMO</code> directly:
      </p>
      <p data-testid="server-secret-value">{secret}</p>
    </div>
  );
}
