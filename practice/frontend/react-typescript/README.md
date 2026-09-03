# React + TypeScript demo app (F-119)

Real Vite + React 19 + TypeScript app backing [`syllabus/21-frontend-web/react-typescript.md`](../../../syllabus/21-frontend-web/react-typescript.md).

## Run it

```bash
npm install
npm run dev    # app at http://localhost:5196
npm run build  # tsc -b && vite build — real compile check, not just a bundler pass
```

Three demos:

1. **Typed props, state, and hooks** (`TypedComponents.tsx`) — a typed props interface with optional children, a typed `useReducer` with an exhaustiveness-checked action union.
2. **A generic component** (`GenericList.tsx`) — one `List<T>` component, used with `T` inferred as `Task` in one call site and `number` in another.
3. **Discriminated union props** (`VariantAlert.tsx`) — an `Alert` component whose required props change based on a `variant` discriminant (`error` requires `onRetry`, the others don't).

## Captured evidence (real `tsc -b` runs)

This chapter's central claim — TypeScript's type errors are real, compile-time-caught bugs, not documentation — was proven by deliberately breaking each demo and running the real compiler, then reverting.

### Baseline: clean compile

```
$ npx tsc -b
(no output — zero errors)
```

### Discriminated union: an `error` Alert without `onRetry`

`App.tsx` was edited to drop `onRetry` from the error-variant `<Alert>` usage:

```
src/App.tsx(32,24): error TS2322: Type '{ variant: "error"; message: string; }' is not assignable to type 'IntrinsicAttributes & AlertProps'.
  Property 'onRetry' is missing in type '{ variant: "error"; message: string; }' but required in type '{ variant: "error"; message: string; onRetry: () => void; }'.
```

`onRetry` is only required on the `'error'` branch of the union — TypeScript reported that specific, narrowed requirement, not a generic "missing prop" error.

### Generic component: mismatching `renderItem`'s param type

`GenericList.tsx`'s `<List items={tasks} .../>` call was edited so `renderItem` was typed `(task: number) => ...` while `items` stayed `tasks: Task[]`:

```
src/demos/GenericList.tsx(42,9): error TS2322: Type 'Task[]' is not assignable to type 'number[]'.
  Type 'Task' is not assignable to type 'number'.
src/demos/GenericList.tsx(43,38): error TS2339: Property 'id' does not exist on type 'number'.
```

Real proof that `T` is unified across every prop of `ListProps<T>` at a single call site — mistyping `renderItem` alone was enough to break `items` too, because the compiler inferred `T = number` from `renderItem` first and then checked `items` against that.

### Exhaustiveness checking: a new action variant without a matching `case`

`TypedComponents.tsx`'s `CounterAction` union was extended with a `{ type: 'double' }` member, with no corresponding `case 'double'` added to `counterReducer`'s switch:

```
src/demos/TypedComponents.tsx(50,26): error TS2345: Argument of type '{ type: "double"; }' is not assignable to parameter of type 'never'.
```

The `default: return assertNever(action)` branch is only reachable, per TypeScript's control-flow narrowing, when every named case has already been handled — so at that point `action`'s type is narrowed to `never`. Adding a new union member without a matching case leaves that member still assignable to `action` at the `default` branch, and passing a non-`never` value to a parameter typed `never` is a real, compile-time-caught bug — not a lint suggestion.

### Final: clean compile restored

```
$ npx tsc -b
(no output — zero errors)
```

## Verification performed

- `npx tsc -b` — run four times: once clean, then after each of the three deliberate breakages above (each producing the exact captured error), then once more clean after reverting.
- Live browser check: clicking `+1` moved the reducer-driven counter from `Count: 0` to `Count: 1`; all three `Alert` variants (`info`, `success`, `error`) rendered with the expected classes and text, confirmed via `document.querySelectorAll('.alert')`.
- `npm run build` — clean production build (`tsc -b && vite build`), zero errors/warnings.
