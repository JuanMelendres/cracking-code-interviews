# Service Workers and PWA: Caching Strategies and Offline Support — Real Demo

Backs [`syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md`](../../../syllabus/21-frontend-web/service-workers-and-pwa-caching-strategies.md) (F-404).

Real headless Chromium (Playwright), a real Node HTTP server, a real
registered Service Worker with real Cache Storage — no jsdom, no mocked
`fetch`, no mocked offline state.

## Run it

```bash
npm install
node run.js
```

Real captured output in [`output-transcript.txt`](output-transcript.txt),
re-run twice to confirm reliability — byte-identical both times (fully
deterministic: no timing-dependent assertions, no randomness).

## What it proves

1. **Cache-first strategy** (`/app.css`) — after the service worker installs
   and takes control (`clients.claim()`, no reload needed), 3 repeated
   fetches of `/app.css` produce **zero** new server requests: the real
   server's own request counter stays at 2 (the initial uncontrolled page
   load's request, plus the service worker's own install-time
   `cache.addAll` fetch) before and after.
2. **Network-first strategy** (`/api/data`) — while online, every fetch
   genuinely reaches the real server: the counter increases by exactly 1
   per fetch (0 → 2 across two real fetches).
3. **Real offline mode** (`browserContext.setOffline(true)`, a genuine
   Playwright network cutoff at the browser-context level, not a mocked
   `fetch` failure):
   - The cached `/app.css` still loads successfully, served entirely from
     Cache Storage.
   - `/api/data` (network-first) falls back to the **last real cached
     value** (`value: 2`) rather than failing, because the strategy's
     `.catch()` reaches into the cache.
   - A **never-cached** resource (`/never-cached.txt`) genuinely fails —
     `TypeError: Failed to fetch` — proving offline support here is real
     and selective, not a blanket success.
   - The server's own request counters are provably unchanged during the
     entire offline block, confirming no real network request reached it.
4. **Real recovery** — `setOffline(false)` restores real network access;
   the next `/api/data` fetch immediately reaches the server again
   (counter increases to 3).

## Honest limitations

- This demo runs a single Node HTTP server and a single Chromium page, not
  a real deployed HTTPS origin — Service Workers require a secure context
  (HTTPS or `localhost`) in production; `localhost` here satisfies that
  requirement the same way it would for local development.
- The two strategies shown (cache-first, network-first-with-fallback) are
  the two most common real-world patterns, not an exhaustive list —
  stale-while-revalidate and other variants compose from the same
  `caches`/`fetch` primitives shown here.
