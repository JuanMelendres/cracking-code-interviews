// Forces dynamic rendering so generateMetadata resolves per-request,
// not once at build time -- the precondition for this version's
// streaming-metadata behavior (and its bot-specific exception) to be
// observable at all. See F-209's real chunk-timing proof.
export const dynamic = "force-dynamic";

// A deliberately slow generateMetadata -- real, artificial delay before
// the title/OG data is ready, so a chunk-timing observer can show
// whether the surrounding page waits for it (bots) or not (everyone else).
export async function generateMetadata({ params }) {
  const { id } = await params;
  await new Promise((resolve) => setTimeout(resolve, 1200));

  return {
    // title.absolute ignores the root layout's title.template entirely --
    // real proof: the rendered <title> is exactly this string, with no
    // " | Next.js Fundamentals Demo" suffix.
    title: { absolute: `Product ${id} (absolute, no template)` },
    // A relative path -- resolves against the root layout's metadataBase
    // into a real, absolute URL in the rendered <head>.
    openGraph: {
      images: [`/og/product-${id}.png`],
    },
  };
}

export default async function ProductPage({ params }) {
  const { id } = await params;
  return (
    <div>
      <h1>Product {id}</h1>
      <p data-testid="product-body">Body content for product {id} — arrives independently of metadata.</p>
    </div>
  );
}
