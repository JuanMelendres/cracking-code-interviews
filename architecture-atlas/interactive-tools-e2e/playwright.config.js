import { defineConfig, devices } from '@playwright/test';
import { fileURLToPath } from 'node:url';

// Repo root: three levels up from this config
// (architecture-atlas/interactive-tools-e2e/playwright.config.js).
const repoRoot = fileURLToPath(new URL('../..', import.meta.url));
const PORT = 8756;
const BASE_URL = `http://127.0.0.1:${PORT}/cracking-code-interviews/`;

export default defineConfig({
  testDir: './e2e',
  // mkdocs serve's dev server is single-threaded (Werkzeug's reference
  // server) -- running many parallel Chromium pages against it caused
  // real page loads to queue past 30s and fail their beforeEach hook
  // (confirmed: the same navigation resolves in ~1s run alone). One
  // worker keeps every request honest instead of raising timeouts to
  // paper over a dev-server bottleneck that isn't in the code under test.
  fullyParallel: false,
  workers: 1,
  reporter: 'list',
  webServer: {
    // Real build, not a shortcut: mirrors architecture-atlas/ (and the
    // other MIRROR_DIRS) into docs/ exactly as CI/production does, then
    // serves the real MkDocs site -- same docs_dir, same extra_javascript,
    // same site_url path prefix ("/cracking-code-interviews/", hence the
    // BASE_URL above) as the deployed GitHub Pages build. A full build
    // takes roughly a minute (git-revision-date-localized runs `git log`
    // per page), so the webServer timeout below is generous.
    command:
      'bash scripts/build_docs_site.sh && .venv-docs/bin/mkdocs serve -a 127.0.0.1:' +
      PORT +
      ' --no-livereload',
    cwd: repoRoot,
    url: BASE_URL,
    // A from-scratch build is slow enough that re-running it for every
    // local `npx playwright test` invocation is impractical; CI always
    // starts clean, so it still gets a real build every run.
    reuseExistingServer: !process.env.CI,
    timeout: 240000,
  },
  use: {
    baseURL: BASE_URL,
  },
  projects: [
    {
      name: 'desktop',
      use: { ...devices['Desktop Chrome'] },
      testIgnore: /mobile-viewport\.spec\.js/,
    },
    {
      name: 'mobile-375',
      // iPhone SE viewport width (375px) -- the exact size this project's
      // known, unactioned gap named: docs/javascripts/design-canvas.js and
      // whiteboard-timer.js were verified ad hoc only at desktop width.
      use: {
        ...devices['Desktop Chrome'],
        viewport: { width: 375, height: 812 },
        isMobile: true,
        hasTouch: true,
      },
      testMatch: /mobile-viewport\.spec\.js/,
    },
  ],
});
