const http = require('http');

function escapeHtml(s) {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
          .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

const server = http.createServer((req, res) => {
  const parsed = new URL(req.url, 'http://localhost');
  const name = parsed.searchParams.get('name') || '';

  if (parsed.pathname === '/vulnerable-reflected') {
    // Real, naive server-side template injection: user input concatenated
    // directly into the response HTML with no escaping at all.
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(`<!DOCTYPE html><html><body>
      <div id="greeting">Hello, ${name}</div>
    </body></html>`);
    return;
  }

  if (parsed.pathname === '/safe-escaped') {
    // Identical input, but HTML-escaped before insertion.
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(`<!DOCTYPE html><html><body>
      <div id="greeting">Hello, ${escapeHtml(name)}</div>
    </body></html>`);
    return;
  }

  if (parsed.pathname === '/no-csp-inline-script') {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end(`<!DOCTYPE html><html><body>
      <div id="marker">not set</div>
      <script>document.getElementById('marker').textContent = 'inline script ran';</script>
    </body></html>`);
    return;
  }

  if (parsed.pathname === '/with-csp-inline-script') {
    res.writeHead(200, {
      'Content-Type': 'text/html',
      'Content-Security-Policy': "default-src 'self'; script-src 'self'"
    });
    res.end(`<!DOCTYPE html><html><body>
      <div id="marker">not set</div>
      <script>document.getElementById('marker').textContent = 'inline script ran';</script>
    </body></html>`);
    return;
  }

  res.writeHead(404);
  res.end('not found');
});

const PORT = 8917;
server.listen(PORT, () => {
  console.log(`listening on ${PORT}`);
});
