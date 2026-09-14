// Explicit cache: 'no-store' — the register's own named topic. Should
// behave identically to the default (uncached) page: a different real
// uuid every real reload.
async function getUuid() {
  const res = await fetch("https://httpbin.org/uuid", { cache: "no-store" });
  return res.json();
}

export default async function NoStoreFetchPage() {
  const data = await getUuid();
  return (
    <div>
      <h1>fetch() with cache: &apos;no-store&apos;</h1>
      <p data-testid="fetch-value">{JSON.stringify(data)}</p>
    </div>
  );
}
