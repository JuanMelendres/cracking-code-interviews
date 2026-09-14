import { Suspense } from "react";
import { getSession } from "../../lib/dal";
import { unauthorized } from "next/navigation";

// Auth check lives in the data function, not the page body, so the
// page's shell can stream while the session resolves (F-206's pattern,
// applied here to auth specifically -- see this version's own docs).
async function getAccount() {
  const session = await getSession();
  if (!session?.userId) {
    unauthorized();
  }
  return { userId: session.userId };
}

async function AccountDetails() {
  const account = await getAccount();
  return <p data-testid="account-user">Signed in as: {account.userId}</p>;
}

export default function AccountPage() {
  return (
    <main>
      <h1>Account (F-211: unauthorized())</h1>
      <Suspense fallback={<p>Loading...</p>}>
        <AccountDetails />
      </Suspense>
    </main>
  );
}
