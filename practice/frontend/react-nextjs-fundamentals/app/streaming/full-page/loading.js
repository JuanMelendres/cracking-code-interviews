// This file's mere presence wraps app/streaming/full-page/page.js in a
// <Suspense> boundary automatically — no explicit Suspense import
// needed in the page itself.
export default function Loading() {
  return <p data-testid="full-page-loading">Loading full page...</p>;
}
