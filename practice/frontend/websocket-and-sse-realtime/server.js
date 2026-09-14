const http = require('http');
const { WebSocketServer } = require('ws');

let wsConnectionCount = 0;
let sseConnectionCount = 0;

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');

  if (url.pathname === '/sse-stream') {
    sseConnectionCount += 1;
    const thisConnectionId = sseConnectionCount;
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      Connection: 'keep-alive',
    });
    // Override the browser's default 3000ms reconnection delay via the
    // spec-defined `retry:` field, so this demo doesn't need to wait 3s
    // per reconnect cycle. This is a real EventSource wire-format field,
    // not a made-up header.
    res.write('retry: 300\n\n');

    let tick = 0;
    const interval = setInterval(() => {
      tick += 1;
      res.write(`id: ${thisConnectionId}-${tick}\n`);
      res.write(`data: connection ${thisConnectionId}, tick ${tick}\n\n`);
      if (tick >= 3) {
        clearInterval(interval);
        // Deliberately end the response — simulates a dropped connection
        // (proxy timeout, server restart, network blip). We do NOT send
        // any client-side reconnect code; the browser's EventSource does
        // this on its own per the WHATWG HTML spec's reconnection algorithm.
        res.end();
      }
    }, 200);

    req.on('close', () => clearInterval(interval));
    return;
  }

  if (url.pathname === '/') {
    res.writeHead(200, { 'Content-Type': 'text/html' });
    res.end('<!DOCTYPE html><html><body>ok</body></html>');
    return;
  }

  if (url.pathname === '/debug-counts') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ wsConnectionCount, sseConnectionCount }));
    return;
  }

  res.writeHead(404);
  res.end('not found');
});

const wss = new WebSocketServer({ server, path: '/ws-echo' });

wss.on('connection', (socket) => {
  wsConnectionCount += 1;
  const thisConnectionId = wsConnectionCount;

  socket.on('message', (raw) => {
    const text = raw.toString();
    if (text === 'close-me') {
      // Deliberate server-initiated close with a real application close
      // code/reason, so the client's onclose handler has something real
      // to inspect. A native browser WebSocket does NOT reconnect after
      // this on its own — that is the exact behavior this demo proves.
      socket.close(4001, 'demo-server-initiated-close');
      return;
    }
    socket.send(`echo:${thisConnectionId}:${text}`);
  });
});

const PORT = 8918;
server.listen(PORT, () => {
  console.log(`listening on ${PORT}`);
});

module.exports = { getWsConnectionCount: () => wsConnectionCount, getSseConnectionCount: () => sseConnectionCount };
