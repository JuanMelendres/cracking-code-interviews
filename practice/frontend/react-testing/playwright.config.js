import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  reporter: 'list',
  webServer: {
    command: 'npm run dev -- --port 5195 --strictPort',
    url: 'http://localhost:5195',
    reuseExistingServer: false,
    timeout: 30000,
  },
  use: {
    baseURL: 'http://localhost:5195',
  },
});
