// No `cache` option at all. Per this Next.js version's own docs
// (node_modules/next/dist/docs/.../caching-without-cache-components.md):
// "By default, fetch requests are not cached." This page exists to
// verify that claim directly rather than assume it: real repeated
// loads should show a DIFFERENT uuid every time (httpbin.org/uuid
// returns a fresh random UUID on every real HTTP call it receives).
async function getUuid() {
  const res = await fetch("https://httpbin.org/uuid");
  return res.json();
}

export default async function DefaultFetchPage() {
  const data = await getUuid();
  return (
    <div>
      <h1>Default fetch() — no cache option</h1>
      <p data-testid="fetch-value">{JSON.stringify(data)}</p>
    </div>
  );
}
