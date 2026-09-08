# TypeScript Fundamentals demos (F-003)

Real TypeScript 6.0.3 source backing [`syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md`](../../../syllabus/21-frontend-web/typescript-fundamentals-types-interfaces-and-generics.md).

This is a plain `tsc`-checked Node-style project (no bundler, no React) — the
point of this chapter is TypeScript itself, before it ever touches JSX.

## Run it

```bash
npm install
npm run build   # tsc -p tsconfig.json — type-checks every file under src/, emits nothing (noEmit: true)
node src/01-why-typescript-bug.js   # run the plain-JS bug directly — it really crashes
```

## Files

1. **`01-why-typescript-bug.js`** — plain JavaScript. `formatPrice(amount)` assumes `amount` is always a number; nothing stops you from calling it with a string. Run it with `node` and it really throws `TypeError: amount.toFixed is not a function` on the last call — see the captured crash below.
2. **`01-why-typescript-fix.ts`** — the identical function, annotated `amount: number`. The identical mistake at the identical call site is now a compile-time error instead of a runtime crash — see `tsc-output-before-fix.txt` / `tsc-output-after-fix.txt`.
3. **`02-basic-types.ts`** — `string`/`number`/`boolean`/arrays/tuples/enums, inference vs. explicit annotation, and why `any` silently disables checking while `unknown` forces a narrowing check first.
4. **`03-interfaces-and-type-aliases.ts`** — `interface` vs. `type` alias, and a real demonstration of structural typing: a `Coordinate`-typed value is assignable to a `Point`-typed variable because the shapes match, with no declared relationship between the two interfaces at all.
5. **`04-union-intersection-discriminated.ts`** — union types (`string | number`) requiring narrowing before use, intersection types (`Timestamped & Named`) requiring every field from both, and a discriminated union (`Shape`) where switching on the shared `kind` field narrows each branch to exactly one member.
6. **`05-generics.ts`** — a generic `identity<T>` function, a generic `Box<T>` container, and a generic constraint (`<T extends HasId>`) — with an inline comment showing the exact `tsc` error a mismatched generic usage produces.
7. **`06-function-types-optional-readonly.ts`** — function types for typed callbacks, optional properties (`?`) forcing a `string | undefined` check, and `readonly` properties (compile-time only — not runtime immutability, demonstrated directly).

## Captured evidence (real `tsc` runs, not invented)

### The plain-JS bug actually crashes

```
$ node src/01-why-typescript-bug.js
$19.50
$0.00
$19.00
file:///.../src/01-why-typescript-bug.js:13
  return `$${amount.toFixed(2)}`;
                    ^

TypeError: amount.toFixed is not a function
    at formatPrice (file:///.../src/01-why-typescript-bug.js:13:21)
    ...
```

### The same mistake in TypeScript: a real, captured compile-time rejection

`src/01-why-typescript-fix.ts` line 19 was temporarily uncommented
(`console.log(formatPrice("19.99"));`) and `npx tsc -p tsconfig.json` was
run — full transcript in [`tsc-output-before-fix.txt`](tsc-output-before-fix.txt):

```
src/01-why-typescript-fix.ts(23,25): error TS2345: Argument of type 'string' is not assignable to parameter of type 'number'.
```

The line was then reverted (commented back out) and `npx tsc -p tsconfig.json`
was run again over the whole project — full transcript in
[`tsc-output-after-fix.txt`](tsc-output-after-fix.txt):

```
(no output — zero errors)
```

Same mistake, same call site, two different outcomes: a runtime crash in
plain JavaScript versus a precisely located compile-time error in
TypeScript, caught before the code ever runs.

## Reproducing this yourself

```bash
cd practice/frontend/typescript-fundamentals
npm install
# 1. See the real runtime crash:
node src/01-why-typescript-bug.js
# 2. Reproduce the real compile-time rejection: uncomment the last line of
#    src/01-why-typescript-fix.ts, then run:
npx tsc -p tsconfig.json
# 3. Revert the line and confirm a clean compile:
npx tsc -p tsconfig.json
```

## Verification performed

- `node src/01-why-typescript-bug.js` — really executed with Node 24.18.0; really crashed with the `TypeError` captured above.
- `npx tsc -p tsconfig.json` — run with the deliberate bug active (captured in `tsc-output-before-fix.txt`) and again after reverting it (captured in `tsc-output-after-fix.txt`, covering all six source files with zero errors).
- TypeScript version used throughout: `6.0.3` (this repository's existing `practice/frontend/react-typescript/` dependency, matching its `~6.0.2` devDependency range).
