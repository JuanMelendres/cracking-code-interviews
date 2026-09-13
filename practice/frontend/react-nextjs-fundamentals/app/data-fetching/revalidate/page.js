// next.revalidate: 5 — time-based revalidation. Real repeated reloads
// within the same 5-second window should show the SAME uuid; a reload
// after 5+ real seconds have elapsed should show a different one. See
// README.md for the timed real-clock proof.
async function getUuid() {
  const res = await fetch("https://httpbin.org/uuid", {
    next: { revalidate: 5 },
  });
  return res.json();
}

export default async function RevalidateFetchPage() {
  const data = await getUuid();
  return (
    <div>
      <h1>fetch() with next: {"{"} revalidate: 5 {"}"}</h1>
      <p data-testid="fetch-value">{JSON.stringify(data)}</p>
    </div>
  );
}
