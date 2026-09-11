const { chromium } = require('playwright');
const { spawn } = require('child_process');
const path = require('path');

function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

async function main() {
  const serverProc = spawn('node', [path.join(__dirname, 'server.js')], { stdio: 'inherit' });
  await sleep(500);

  const browser = await chromium.launch();
  const base = 'http://localhost:8917';
  const payload = encodeURIComponent('<img src=x onerror="window.xssFired=true">');

  console.log('\n=== 1. Reflected XSS: server concatenates user input into HTML with no escaping ===');
  {
    const page = await browser.newPage();
    await page.goto(`${base}/vulnerable-reflected?name=${payload}`);
    const fired = await page.evaluate(() => window.xssFired === true);
    const html = await page.locator('#greeting').innerHTML();
    console.log('window.xssFired === true ?', fired);
    console.log('rendered #greeting innerHTML:', html);
    await page.close();
  }

  console.log('\n=== 2. Identical payload, but HTML-escaped by the server before insertion ===');
  {
    const page = await browser.newPage();
    await page.goto(`${base}/safe-escaped?name=${payload}`);
    const fired = await page.evaluate(() => window.xssFired === true);
    const text = await page.locator('#greeting').textContent();
    console.log('window.xssFired === true ?', fired);
    console.log('rendered #greeting text (payload shown literally, not executed):', text);
    await page.close();
  }

  console.log('\n=== 3. Inline <script> with NO Content-Security-Policy header ===');
  {
    const page = await browser.newPage();
    await page.goto(`${base}/no-csp-inline-script`);
    await sleep(100);
    const marker = await page.locator('#marker').textContent();
    console.log('#marker text:', marker);
    await page.close();
  }

  console.log('\n=== 4. Identical inline <script>, but WITH a real CSP header (script-src \'self\') ===');
  {
    const page = await browser.newPage();
    const cspViolations = [];
    page.on('console', msg => {
      if (msg.text().toLowerCase().includes('content security policy')) {
        cspViolations.push(msg.text());
      }
    });
    const response = await page.goto(`${base}/with-csp-inline-script`);
    await sleep(100);
    const marker = await page.locator('#marker').textContent();
    console.log('Response CSP header:', response.headers()['content-security-policy']);
    console.log('#marker text (expect: NOT set, script blocked):', marker);
    console.log('Real CSP violation reported in browser console:');
    cspViolations.forEach(v => console.log('  ' + v));
    await page.close();
  }

  await browser.close();
  serverProc.kill();
}

main();
