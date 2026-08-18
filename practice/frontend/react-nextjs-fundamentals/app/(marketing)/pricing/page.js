// This file lives at app/(marketing)/pricing/page.js on disk, but the
// (marketing) segment is a route group — it is stripped from the URL.
// The real route is /pricing, NOT /marketing/pricing.
export default function PricingPage() {
  return (
    <div>
      <h1>Pricing — app/(marketing)/pricing/page.js</h1>
      <p data-testid="page-path">Route: /pricing (NOT /marketing/pricing)</p>
    </div>
  );
}
