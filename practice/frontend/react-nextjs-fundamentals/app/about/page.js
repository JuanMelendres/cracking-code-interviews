// Creating this file at app/about/page.js is the entire implementation
// of the /about route. No <Route path="/about"> anywhere, no router
// import, no registration step — the folder name IS the URL segment.
export default function AboutPage() {
  return (
    <div>
      <h1>About — app/about/page.js</h1>
      <p data-testid="page-path">Route: /about</p>
    </div>
  );
}
