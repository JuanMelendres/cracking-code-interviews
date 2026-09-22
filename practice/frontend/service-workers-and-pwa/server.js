const http = require('http');
const fs = require('fs');
const path = require('path');

let cssRequestCount = 0;
let apiRequestCount = 0;
let apiValue = 0;

const PUBLIC_DIR = path.join(__dirname, 'public');

const STATIC_FILES = {
  '/': { file: 'index.html', type: 'text/html' },
  '/index.html': { file: 'index.html', type: 'text/html' },
  '/sw.js': { file: 'sw.js', type: 'application/javascript' },
};

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');

  if (url.pathname === '/app.css') {
    cssRequestCount += 1;
    const content = fs.readFileSync(path.join(PUBLIC_DIR, 'app.css'));
    res.writeHead(200, { 'Content-Type': 'text/css' });
    res.end(content);
    return;
  }

  if (url.pathname === '/api/data') {
    apiRequestCount += 1;
    apiValue += 1;
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ value: apiValue, servedByRequestNumber: apiRequestCount }));
    return;
  }

  if (url.pathname === '/debug-counts') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ cssRequestCount, apiRequestCount, apiValue }));
    return;
  }

  const staticEntry = STATIC_FILES[url.pathname];
  if (staticEntry) {
    const content = fs.readFileSync(path.join(PUBLIC_DIR, staticEntry.file));
    res.writeHead(200, { 'Content-Type': staticEntry.type });
    res.end(content);
    return;
  }

  res.writeHead(404);
  res.end('not found');
});

server.listen(8919, () => console.log('server listening on 8919'));
