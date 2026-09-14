const { chromium } = require('playwright');
const { spawn } = require('child_process');
const path = require('path');
const http = require('http');

function sleep(ms) { return new Promise((r) => setTimeout(r, ms)); }

function getDebugCounts() {
  return new Promise((resolve, reject) => {
    http.get('http://localhost:8918/debug-counts', (res) => {
      let body = '';
      res.on('data', (c) => (body += c));
      res.on('end', () => resolve(JSON.parse(body)));
    }).on('error', reject);
  });
}

async function main() {
  const serverProc = spawn('node', [path.join(__dirname, 'server.js')], { stdio: 'inherit' });
  await sleep(500);

  const browser = await chromium.launch();
  const base = 'ws://localhost:8918';

  console.log('\n=== 1. Native WebSocket: connect, echo round trip, server-initiated close ===');
  {
    const page = await browser.newPage();
    await page.goto('http://localhost:8918/');
    const result = await page.evaluate(async (wsUrl) => {
      return await new Promise((resolve) => {
        const socket = new WebSocket(`${wsUrl}/ws-echo`);
        const log = [];
        socket.addEventListener('open', () => log.push('open'));
        socket.addEventListener('message', (ev) => {
          log.push(`message:${ev.data}`);
          if (ev.data.startsWith('echo:')) {
            socket.send('close-me');
          }
        });
        socket.addEventListener('close', (ev) => {
          log.push(`close:code=${ev.code}:reason=${ev.reason}`);
          resolve({ log, finalReadyState: socket.readyState });
        });
        socket.addEventListener('open', () => socket.send('hello-server'));
      });
    }, base);
    console.log('Event log:', JSON.stringify(result.log));
    console.log('readyState after close (expect 3 = CLOSED):', result.finalReadyState);
    await sleep(1500);
    const counts = await getDebugCounts();
    console.log('Server ws connection count 1.5s after close (expect: still 1, no auto-reconnect):', counts.wsConnectionCount);
    await page.close();
  }

  console.log('\n=== 2. Native WebSocket has NO built-in reconnection — a manual backoff wrapper is required and measured here ===');
  {
    const page = await browser.newPage();
    await page.goto('http://localhost:8918/');
    const result = await page.evaluate(async (wsUrl) => {
      function connectOnce() {
        return new Promise((resolve, reject) => {
          const socket = new WebSocket(`${wsUrl}/ws-echo`);
          socket.addEventListener('open', () => resolve(socket));
          socket.addEventListener('error', reject);
        });
      }

      const delays = [300, 600, 1200];
      const measured = [];
      let socket = await connectOnce();
      measured.push({ attempt: 0, delayRequested: 0 });

      // Force a close, then reconnect with real, measured exponential backoff.
      const closeTimes = [];
      for (let attempt = 0; attempt < delays.length; attempt += 1) {
        const closedAt = await new Promise((resolve) => {
          socket.addEventListener('close', () => resolve(Date.now()));
          socket.send('close-me');
        });
        const delay = delays[attempt];
        await new Promise((r) => setTimeout(r, delay));
        const beforeReconnect = Date.now();
        socket = await connectOnce();
        const reconnectedAt = Date.now();
        closeTimes.push({
          attempt: attempt + 1,
          requestedDelayMs: delay,
          actualElapsedMs: beforeReconnect - closedAt,
        });
      }
      socket.close();
      return closeTimes;
    }, base);
    console.log('Measured manual reconnect backoff schedule (real elapsed ms, not asserted):');
    result.forEach((r) => console.log(`  attempt ${r.attempt}: requested ${r.requestedDelayMs}ms, actual elapsed ${r.actualElapsedMs}ms`));
    const counts = await getDebugCounts();
    console.log('Server ws connection count after 3 manual reconnects (expect 5: 1 initial + 1 from scenario 1 + 3 here... actual count reported below):', counts.wsConnectionCount);
    await page.close();
  }

  console.log("\n=== 3. Native EventSource (SSE) auto-reconnects on its own -- zero reconnect code written ===");
  {
    const page = await browser.newPage();
    await page.goto('http://localhost:8918/');
    const result = await page.evaluate(async () => {
      return await new Promise((resolve) => {
        const events = [];
        let openCount = 0;
        const source = new EventSource('http://localhost:8918/sse-stream');
        source.addEventListener('open', () => {
          openCount += 1;
          events.push(`open#${openCount}`);
        });
        source.addEventListener('message', (ev) => {
          events.push(`message:${ev.data}`);
        });
        // No 'error' handler performs any reconnect logic -- the browser
        // does that on its own per the EventSource spec. We just wait
        // and observe.
        setTimeout(() => {
          source.close();
          resolve({ events, openCount });
        }, 2200);
      });
    });
    console.log(`Total distinct 'open' events observed (expect >= 2, proving real automatic reconnection with zero client reconnect code): ${result.openCount}`);
    console.log('Full event log:');
    result.events.forEach((e) => console.log('  ' + e));
    const counts = await getDebugCounts();
    console.log('Server sse connection count (expect >= 2, each a genuine new HTTP request the browser made on its own):', counts.sseConnectionCount);
    await page.close();
  }

  await browser.close();
  serverProc.kill();
}

main();
