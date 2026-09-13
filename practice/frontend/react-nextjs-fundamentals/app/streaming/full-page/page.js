export const dynamic = "force-dynamic";

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// No Suspense boundary written explicitly here — loading.js (sibling
// file) automatically wraps this whole page in one, per this Next.js
// version's docs: "Next.js automatically wraps the page content in a
// <Suspense> boundary, using your loading component as the fallback."
export default async function FullPageStreamingPage() {
  await delay(1500);
  return (
    <div>
      <h1>Streaming — page-level with loading.js</h1>
      <p data-testid="full-page-content">Full page content resolved (1500ms)</p>
    </div>
  );
}
