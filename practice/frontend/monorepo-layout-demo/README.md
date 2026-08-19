# Monorepo layout demo (F-303)

A real, minimal npm workspaces monorepo — one shared package, two independent consumers — backing [`handbook/frontend/nextjs-monorepo-layout.md`](../../../handbook/frontend/nextjs-monorepo-layout.md) (F-303), the final entry in the frontend topic register.

## Run it

```bash
npm install                        # real npm workspaces install, hoists + symlinks
node packages/web-app/run.js       # [web-app] SHARED_UTILS_V1: Hello, frontend!
node packages/api-service/run.js   # [api-service] SHARED_UTILS_V1: Hello, backend!
```

## Layout

```
monorepo-layout-demo/
├── package.json              # "workspaces": ["packages/*"]
└── packages/
    ├── shared-utils/         # a real, local package -- not published anywhere
    ├── web-app/               # depends on "shared-utils": "*"
    └── api-service/           # depends on "shared-utils": "*"
```

## Captured evidence

### A real symlink, not a copy

```
$ npm install
$ ls -la node_modules/shared-utils
lrwxr-xr-x  ...  node_modules/shared-utils -> ../packages/shared-utils
```

`npm install` hoisted all three local workspace packages (`web-app`, `api-service`, `shared-utils`) into ONE root `node_modules/`, each as a real symlink pointing straight back to its own source directory — no copy, no build step, no publish step.

### Real, instant propagation — no rebuild, no republish

```
$ node packages/web-app/run.js
[web-app] SHARED_UTILS_V1: Hello, frontend!

$ sed -i '' 's/SHARED_UTILS_V1/SHARED_UTILS_V2_EDITED/' packages/shared-utils/index.js
$ node packages/web-app/run.js       # NOT re-installed, NOT rebuilt -- just re-run
[web-app] SHARED_UTILS_V2_EDITED: Hello, frontend!
$ node packages/api-service/run.js
[api-service] SHARED_UTILS_V2_EDITED: Hello, backend!
```

Both consumers picked up the edit instantly. Reverted immediately after capture.

### The real contrast: this repo's OWN actual structure has none of this

This repository itself does NOT use workspace tooling — `practice/java/`, `practice/frontend/react-nextjs-fundamentals/`, `practice/frontend/build-tooling-comparison/`, and `practice/frontend/styling-approaches-comparison/` are all fully independent, each with its own separate `package.json` and `node_modules`, with zero symlinking between them. Real, measured cost of that choice:

```
$ du -sh */node_modules
435M  react-nextjs-fundamentals/node_modules
 39M  build-tooling-comparison/node_modules
 60M  styling-approaches-comparison/node_modules
 55M  react-fundamentals/node_modules
```

All four independently depend on the identical `react@19.2.8` — four separate, fully duplicated real installs, confirmed via `node -p "require('./<dir>/node_modules/react/package.json').version"` on each. A real npm/pnpm workspace covering all of them would hoist ONE shared `react` install instead. This repo's own real choice not to do that is deliberate, not an oversight — see the chapter's own Decision Framework for why.
