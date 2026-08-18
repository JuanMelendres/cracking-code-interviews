// A plain string title -- the root layout's title.template ("%s | Next.js
// Fundamentals Demo") applies to it, since this page doesn't override with
// title.absolute. Real proof: the rendered <title> is "About | Next.js
// Fundamentals Demo", not just "About".
export const metadata = {
  title: "About",
  openGraph: { images: ["/og/about.png"] },
};

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
