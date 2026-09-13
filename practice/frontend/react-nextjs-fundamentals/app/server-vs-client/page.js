import ServerSecretDemo from "../components/ServerSecretDemo";
import ClientCounter from "../components/ClientCounter";

// This page ITSELF is a Server Component by default (no "use client"
// here) — it can freely render both a Server Component
// (ServerSecretDemo) and a Client Component (ClientCounter) as
// children. See README.md for the real captured evidence: the server
// secret never reaches any client JS bundle, but the interactive
// counter genuinely hydrates and works.
export default function ServerVsClientPage() {
  return (
    <div>
      <h1>Server vs. Client Components — app/server-vs-client/page.js</h1>
      <p data-testid="page-path">Route: /server-vs-client</p>
      <ServerSecretDemo />
      <ClientCounter />
    </div>
  );
}
