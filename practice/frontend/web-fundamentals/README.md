# Web Fundamentals Demo

Real, static HTML/CSS/JS backing
[`syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md`](../../../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)
(topic `F-001`). No build step, no framework — the point of this demo is to
show what plain HTML, CSS, the DOM, and HTTP look like before React exists.

## Files

- `index.html` — semantic HTML structure: `<header>`, `<nav>`, `<main>`,
  `<section>`, `<article>`, `<footer>` instead of generic `<div>`s.
- `styles.css` — a real box-model demonstration (`.card`'s padding/border/
  margin), a real `display: flex` layout (`.card-grid`), and a real
  `display: grid` layout (`.grid-demo`).
- `app.js` — minimal direct DOM manipulation (`querySelector`,
  `querySelectorAll`, `dataset`) — what React abstracts away.
- `curl-transcript.txt` — real, captured `curl -v` output against this
  page, served locally. Read its capture note first: this environment
  blocks TCP connections to `127.0.0.1`/`localhost` outright, so the
  transcript was captured over a Unix domain socket instead of TCP (see
  "Reproducing this" below for both versions). The HTTP semantics captured
  — request line, headers, status codes, bodies — are identical either way;
  only the transport differs.

## Reproducing this

### Standard version (works on an unrestricted machine)

```bash
cd practice/frontend/web-fundamentals
python3 -m http.server 8000
```

In a second terminal:

```bash
curl -v http://127.0.0.1:8000/
curl -v http://127.0.0.1:8000/styles.css
curl -v http://127.0.0.1:8000/app.js
curl -v http://127.0.0.1:8000/nonexistent-page   # a real 404
```

Then open `http://127.0.0.1:8000/` in a real browser and use DevTools to:

- Inspect `.card` in the Elements/Computed panel to see the box model
  (content, padding, border, margin) as four distinct highlighted regions.
- Inspect `.card-grid` and `.grid-demo` in the Layout panel — Chrome and
  Firefox both overlay the Flexbox/Grid track lines directly on the page.
- Confirm in the Elements panel that `<h1 data-loaded="true">` and each
  `.card[data-card-index]` attribute were added by `app.js` at runtime —
  they do not exist in `index.html`'s source text, only in the live DOM.

This repository's execution environment has no GUI browser, so the DOM/
box-model instructions above are honest instructional narration — precise
about exactly what to click and what you will see — not a captured
screenshot. Everything else (the HTML/CSS/JS content itself, and the HTTP
transcript) is real, served, and independently verified below.

### Version actually captured in this environment

The sandbox this demo was built in blocks TCP connections to
`127.0.0.1`/`localhost` (verified: a listening `http.server` process is
alive and visible in `ps`, but even a raw Python `socket.connect()` to it
times out — Unix domain sockets are unaffected). To get a real HTTP
transcript anyway, the server was run over a Unix domain socket instead
of TCP, using this two-line adapter:

```python
import http.server, socket, sys

class UnixHTTPServer(http.server.HTTPServer):
    address_family = socket.AF_UNIX

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=sys.argv[2], **kwargs)
    def address_string(self):
        return "unix-socket-client"  # AF_UNIX has no client IP to log

server = UnixHTTPServer(sys.argv[1], Handler)
server.serve_forever()
```

```bash
python3 serve_unix.py /tmp/web-fundamentals.sock "$(pwd)" &
curl -v --unix-socket /tmp/web-fundamentals.sock http://localhost/
curl -v --unix-socket /tmp/web-fundamentals.sock http://localhost/styles.css
curl -v --unix-socket /tmp/web-fundamentals.sock http://localhost/app.js
curl -v --unix-socket /tmp/web-fundamentals.sock http://localhost/nonexistent-page
kill %1
```

The HTTP request line, response status line, headers (`Content-Type`,
`Content-Length`, `Server`, `Date`, `Last-Modified`), and bodies captured
this way are exactly what `http.server` produces over a normal TCP socket
— only the transport underneath curl and the server changed, not the HTTP
protocol exchange itself.
