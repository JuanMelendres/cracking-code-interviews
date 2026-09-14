import Link from "next/link";

export default function Unauthorized() {
  return (
    <main>
      <h1>401 - Unauthorized</h1>
      <p>
        Please <Link href="/login">sign in</Link> to view your account.
      </p>
    </main>
  );
}
