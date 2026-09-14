import { verifySession } from "../../lib/dal";
import { logout } from "../actions/auth";

// verifySession() (F-211) redirects to /login if the real, decrypted
// session is missing or invalid -- this is the DAL check the framework's
// own docs recommend doing close to the data, not relying on proxy.js's
// (F-208) optimistic check alone.
export default async function DashboardPage() {
  const session = await verifySession();

  return (
    <div>
      <h1>Dashboard — app/dashboard/page.js</h1>
      <p data-testid="page-path">Route: /dashboard</p>
      <p data-testid="session-user">Signed in as: {session.userId}</p>
      <form action={logout}>
        <button type="submit">Log out</button>
      </form>
    </div>
  );
}
