import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: './src/setupTests.js',
    // Vitest's default include glob also matches "*.spec.js", which
    // collides with Playwright's own naming convention for e2e/ — without
    // this exclude, `npm run test` tries to run Playwright's test() calls
    // as Vitest tests and fails with "did not expect test() to be called
    // here." Playwright specs run separately via `npm run test:e2e`.
    exclude: ['**/node_modules/**', 'e2e/**'],
  },
})
