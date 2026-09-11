# Frontend Security: XSS and CSP — Real Demo

Real, executed output backing
[`syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md`](../../../syllabus/21-frontend-web/frontend-security-xss-csrf-and-csp.md)
(F-401). A real Node HTTP server plus a real, headless Chromium browser
(via Playwright) — not a description of XSS/CSP, and not a simulated DOM
(jsdom) that can't actually enforce a browser security policy. Every
claim below is something a real browser engine actually did.

## Run it

```bash
npm install
npx playwright install chromium
npm run start
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Reflected XSS is a real, working attack against naive server-side
  templating, not a theoretical concern.** A payload
  (`<img src=x onerror="window.xssFired=true">`) passed through a
  query parameter and concatenated directly into the response HTML
  with no escaping causes a real Chromium page to set
  `window.xssFired = true` — the browser genuinely parsed the injected
  `<img>` tag, tried to load `x`, failed, and ran the real `onerror`
  handler.
- **HTML-escaping the identical payload before insertion genuinely
  neutralizes it.** The exact same input, escaped
  (`&lt;img src=x ...&gt;`) before being written into the response,
  renders as literal, visible text in a real browser — `window.xssFired`
  stays `undefined`. Same payload, same server, only the escaping
  differs — real, direct, controlled proof of the fix.
- **A real Content-Security-Policy header genuinely blocks inline
  script execution in a real browser — and the identical page without
  it does not.** An inline `<script>` tag with no CSP header runs
  normally (`#marker` text changes). The identical HTML served with
  `Content-Security-Policy: default-src 'self'; script-src 'self'`
  has that same inline script **blocked** — `#marker` stays at its
  initial value, and Chromium's own real console reports the exact
  violation: `Executing inline script violates the following Content
  Security Policy directive 'script-src 'self''... The action has
  been blocked.` This is genuine browser-engine enforcement, not a
  simulated or asserted behavior.
