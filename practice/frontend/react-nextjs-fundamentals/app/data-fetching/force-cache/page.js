import RevalidateButton from "../../components/RevalidateButton";

// cache: 'force-cache', tagged for on-demand revalidation. This makes
// the route eligible for static generation, so Next.js actually
// attempts this fetch at BUILD time (a real, live-discovered fact —
// see README.md for the first build attempt, which failed with
// ECONNREFUSED when this page called a same-server API route that
// wasn't running yet during the build). Using a real external endpoint
// avoids that chicken-and-egg problem. Real repeated reloads should
// show the SAME uuid until revalidateTimeTag() (bound to the button
// below) is invoked.
async function getUuid() {
  const res = await fetch("https://httpbin.org/uuid", {
    cache: "force-cache",
    next: { tags: ["uuid-tag"] },
  });
  return res.json();
}

export default async function ForceCacheFetchPage() {
  const data = await getUuid();
  return (
    <div>
      <h1>fetch() with cache: &apos;force-cache&apos; (tagged)</h1>
      <p data-testid="fetch-value">{JSON.stringify(data)}</p>
      <RevalidateButton />
    </div>
  );
}
