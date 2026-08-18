// Reads a route's raw HTTP response as a stream and logs each chunk's
// arrival time — the real verification method this Next.js version's
// own streaming docs recommend over curl (curl has its own buffering).
// Usage: node scripts/stream-observer.mjs [url] [User-Agent]
const url = process.argv[2] || "http://localhost:5198/streaming/sibling-boundaries";
const userAgent = process.argv[3] || "";

const headers = { "Accept-Encoding": "identity" };
if (userAgent) headers["User-Agent"] = userAgent;

const start = Date.now();
const res = await fetch(url, { headers });
console.log(`fetch() returned headers at +${Date.now() - start}ms`);

const reader = res.body.getReader();
const decoder = new TextDecoder();
let i = 0;
while (true) {
  const { done, value } = await reader.read();
  if (done) break;
  const t = Date.now() - start;
  const text = decoder.decode(value);
  const markers = [...text.matchAll(/data-testid=\\?"([a-z-]+)\\?"/g)].map((m) => m[1]);
  console.log(`chunk ${i++} (+${t}ms) bytes=${value.length} markers=${JSON.stringify(markers)}`);
}
console.log(`stream done at +${Date.now() - start}ms`);
