# React Testing demo app (F-118)

Real Vite + React 19 app backing [`handbook/frontend/react-testing.md`](../../../handbook/frontend/react-testing.md).

## Run it

```bash
npm install
npm run dev      # app at http://localhost:5194
npm run test      # Vitest + React Testing Library unit/component tests
npm run test:e2e  # Playwright, real Chromium, drives the real dev server
```

Three layers of real, executed tests:

1. **Behavior vs. implementation-detail queries** (`LoginForm.behavior.test.jsx` vs. `LoginForm.implementation-detail.test.jsx`) — same component, two query strategies, a real markup refactor performed mid-session to prove which one breaks.
2. **Mocking an async dependency** (`UserProfile.test.jsx`) — `vi.mock` replaces `fetchUser`, tests verify both the resulting behavior (loading → name) and the interaction itself (called with the right id).
3. **A real E2E flow** (`e2e/login.spec.js`) — Playwright driving a real Chromium instance against the real dev server, no mocks.

## Captured evidence (real terminal sessions)

### Baseline: all three suites green

```
Test Files  3 passed (3)
     Tests  6 passed (6)
```

### A real markup refactor breaks the implementation-detail test, not the behavior test

`LoginForm.jsx` was edited live: the wrapper class was renamed `.field-wrap` → `.input-group`, and the username/password fields were reordered — a pure markup change with zero effect on what a user can see or do (same labels, same roles, same behavior). Re-running immediately after:

```
❯ src/demos/LoginForm.implementation-detail.test.jsx (1 test | 1 failed)
    × submits the first field as username and second field as password
Error: Unable to fire a "change" event - please provide a DOM element.
  ❯ src/demos/LoginForm.implementation-detail.test.jsx:17:15
      const inputs = container.querySelectorAll('.field-wrap input');
      fireEvent.change(inputs[0], { target: { value: 'juan' } });
                ^

Test Files  1 failed | 2 passed (3)
     Tests  1 failed | 5 passed (6)
```

`inputs[0]` was `undefined` — the old class selector matched nothing after the rename, so `fireEvent.change` had no element to fire on. `LoginForm.behavior.test.jsx` (queries by `getByLabelText`/`getByRole`) needed zero changes and stayed green through the same refactor, because it never depended on class names or DOM position in the first place. The implementation-detail test was then updated to match the new markup (`.input-group`, swapped index order) — real, required maintenance work the behavior-based test never paid:

```
Test Files  3 passed (3)
     Tests  6 passed (6)
```

### Mocking proves both the return-value behavior and the interaction

```
✓ UserProfile (mocked dependency) > shows a loading state, then the user name once fetchUser resolves
✓ UserProfile (mocked dependency) > calls fetchUser with the exact id it was given
✓ UserProfile (mocked dependency) > shows an error message when fetchUser rejects
```

`fetchUser` is fully mocked with `vi.mock` — no real `fetch()` call ever happens in this suite. `toHaveBeenCalledWith(42)` is an interaction assertion a return-value-only stub can't make; it's the same "what did this actually call, with what" question a Mockito `verify()` answers on the backend side.

### A real Playwright E2E run, including a genuine locator bug it caught

First run, two tests, real Chromium against the real dev server:

```
✓  1 e2e/login.spec.js:8:1 › logs in with valid credentials end to end (1.1s)
✘  2 e2e/login.spec.js:18:1 › shows a validation error for a too-short username and never submits (1.1s)

Error: expect(locator).toHaveText(expected) failed
Error: strict mode violation: getByRole('alert') resolved to 2 elements:
    1) <p role="alert">Username must be at least 3 characters</p>
    2) <p role="alert">Could not load user</p>
```

Not a scripted example: `UserProfile` really does call `fetch('/api/users/7')` against a dev server with no backend, which really does reject and render its own `role="alert"` element on the same page — an unscoped `page.getByRole('alert')` genuinely matched two elements. Fixed by scoping the locator to the login form (`page.getByRole('form', { name: 'login' }).getByRole('alert')`); re-run:

```
✓  1 e2e/login.spec.js:8:1 › logs in with valid credentials end to end (246ms)
✓  2 e2e/login.spec.js:18:1 › shows a validation error for a too-short username and never submits (212ms)

2 passed (1.3s)
```

### A real Vitest/Playwright naming collision, caught and fixed

`npm run test` initially picked up `e2e/login.spec.js` too — Vitest's default include glob matches `*.spec.js` as well as `*.test.js`, and it tried to execute Playwright's `test()` calls as Vitest tests:

```
FAIL  e2e/login.spec.js [ e2e/login.spec.js ]
Error: Playwright Test did not expect test() to be called here.
```

Fixed with an explicit `exclude: ['**/node_modules/**', 'e2e/**']` in `vite.config.js`'s `test` block — Playwright specs run only via `npm run test:e2e`. A genuine tooling seam between two test runners that happen to share a naming convention, not a contrived example.

## Verification performed

- `npm run test` — Vitest, jsdom environment, real render/interact/assert cycles via React Testing Library and `@testing-library/user-event`, no snapshot tests.
- `npm run test:e2e` — Playwright, real installed Chromium (`npx playwright install chromium`), against the real Vite dev server on port 5195 (Playwright's own `webServer` config, separate from the app's usual dev port to avoid clashing with `.claude/launch.json`).
- `npm run build` — clean production build, zero errors/warnings.
