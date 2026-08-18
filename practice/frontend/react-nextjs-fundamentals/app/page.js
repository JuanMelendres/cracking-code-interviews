// This file alone — no router library, no config, no route registration —
// is what makes "/" a real route. The App Router maps app/page.js to "/"
// purely from its location in the file tree.
export default function HomePage() {
  return (
    <div>
      <h1>Home — app/page.js</h1>
      <p data-testid="page-path">Route: /</p>
      <p>
        This route exists because this exact file is at <code>app/page.js</code>.
        No router config anywhere registers it.
      </p>
    </div>
  );
}
