# Interactive Tools E2E

Real Playwright/Chromium regression tests for the two interactive
practice tools in this directory:
[Interactive System Design Canvas](../interactive-system-design-canvas.md)
(`docs/javascripts/design-canvas.js`) and
[Timed Whiteboard Practice](../timed-whiteboard-practice.md)
(`docs/javascripts/whiteboard-timer.js`).

Both tools were verified ad hoc (an ephemeral Playwright harness, not
committed) when first built. This suite replaces that with committed,
re-runnable coverage, plus a real 375px mobile-viewport check neither
tool had before.

The `webServer` in `playwright.config.js` runs the project's real build
(`scripts/build_docs_site.sh` + `mkdocs serve`) — the same MkDocs site
that deploys to GitHub Pages, not a stub page. A from-scratch build takes
roughly a minute (`git-revision-date-localized` runs `git log` per page),
so the full suite is slower than a typical unit test run.

## Run it

```bash
npm install
npx playwright install chromium
npm test
```

Two projects run: `desktop` (`design-canvas.spec.js`,
`whiteboard-timer.spec.js`) and `mobile-375` (`mobile-viewport.spec.js`,
a real 375×812 viewport with touch enabled).

Not wired into CI (`.github/workflows/`) — matches this repo's existing
`practice/frontend/react-testing/e2e/` Playwright suite, which is also
committed but run manually, not on every push.
