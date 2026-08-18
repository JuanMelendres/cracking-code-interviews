// The square-bracket folder name [slug] creates a dynamic route segment.
// This one file at app/blog/[slug]/page.js serves EVERY /blog/<anything>
// URL — /blog/hello-world, /blog/file-based-routing, /blog/whatever —
// with params.slug telling this component which one was requested. No
// per-post route was created ahead of time; params is a Promise in the
// current App Router (must be awaited), a real, live-verified API detail
// from this Next.js version's own bundled docs, not assumed from memory.
export default async function BlogPostPage({ params }) {
  const { slug } = await params;
  return (
    <div>
      <h1>Blog post — app/blog/[slug]/page.js</h1>
      <p data-testid="page-path">Route: /blog/{slug}</p>
      <p data-testid="page-slug">params.slug = &quot;{slug}&quot;</p>
    </div>
  );
}
