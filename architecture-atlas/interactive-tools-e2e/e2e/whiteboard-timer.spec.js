import { expect, test } from '@playwright/test';

// Real Chromium against the real built MkDocs page -- the countdown runs
// on real wall-clock seconds via docs/javascripts/whiteboard-timer.js's
// own setInterval, not a mocked clock. Tests that need a full six-phase
// run set every phase to its minimum (0.1 min = 6s, same as a reader
// could type into the real inputs) so the suite still finishes quickly.

const PAGE_PATH = 'architecture-atlas/timed-whiteboard-practice/';

test.beforeEach(async ({ page }) => {
  await page.goto(PAGE_PATH);
  await page.locator('#cci-whiteboard-timer[data-cci-whiteboard-timer-ready="true"]').waitFor();
});

test('loads ready with phase 1 and the default 2-minute clock', async ({ page }) => {
  await expect(page.locator('#cci-timer-phase-label')).toHaveText('Ready — Phase 1: Clarify');
  await expect(page.locator('#cci-timer-clock')).toHaveText('02:00');
  await expect(page.locator('#cci-timer-pause')).toBeDisabled();
});

test('41-min preset loads the upper-bound minutes into every phase input', async ({ page }) => {
  await page.locator('[data-preset="41"]').click();

  const expected = ['3', '5', '3', '5', '15', '10'];
  const inputs = page.locator('[data-phase-minutes]');
  for (let i = 0; i < expected.length; i++) {
    await expect(inputs.nth(i)).toHaveValue(expected[i]);
  }
  // Loading a preset resets the clock to that phase's new minutes.
  await expect(page.locator('#cci-timer-clock')).toHaveText('03:00');
});

test('start disables the inputs and pause stops the countdown in place', async ({ page }) => {
  await page.locator('[data-phase-minutes="0"]').fill('0.5'); // 30s, plenty of headroom
  await page.locator('#cci-timer-start').click();

  await expect(page.locator('#cci-timer-start')).toBeDisabled();
  await expect(page.locator('#cci-timer-pause')).toBeEnabled();
  for (const input of await page.locator('[data-phase-minutes]').all()) {
    await expect(input).toBeDisabled();
  }

  await page.waitForTimeout(2200); // let a couple of real ticks land
  await page.locator('#cci-timer-pause').click();

  const pausedClock = await page.locator('#cci-timer-clock').textContent();
  await page.waitForTimeout(2200);
  await expect(page.locator('#cci-timer-clock')).toHaveText(pausedClock ?? '');
  await expect(page.locator('#cci-timer-start')).toBeEnabled();
});

test('reset returns to phase 1 and re-enables the inputs', async ({ page }) => {
  await page.locator('[data-phase-minutes="0"]').fill('0.5');
  await page.locator('#cci-timer-start').click();
  await page.waitForTimeout(1500);

  await page.locator('#cci-timer-reset').click();

  await expect(page.locator('#cci-timer-phase-label')).toHaveText('Ready — Phase 1: Clarify');
  await expect(page.locator('#cci-timer-start')).toBeEnabled();
  await expect(page.locator('#cci-timer-pause')).toBeDisabled();
  for (const input of await page.locator('[data-phase-minutes]').all()) {
    await expect(input).toBeEnabled();
  }
});

test('auto-advances through all six phases in order and completes the session', async ({ page }) => {
  test.setTimeout(90000);

  const inputs = page.locator('[data-phase-minutes]');
  for (let i = 0; i < 6; i++) {
    await inputs.nth(i).fill('0.1'); // 6s per phase -> ~36s for the full run
  }

  const phaseNames = ['Clarify', 'Estimate', 'API', 'Data', 'Architecture', 'Bottlenecks'];
  await page.locator('#cci-timer-start').click();

  for (let i = 0; i < phaseNames.length; i++) {
    await expect(page.locator('#cci-timer-phase-label')).toHaveText(
      `Phase ${i + 1}: ${phaseNames[i]}`,
      { timeout: 10000 },
    );
    await expect(page.locator(`.whiteboard-timer__segment[data-index="${i}"]`)).toHaveClass(
      /is-current/,
    );
  }

  await expect(page.locator('#cci-timer-phase-label')).toHaveText(
    'Session complete — all six phases done',
    { timeout: 10000 },
  );
  await expect(page.locator('#cci-timer-clock')).toHaveText('00:00');
  await expect(page.locator('#cci-timer-start')).toBeDisabled();
  await expect(page.locator('#cci-timer-pause')).toBeDisabled();

  const segments = page.locator('.whiteboard-timer__segment');
  await expect(segments).toHaveCount(6);
  for (const segment of await segments.all()) {
    await expect(segment).toHaveClass(/is-done/);
  }
});
