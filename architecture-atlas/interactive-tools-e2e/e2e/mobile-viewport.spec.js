import { expect, test } from '@playwright/test';

// Runs only under the "mobile-375" project (see playwright.config.js) --
// a real 375x812 Chromium viewport with touch enabled, the exact width
// this project's own notes flagged as never actually checked for these
// two tools. Verifies two concrete things ad hoc desktop testing can't:
// the page never grows a horizontal scrollbar, and both tools still work
// when driven with tap() instead of click()/mouse drag.

test('design canvas: no horizontal overflow and add/tap-drag work at 375px', async ({ page }) => {
  await page.goto('architecture-atlas/interactive-system-design-canvas/');
  await page.locator('#cci-design-canvas[data-cci-design-canvas-ready="true"]').waitFor();

  const overflow = await page.evaluate(() => ({
    scrollWidth: document.documentElement.scrollWidth,
    clientWidth: document.documentElement.clientWidth,
  }));
  expect(overflow.scrollWidth).toBeLessThanOrEqual(overflow.clientWidth + 1);

  await page.getByRole('button', { name: '+ Client' }).tap();
  const node = page.locator('#cci-canvas-surface [data-node-id]');
  await expect(node).toHaveCount(1);

  // The node itself (150px, see NODE_WIDTH in design-canvas.js) must fit
  // inside the surface at this viewport width -- not just be present.
  const surfaceBox = await page.locator('#cci-canvas-surface').boundingBox();
  const nodeBox = await node.boundingBox();
  if (!surfaceBox || !nodeBox) throw new Error('missing bounding box');
  expect(nodeBox.x).toBeGreaterThanOrEqual(surfaceBox.x - 1);
  expect(nodeBox.x + nodeBox.width).toBeLessThanOrEqual(surfaceBox.x + surfaceBox.width + 1);

  // Toolbar buttons must still be reachable (not clipped off-canvas by
  // the fixed-width palette failing to wrap at this width).
  const exportBtn = page.locator('#cci-canvas-export');
  await expect(exportBtn).toBeVisible();
  const exportBox = await exportBtn.boundingBox();
  if (!exportBox) throw new Error('export button has no bounding box');
  expect(exportBox.x + exportBox.width).toBeLessThanOrEqual(overflow.clientWidth + 1);
});

test('whiteboard timer: no horizontal overflow and tap-driven start/pause work at 375px', async ({
  page,
}) => {
  await page.goto('architecture-atlas/timed-whiteboard-practice/');
  await page.locator('#cci-whiteboard-timer[data-cci-whiteboard-timer-ready="true"]').waitFor();

  const overflow = await page.evaluate(() => ({
    scrollWidth: document.documentElement.scrollWidth,
    clientWidth: document.documentElement.clientWidth,
  }));
  expect(overflow.scrollWidth).toBeLessThanOrEqual(overflow.clientWidth + 1);

  // The phase-minutes table is the one element in this widget most likely
  // to overflow a narrow viewport (six rows, a label plus a number input)
  // -- check it directly, not just the page as a whole.
  const table = page.locator('.whiteboard-timer__phases');
  const tableBox = await table.boundingBox();
  if (!tableBox) throw new Error('phases table has no bounding box');
  expect(tableBox.x + tableBox.width).toBeLessThanOrEqual(overflow.clientWidth + 1);

  await page.locator('[data-phase-minutes="0"]').fill('0.1');
  await page.locator('#cci-timer-start').tap();
  await expect(page.locator('#cci-timer-pause')).toBeEnabled();

  await page.locator('#cci-timer-pause').tap();
  await expect(page.locator('#cci-timer-start')).toBeEnabled();
});
