const { chromium } = require('playwright');
const http = require('http');
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

function sleep(ms) { return new Promise((r) => setTimeout(r, ms)); }

function serveStatic(rootDir, port) {
  const server = http.createServer((req, res) => {
    let urlPath = decodeURIComponent(new URL(req.url, 'http://localhost').pathname);
    if (urlPath === '/') urlPath = '/index.html';
    const filePath = path.join(rootDir, urlPath);
    fs.readFile(filePath, (err, data) => {
      if (err) {
        res.writeHead(404);
        res.end('not found');
        return;
      }
      const ext = path.extname(filePath);
      const type = ext === '.js' ? 'application/javascript' : ext === '.html' ? 'text/html' : 'application/octet-stream';
      res.writeHead(200, { 'Content-Type': type });
      res.end(data);
    });
  });
  server.listen(port);
  return server;
}

async function main() {
  // host/dist is build output (gitignored) -- copy the checked-in static
  // shell page into it so a fresh clone + npm run build has something to serve.
  fs.mkdirSync(path.join(__dirname, 'host', 'dist'), { recursive: true });
  fs.copyFileSync(
    path.join(__dirname, 'host', 'index.html.template'),
    path.join(__dirname, 'host', 'dist', 'index.html')
  );

  const remoteServer = serveStatic(path.join(__dirname, 'remote', 'dist'), 3001);
  const hostServer = serveStatic(path.join(__dirname, 'host', 'dist'), 3002);
  await sleep(300);

  const browser = await chromium.launch();

  console.log('\n=== 1. Host shell renders a component whose actual code came from a separately built remote bundle ===');
  {
    const page = await browser.newPage();
    const requestedUrls = [];
    page.on('request', (req) => requestedUrls.push(req.url()));
    await page.goto('http://localhost:3002/');
    await sleep(300);
    const buttonText = await page.locator('#remote-button').textContent();
    const labelText = await page.locator('#host-label').textContent();
    const fetchedRemoteEntry = requestedUrls.some((u) => u.includes('localhost:3001/remoteEntry.js'));
    const fetchedButtonChunk = requestedUrls.some((u) => u.includes('localhost:3001/src_Button_js.js'));
    console.log('Host page label (host\'s own code):', labelText);
    console.log('Rendered button text (came from remote):', buttonText);
    console.log('Host page fetched remote\'s remoteEntry.js at runtime:', fetchedRemoteEntry);
    console.log('Host page fetched remote\'s Button chunk at runtime:', fetchedButtonChunk);
    await page.close();
  }

  console.log('\n=== 2. Rebuilding ONLY the remote, with the host never rebuilt, changes what the host renders ===');
  {
    console.log('Editing remote/src/Button.js and rebuilding ONLY the remote (no host rebuild)...');
    const buttonPath = path.join(__dirname, 'remote', 'src', 'Button.js');
    const original = fs.readFileSync(buttonPath, 'utf8');
    const updated = original
      .replace('Remote Button v1', 'Remote Button v2 -- deployed independently')
      .replace('#2b6cb0', '#c53030');
    fs.writeFileSync(buttonPath, updated);
    execSync('npx webpack --config remote/webpack.config.js', { cwd: __dirname, stdio: 'inherit' });
    fs.writeFileSync(buttonPath, original); // restore source for repeatability

    const page = await browser.newPage();
    await page.goto('http://localhost:3002/');
    await sleep(300);
    const buttonText = await page.locator('#remote-button').textContent();
    const buttonColor = await page.locator('#remote-button').evaluate((el) => getComputedStyle(el).backgroundColor);
    console.log('Rendered button text after remote-only rebuild (host bundle.js untouched):', buttonText);
    console.log('Rendered button background-color after remote-only rebuild:', buttonColor);
    await page.close();
  }

  await browser.close();
  remoteServer.close();
  hostServer.close();
}

main();
