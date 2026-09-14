import { expect, test } from '@playwright/test';

// Real, full-browser E2E flow via Playwright: a real Chromium instance
// driving the real dev server, exercising the LoginForm exactly as a
// user would (fill fields, click submit) with no mocks anywhere in
// this file. Complements LoginForm.behavior.test.jsx, which tests the
// component in isolation with jsdom instead of a real browser.
test('logs in with valid credentials end to end', async ({ page }) => {
  await page.goto('/');

  await page.getByLabel('Username').fill('juan');
  await page.getByLabel('Password').fill('hunter2');
  await page.getByRole('button', { name: 'Log in' }).click();

  await expect(page.getByTestId('submitted-payload')).toHaveText('Submitted: juan / hunter2');
});

test('shows a validation error for a too-short username and never submits', async ({ page }) => {
  await page.goto('/');

  // Scoped to the login form's own alert: the page also renders a
  // second, unrelated alert from UserProfile's failed fetch (there's
  // no real backend here), and an unscoped getByRole('alert') hit a
  // real Playwright strict-mode violation ("resolved to 2 elements")
  // the first time this test ran — a genuine, captured E2E gotcha,
  // not a hypothetical one.
  const loginForm = page.getByRole('form', { name: 'login' });
  await loginForm.getByLabel('Username').fill('ab');
  await loginForm.getByRole('button', { name: 'Log in' }).click();

  await expect(loginForm.getByRole('alert')).toHaveText('Username must be at least 3 characters');
  await expect(page.getByTestId('submitted-payload')).toHaveCount(0);
});
