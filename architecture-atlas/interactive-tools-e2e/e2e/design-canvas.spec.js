import { expect, test } from '@playwright/test';

// Real Chromium against the real built MkDocs page -- no mocked DOM, no
// mocked localStorage, no stubbed pointer events. Exercises
// docs/javascripts/design-canvas.js exactly as a reader would on
// architecture-atlas/interactive-system-design-canvas.md.

const PAGE_PATH = 'architecture-atlas/interactive-system-design-canvas/';

test.beforeEach(async ({ page }) => {
  await page.goto(PAGE_PATH);
  await page.locator('#cci-design-canvas[data-cci-design-canvas-ready="true"]').waitFor();
});

test('adding a component renders a draggable node on the surface', async ({ page }) => {
  await page.getByRole('button', { name: '+ Client' }).click();

  const node = page.locator('#cci-canvas-surface [data-node-id]');
  await expect(node).toHaveCount(1);
  await expect(node.locator('.design-canvas__node-label')).toHaveText('Client');
});

test('connect mode draws an edge between two components', async ({ page }) => {
  await page.getByRole('button', { name: '+ Client' }).click();
  await page.getByRole('button', { name: '+ Database' }).click();

  const nodes = page.locator('#cci-canvas-surface [data-node-id]');
  await expect(nodes).toHaveCount(2);

  const connectBtn = page.locator('#cci-canvas-connect');
  await connectBtn.click();
  await expect(connectBtn).toHaveAttribute('aria-pressed', 'true');

  await nodes.nth(0).click();
  await expect(nodes.nth(0)).toHaveClass(/design-canvas__node--pending/);
  await nodes.nth(1).click();

  await expect(page.locator('#cci-canvas-lines line.design-canvas__edge')).toHaveCount(1);
  await expect(nodes.nth(0)).not.toHaveClass(/design-canvas__node--pending/);
});

test('dragging a node moves it and keeps its connected edge attached', async ({ page }) => {
  await page.getByRole('button', { name: '+ Client' }).click();
  await page.getByRole('button', { name: '+ Database' }).click();
  await page.locator('#cci-canvas-connect').click();

  const nodes = page.locator('#cci-canvas-surface [data-node-id]');
  await nodes.nth(0).click();
  await nodes.nth(1).click();
  await expect(page.locator('#cci-canvas-lines line.design-canvas__edge')).toHaveCount(1);

  const dragged = nodes.nth(0);
  const before = await dragged.boundingBox();
  const box = before;
  if (!box) throw new Error('node has no bounding box');

  await page.mouse.move(box.x + box.width / 2, box.y + box.height / 2);
  await page.mouse.down();
  await page.mouse.move(box.x + box.width / 2 + 80, box.y + box.height / 2 + 40, { steps: 8 });
  await page.mouse.up();

  const after = await dragged.boundingBox();
  if (!after) throw new Error('node lost its bounding box after drag');
  expect(after.x).not.toBeCloseTo(before.x, 0);

  // The drag ended on a real pointer move past the click-move threshold,
  // so handleNodeClick's "toggle connect selection" branch must NOT have
  // fired -- the edge from the earlier connect stays exactly one line.
  await expect(page.locator('#cci-canvas-lines line.design-canvas__edge')).toHaveCount(1);
});

test('deleting a node also removes the edges attached to it', async ({ page }) => {
  await page.getByRole('button', { name: '+ Client' }).click();
  await page.getByRole('button', { name: '+ Database' }).click();
  await page.locator('#cci-canvas-connect').click();

  const nodes = page.locator('#cci-canvas-surface [data-node-id]');
  await nodes.nth(0).click();
  await nodes.nth(1).click();
  await expect(page.locator('#cci-canvas-lines line.design-canvas__edge')).toHaveCount(1);

  await nodes.nth(0).locator('.design-canvas__node-delete').click();

  await expect(nodes).toHaveCount(1);
  await expect(page.locator('#cci-canvas-lines line.design-canvas__edge')).toHaveCount(0);
});

test('double-click renames a component via the native prompt', async ({ page }) => {
  await page.getByRole('button', { name: '+ Service' }).click();
  const node = page.locator('#cci-canvas-surface [data-node-id]');

  page.once('dialog', (dialog) => {
    expect(dialog.type()).toBe('prompt');
    dialog.accept('Order Service');
  });
  await node.dblclick();

  await expect(node.locator('.design-canvas__node-label')).toHaveText('Order Service');
});

test('export PNG produces real, non-empty image data and triggers a download', async ({ page }) => {
  await page.getByRole('button', { name: '+ Cache' }).click();

  const [download] = await Promise.all([
    page.waitForEvent('download'),
    page.locator('#cci-canvas-export').click(),
  ]);

  expect(download.suggestedFilename()).toBe('system-design-canvas.png');

  const lastExport = await page.locator('#cci-design-canvas').getAttribute('data-last-export');
  expect(lastExport).toMatch(/^data:image\/png;base64,/);
});

test('clear wipes the canvas after confirmation', async ({ page }) => {
  await page.getByRole('button', { name: '+ CDN' }).click();
  await expect(page.locator('#cci-canvas-surface [data-node-id]')).toHaveCount(1);

  page.once('dialog', (dialog) => {
    expect(dialog.type()).toBe('confirm');
    dialog.accept();
  });
  await page.locator('#cci-canvas-clear').click();

  await expect(page.locator('#cci-canvas-surface [data-node-id]')).toHaveCount(0);
});

test('layout persists across a real reload via localStorage', async ({ page }) => {
  await page.getByRole('button', { name: '+ API Gateway' }).click();
  await expect(page.locator('#cci-canvas-surface [data-node-id]')).toHaveCount(1);

  await page.reload();
  await page.locator('#cci-design-canvas[data-cci-design-canvas-ready="true"]').waitFor();

  const node = page.locator('#cci-canvas-surface [data-node-id]');
  await expect(node).toHaveCount(1);
  await expect(node.locator('.design-canvas__node-label')).toHaveText('API Gateway');
});
