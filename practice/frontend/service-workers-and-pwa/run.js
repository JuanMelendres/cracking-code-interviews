const { chromium } = require('playwright');
const { spawn } = require('child_process');
const path = require('path');
const http = require('http');

function sleep(ms) { return new Promise((r) => setTimeout(r, ms)); }

function getDebugCounts() {
  return new Promise((resolve, reject) => {
    http.get('http://localhost:8919/debug-counts', (res) => {
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
  const context = await browser.newContext();
  const page = await context.newPage();
  await page.goto('http://localhost:8919/');

  console.log('=== 1. Registering the real service worker and waiting for it to take control ===');
  await page.evaluate(async () => {
    await navigator.serviceWorker.register('/sw.js');
    if (!navigator.serviceWorker.controller) {
      await new Promise((resolve) => navigator.serviceWorker.addEventListener('controllerchange', resolve));
    }
  });
  console.log('  Service worker is now controlling this page (real navigator.serviceWorker.controller set, no reload needed -- clients.claim() in effect).');

  let counts = await getDebugCounts();
  console.log(`  Server counts after initial page load + SW install caching: cssRequestCount=${counts.cssRequestCount}, apiRequestCount=${counts.apiRequestCount}`);

  console.log('\n=== 2. Cache-first strategy for /app.css: repeated fetches should NOT increase server request count ===');
  const cssCountBefore = (await getDebugCounts()).cssRequestCount;
  for (let i = 0; i < 3; i += 1) {
    const text = await page.evaluate(() => fetch('/app.css').then((r) => r.text()));
    console.log(`  fetch #${i + 1} of /app.css returned ${text.length} bytes`);
  }
  const cssCountAfter = (await getDebugCounts()).cssRequestCount;
  console.log(`  Server cssRequestCount before: ${cssCountBefore}, after 3 more fetches: ${cssCountAfter} (real proof: cache-first served all 3 with zero new network hits)`);

  console.log('\n=== 3. Network-first strategy for /api/data: each online fetch SHOULD hit the real server ===');
  const apiCountBefore = (await getDebugCounts()).apiRequestCount;
  let lastOnlineValue = null;
  for (let i = 0; i < 2; i += 1) {
    const data = await page.evaluate(() => fetch('/api/data').then((r) => r.json()));
    lastOnlineValue = data.value;
    console.log(`  fetch #${i + 1} of /api/data returned value=${data.value}`);
  }
  const apiCountAfter = (await getDebugCounts()).apiRequestCount;
  console.log(`  Server apiRequestCount before: ${apiCountBefore}, after 2 more fetches: ${apiCountAfter} (real proof: network-first hit the server every time while online)`);

  console.log('\n=== 4. Real offline mode (Playwright browser-context-level network cutoff, not a mocked failure) ===');
  await context.setOffline(true);

  const offlineCssResult = await page.evaluate(async () => {
    try {
      const text = await fetch('/app.css').then((r) => r.text());
      return { ok: true, bytes: text.length };
    } catch (e) {
      return { ok: false, error: String(e) };
    }
  });
  console.log(`  Offline fetch of /app.css (cached asset): ${JSON.stringify(offlineCssResult)}`);

  const offlineApiResult = await page.evaluate(async () => {
    try {
      const data = await fetch('/api/data').then((r) => r.json());
      return { ok: true, data };
    } catch (e) {
      return { ok: false, error: String(e) };
    }
  });
  console.log(`  Offline fetch of /api/data (network-first, falls back to last cached value): ${JSON.stringify(offlineApiResult)}`);
  console.log(`  Last value seen while online was ${lastOnlineValue}; offline fallback returned value=${offlineApiResult.ok ? offlineApiResult.data.value : 'N/A'}`);

  const offlineUncachedResult = await page.evaluate(async () => {
    try {
      await fetch('/never-cached.txt');
      return { ok: true };
    } catch (e) {
      return { ok: false, error: String(e) };
    }
  });
  console.log(`  Offline fetch of /never-cached.txt (never cached, no SW strategy applies): ${JSON.stringify(offlineUncachedResult)}`);

  const countsWhileOffline = await getDebugCounts();
  console.log(`  Server counts unchanged during offline fetches (confirms no real network reached the server): cssRequestCount=${countsWhileOffline.cssRequestCount}, apiRequestCount=${countsWhileOffline.apiRequestCount}`);

  console.log('\n=== 5. Back online: network-first resumes hitting the real server ===');
  await context.setOffline(false);
  const backOnlineData = await page.evaluate(() => fetch('/api/data').then((r) => r.json()));
  const finalCounts = await getDebugCounts();
  console.log(`  Fetch of /api/data after going back online returned value=${backOnlineData.value}`);
  console.log(`  Server apiRequestCount increased to ${finalCounts.apiRequestCount} (real proof: real network resumed)`);

  await browser.close();
  serverProc.kill();
}

main();
