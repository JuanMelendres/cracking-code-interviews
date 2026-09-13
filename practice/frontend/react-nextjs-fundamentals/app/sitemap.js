// A file-based Route Handler (per this version's own docs) generating
// real sitemap.xml content -- verified with a plain curl, not just
// trusted from the returned array's shape.
export default function sitemap() {
  return [
    { url: "http://localhost:5198/", changeFrequency: "monthly", priority: 1 },
    { url: "http://localhost:5198/about", changeFrequency: "yearly", priority: 0.8 },
    { url: "http://localhost:5198/pricing", changeFrequency: "weekly", priority: 0.5 },
  ];
}
