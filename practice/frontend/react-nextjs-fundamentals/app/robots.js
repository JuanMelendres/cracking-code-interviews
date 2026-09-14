// A file-based Route Handler (per this version's own docs) -- disallows
// the F-208 auth-gated section, allows everything else. Real output
// verified with a plain curl against a clean next start server.
export default function robots() {
  return {
    rules: {
      userAgent: "*",
      allow: "/",
      disallow: "/dashboard/",
    },
    sitemap: "http://localhost:5198/sitemap.xml",
  };
}
