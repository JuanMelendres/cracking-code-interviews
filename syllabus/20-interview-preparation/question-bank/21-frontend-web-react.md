---
title: "Interview Question Bank — 21-frontend-web-react"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-14
related:
  - ../../21-frontend-web/INDEX.md
  - 21-frontend-web-foundations.md
  - 21-frontend-web-nextjs.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Frontend Web: React

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline. Part of the 3-file `21-frontend-web` split —
see [`21-frontend-web-foundations.md`](21-frontend-web-foundations.md) for the sourcing
note and the domain's other two files.

**Honest count for this file:** 14 chapters yielded 28 deep questions + 28 quick-fire
questions = **56 real questions**.

---

## React Fundamentals: JSX, Components, Props, and State

### Q1 — What's actually wrong with using the array index as a `key` in a React list, and when is it actually fine?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-fundamentals-jsx-components-props-and-state.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Recites "it's bad practice" without explaining the DOM-node-reuse mechanism — the common mistake this question targets.
- **Senior:** Explains the mechanism unprompted — the index ties the key to a position, not the data's identity, so React reuses the wrong DOM node's carried-over state when order/membership changes.
- **Staff:** Frames it as a lint-enforceable policy decision rather than a per-PR review item.

### Q2 — You render three `<Counter />` components with the same function definition. Why does clicking one not affect the others?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-fundamentals-jsx-components-props-and-state.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a vague answer like "React just handles it," confusing this with closures/scoping in plain JavaScript — the common mistake this question targets.
- **Senior:** States the instance/position-in-tree model unprompted — `useState` tracks state per component instance, keyed by position in the rendered tree, not the function definition.
- **Staff:** Not the focus of this chapter's scope.

---

## React Hooks: useEffect and useRef

### Q1 — `useEffect(() => { setInterval(doSomething, 1000); }, [])` with no return statement. What's wrong, and what actually happens over time?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-hooks-useeffect-and-useref.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Says "it just doesn't clean up" without explaining the ongoing, real-world consequence — the common mistake this question targets.
- **Senior:** States the fix (`return () => clearInterval(id);`) and the accumulation mechanism unprompted — every mount/unmount cycle leaks another live interval.
- **Staff:** Frames this as a lint-enforceable class of bug (`exhaustive-deps`-adjacent tooling) rather than a per-review catch.

### Q2 — Why does clicking a button that does `someRef.current += 1` not update anything on screen, even though the value genuinely changed?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-hooks-useeffect-and-useref.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Gives a vague answer like "refs aren't reactive" without explaining the actual mechanism — the common mistake this question targets.
- **Senior:** States the mechanism unprompted — mutating `.current` is a plain object mutation that never calls a state-update API, so React never schedules a re-render.
- **Staff:** Not the focus of this chapter's scope.

---

## React Memoization and Context: useMemo, useCallback, useContext

### Q1 — You wrap a component's props-consuming child in `React.memo`, but it still re-renders every time the parent re-renders. What are the two most likely causes, and how would you distinguish them?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-usememo-usecallback-and-usecontext.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `memo` alone should always work and concludes it's "broken" rather than checking these two specific causes — the common mistake this question targets.
- **Senior:** Names both causes unprompted (a new-reference prop, or a changing Context value bypassing `memo` entirely) and describes a concrete verification method (Profiler, a render counter).
- **Staff:** Frames this as a team-convention/documentation problem given how easy it is to fix only half the issue and assume it's resolved.

### Q2 — What's the actual difference between `useMemo` making a component "faster" and what it really does?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-usememo-usecallback-and-usecontext.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats `useMemo` as a general performance switch to flip on, rather than a targeted tool with its own real cost — the common mistake this question targets.
- **Senior:** States the overhead trade-off unprompted — for a cheap computation, memoization overhead can exceed the cost of just recomputing it.
- **Staff:** Not the focus of this chapter's scope.

---

## React useReducer and Custom Hooks

### Q1 — `setSubtotal(newValue); setTotal(newValue - discount);` — what's the bug, and how would `useReducer` fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-usereducer-and-custom-hooks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `setTotal`'s functional updater form would fix this — the common mistake this question targets; it wouldn't, since the staleness is about reading a sibling value from the closure.
- **Senior:** Correctly rejects the "just use functional updates" false fix and explains why it doesn't address this specific bug — `discount`/`subtotal` are read from the closure, with no visibility into a sibling setter's pending update.
- **Staff:** Frames this as a case for a shared, tested custom hook or reducer pattern across a team's forms, not a one-off fix.

### Q2 — What makes a function a "hook," technically? Is it just the `use` prefix?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-usereducer-and-custom-hooks.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats the `use` prefix as the mechanism itself — the common mistake this question targets.
- **Senior:** Distinguishes naming convention from mechanism unprompted — what makes it a hook is calling other hooks internally, tying it into React's per-fiber hook bookkeeping.
- **Staff:** Not the focus of this chapter's scope.

---

## React Component Patterns

### Q1 — You have identical logic as both a HOC (`withWindowWidth`) and a custom hook (`useWindowWidth`). What's the actual structural difference in what gets rendered, and why would you prefer one for new code?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-component-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Claims HOCs are "bad" or "wrong" rather than explaining the specific structural cost — the common mistake this question targets.
- **Senior:** States the hidden-component/injected-prop distinction unprompted — the HOC mounts a separate wrapper instance invisible at the call site; the hook introduces zero extra components.
- **Staff:** Frames the fix as a deliberate migration policy decision, not an ad-hoc individual choice.

### Q2 — What problem do compound components solve that a custom hook doesn't?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-component-patterns.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats compound components as "just another way to share hook logic" — the common mistake this question targets, missing that the problem being solved is different.
- **Senior:** Correctly names Context as the underlying mechanism, and states the "fixed set of components sharing state implicitly" framing precisely.
- **Staff:** Not the focus of this chapter's scope beyond noting the shared re-render-cost model with the `useContext` chapter.

---

## React State Management Landscape: Context vs. Redux Toolkit vs. Zustand vs. Server State

### Q1 — A teammate says "Context causes performance problems, we should switch everything to Redux." How do you respond?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-state-management.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Agrees wholesale that "Context is bad, always use Redux" without identifying that the actual problem is bundling unrelated state into one value — the common mistake this question targets.
- **Senior:** Diagnoses the actual mechanism (bundling, not Context itself) before proposing a fix, and proposes the smallest fix that solves the actual measured problem — splitting contexts, or moving only frequently-changing state.
- **Staff:** Frames per-state-slice tool selection as an ongoing architectural discipline, not a one-time library choice for the whole app.

### Q2 — A component fetches a user's profile with `useEffect` + `useState`, and a sibling component fetches the SAME profile the same way. What's wrong with this, and what would you do instead?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-state-management.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes fixing this by lifting the fetch into a shared Redux/Zustand store — the common mistake this question targets, which requires manually reimplementing caching/deduplication/staleness a server-state library already provides.
- **Senior:** Identifies this specifically as a client-state-tool-vs-server-state-tool category error, not just "these components should share a fetch somehow," and proposes a server-state library (TanStack Query) with a shared query key.
- **Staff:** Discusses cache invalidation strategy as part of the real problem, and the broader argument for standardizing server-state handling across a codebase.

---

## React Forms: Controlled vs. Uncontrolled, Validation Strategy, and React Hook Form / Zod

### Q1 — A form with 20 fields, all separate `useState` controlled inputs, feels sluggish while typing. What's your diagnosis process?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-forms.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to "add `useMemo`/`React.memo` everywhere" without first identifying that the re-render source is the controlled-input pattern itself — the common mistake this question targets.
- **Senior:** Identifies controlled-input re-render cost as the root cause unprompted and proposes a measurable verification method (Profiler, render counter).
- **Staff:** Frames the fix as a reusable team convention (when to default to controlled vs. `react-hook-form`) rather than a one-off patch.

### Q2 — Why would you choose `onBlur` validation timing over `onChange` for a given field, and when would `onSubmit`-only be the right choice instead?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-forms.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats validation timing as a purely technical/library-default choice rather than an explicit UX trade-off — the common mistake this question targets.
- **Senior:** States the UX trade-off for each strategy clearly — `onChange` is fastest but naggy, `onBlur` is a good general default, `onSubmit`-only defers all feedback.
- **Staff:** Not the primary focus of this chapter.

---

## React Error Boundaries and Error Handling Strategy

### Q1 — You wrap a component in an error boundary, but an error inside an `onClick` handler never shows the fallback UI. Why, and how would you fix it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-error-boundaries.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the boundary is misconfigured or positioned wrong — the common mistake this question targets.
- **Senior:** States the render-phase-only scoping as the root cause unprompted — event handlers run outside React's render phase entirely, so no boundary can intercept them — and proposes the correct `try`/`catch` fix.
- **Staff:** Frames this as a reason to establish a consistent, app-wide convention for both boundary placement and local error handling.

### Q2 — One root error boundary, or many smaller boundaries around individual sections? Walk through the actual trade-off.

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-error-boundaries.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats this as a binary "boundaries good, more boundaries better" without articulating the blast-radius mechanism — the common mistake this question targets.
- **Senior:** Explains the blast-radius mechanism precisely and recommends layering both — a root-level safety net plus granular boundaries around meaningful sections.
- **Staff:** Connects this to a team-wide convention decision, not just a single app's structure.

---

## React Accessibility: Semantic HTML, ARIA, Keyboard Navigation, and Focus Management

### Q1 — A custom dropdown built entirely from styled `div` elements with `onClick` handlers works fine when clicked. What's likely broken, and how would you verify it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-accessibility.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes "it works when I click it" is sufficient evidence of general accessibility — the common mistake this question targets.
- **Senior:** Identifies both the missing tab-order inclusion and the missing key-handler as separate issues, and proposes a concrete verification method (`document.activeElement` trace).
- **Staff:** Frames this as a reason to default to native elements or a tested widget pattern library going forward.

### Q2 — A modal traps focus correctly — Tab cycles within it and never escapes. Is that sufficient for good focus management? What else would you check?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-accessibility.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "the trap works" as equivalent to "focus management is done," missing entry and return behaviors — the common mistake this question targets.
- **Senior:** Names all three behaviors unprompted (entry, trap, return) and can describe how to verify each independently.
- **Staff:** Connects incomplete focus management to real, measurable user harm (disorientation, task abandonment).

---

## React Performance: Profiling, Memoization Strategy, Virtualization, and Code-Splitting

### Q1 — A teammate wrapped a component in `React.memo`, but it still re-renders every time its parent does. What's the most likely cause, and how would you confirm it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-performance.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `React.memo` is "broken" or that memoization "doesn't really work," rather than checking prop reference stability — the common mistake this question targets.
- **Senior:** Identifies reference instability as the specific cause unprompted and proposes a concrete verification method (Profiler's "why did this render").
- **Staff:** Frames this as a class of bug worth a team-wide lint rule or convention.

### Q2 — You need to render a list of 10,000 items. Would you reach for virtualization? Walk through your actual decision process.

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-performance.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Reaches for virtualization immediately without considering whether reducing the actual data set (pagination, filtering) might be the better fix — the common mistake this question targets.
- **Senior:** Considers data-set-reduction alternatives before jumping to virtualization, and proposes a real verification method (a DOM node count confirming the window moves).
- **Staff:** Frames the choice as a product/UX decision as much as a technical one.

---

## React Reconciliation and the Fiber Architecture

### Q1 — A component's internal state unexpectedly resets to its initial value every time a certain condition changes. What's the most likely reconciliation-related cause, and how would you confirm it?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-reconciliation-and-fiber.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes the reset is a bug in the component's own state logic rather than checking the calling code's structure first — the common mistake this question targets.
- **Senior:** States the type-change/key-change cause unprompted and can name the fix — keep one component type mounted, toggle behavior via props.
- **Staff:** Connects this to a broader diagnostic habit — checking reconciliation structure before assuming a logic bug — as a real production-debugging skill.

### Q2 — A handler calls three separate `setState` functions. Does this cause three re-renders or one? How would you actually verify your answer?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-reconciliation-and-fiber.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Correctly states "one render" but can't describe how they'd verify it, relying purely on memorized documentation — the common mistake this question targets.
- **Senior:** Proposes a concrete verification method — a real, measured commit counter confirming one render regardless of how many state updates occur in the handler.
- **Staff:** Not the focus of this chapter's scope.

---

## Concurrent React: Transitions, Deferred Values, and Suspense for Data

### Q1 — You wrap a `setState` call in `startTransition`, but the UI still feels like it blocks on every keystroke. What would you check first?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-concurrent-rendering.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Assumes `startTransition` is broken or not supported, rather than checking what's actually inside vs. outside the wrapped call — the common mistake this question targets.
- **Senior:** Identifies the "accidentally urgent work inside the transition" failure mode unprompted — the input's own displayed value shouldn't be wrapped.
- **Staff:** Not the primary focus of this chapter.

### Q2 — What's the actual difference between what `useTransition`/`useDeferredValue` do and what `Suspense` does?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-concurrent-rendering.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Treats them as interchangeable "loading state" tools without articulating the synchronous-vs-asynchronous distinction — the common mistake this question targets.
- **Senior:** States the synchronous-vs-asynchronous distinction clearly and unprompted — transitions reprioritize already-available expensive work; Suspense handles genuinely async readiness.
- **Staff:** Can describe the combined pattern (transition + Suspense together) and why it improves perceived UX over a bare Suspense fallback appearing abruptly.

---

## React Testing: RTL Philosophy, Mocking, and E2E with Playwright

### Q1 — A teammate's test queries an element with `container.querySelector('.btn-primary')`. What's your concern, and what would you suggest instead?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-testing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Frames this as a purely stylistic preference ("RTL recommends it") rather than explaining the concrete failure mode it prevents — the common mistake this question targets.
- **Senior:** Explains the why (implementation coupling to a CSS class produces false negatives) unprompted, with a concrete example, and suggests `getByRole` with an accessible name.
- **Staff:** Frames repeated false negatives as an organizational trust cost, and proposes a durable prevention mechanism (lint rule, migration plan).

### Q2 — You need to test a component that fetches data from an API on mount. Walk through your approach, including what you would and wouldn't verify.

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-testing.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Tests against a real network call, or verifies only the final rendered output and never the call arguments — the common mistake this question targets.
- **Senior:** Mocks the fetch dependency, verifies behavior across loading/success/error states, and additionally verifies the interaction (call arguments) when it's part of the actual contract.
- **Staff:** Connects the mock-drift risk to the broader need for contract or E2E coverage at team/org scale.

---

## TypeScript with React: Typing Props/State/Hooks, Generic Components, and Discriminated Unions

### Q1 — A `Toast` component's `variant` prop is `'info' | 'error'`, and only `'error'` should require `onDismiss`. How would you model this, and why not just make `onDismiss` optional?

**Canonical treatment:** [§ Interview Questions, Q1](../../21-frontend-web/react-typescript.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Proposes `onDismiss?: () => void` with a runtime warning check — the common mistake this question targets, reimplementing at runtime with weaker guarantees what the type system provides for free.
- **Senior:** Proposes the discriminated union unprompted and explains specifically why the optional-field alternative is weaker — a checked constraint vs. an unchecked convention.
- **Staff:** Frames this as a team-scale convention (all variant-driven components should default to this pattern).

### Q2 — You're building a generic `Table<T>` component. What makes it genuinely generic versus just using `any`, and how would you catch a caller who passes mismatched props?

**Canonical treatment:** [§ Interview Questions, Q2](../../21-frontend-web/react-typescript.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes `<T>` and `any` as roughly interchangeable "flexible" typing strategies — the common mistake this question targets, missing that `any` provides zero cross-prop checking.
- **Senior:** Explains why `any` loses cross-prop checking specifically, with a concrete example of what it would silently allow — every prop referencing the same type parameter `T` ties them together.
- **Staff:** Discusses when a shared generic component is and isn't the right call at a codebase scale (duplication vs. readability trade-off).

---

## Quick-fire questions (from this file's chapters' Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Why does using the array index as a React list `key` cause bugs when items are removed or reordered? | [React Fundamentals](../../21-frontend-web/react-fundamentals-jsx-components-props-and-state.md#flashcards) |
| 2 | If three components render the same function definition and each calls `useState(0)`, do they share state? | [React Fundamentals](../../21-frontend-web/react-fundamentals-jsx-components-props-and-state.md#flashcards) |
| 3 | What happens if an effect that starts a `setInterval` returns no cleanup function? | [React Hooks: useEffect and useRef](../../21-frontend-web/react-hooks-useeffect-and-useref.md#flashcards) |
| 4 | Why does a value read inside a `setInterval` callback created in `useEffect([])` never update? | [React Hooks: useEffect and useRef](../../21-frontend-web/react-hooks-useeffect-and-useref.md#flashcards) |
| 5 | A `memo()`'d Context consumer still re-renders when an unrelated field changes. What's the likely cause? | [Memoization and Context](../../21-frontend-web/react-usememo-usecallback-and-usecontext.md#flashcards) |
| 6 | Does wrapping a function in `useCallback` help if the component receiving it as a prop isn't wrapped in `React.memo`? | [Memoization and Context](../../21-frontend-web/react-usememo-usecallback-and-usecontext.md#flashcards) |
| 7 | Why can `useReducer` correctly derive one field from another during the same update, when two separate `useState` setters can't? | [useReducer and Custom Hooks](../../21-frontend-web/react-usereducer-and-custom-hooks.md#flashcards) |
| 8 | What actually makes a function behave as a React hook — is the `use` prefix itself the mechanism? | [useReducer and Custom Hooks](../../21-frontend-web/react-usereducer-and-custom-hooks.md#flashcards) |
| 9 | What's structurally different about a HOC-wrapped component versus a custom-hook version of the same behavior? | [React Component Patterns](../../21-frontend-web/react-component-patterns.md#flashcards) |
| 10 | What problem do compound components solve that's different from what HOCs/render props/hooks solve? | [React Component Patterns](../../21-frontend-web/react-component-patterns.md#flashcards) |
| 11 | Two components read different fields from the same Context provider. Why does updating only one field re-render both? | [React State Management Landscape](../../21-frontend-web/react-state-management.md#flashcards) |
| 12 | Two independent components call `useQuery` with the exact same `queryKey`. How many real network requests fire? | [React State Management Landscape](../../21-frontend-web/react-state-management.md#flashcards) |
| 13 | What's the actual, measurable re-render difference between a controlled and an uncontrolled input while typing? | [React Forms](../../21-frontend-web/react-forms.md#flashcards) |
| 14 | Why does `react-hook-form` produce fewer re-renders than a hand-rolled, fully controlled form? | [React Forms](../../21-frontend-web/react-forms.md#flashcards) |
| 15 | Precisely, what do React error boundaries catch, and what do they NOT catch? | [React Error Boundaries](../../21-frontend-web/react-error-boundaries.md#flashcards) |
| 16 | What's the actual, measured difference between a shared boundary and per-section granular boundaries when one section crashes? | [React Error Boundaries](../../21-frontend-web/react-error-boundaries.md#flashcards) |
| 17 | A `<div onClick>` styled to look like a button works fine with a mouse. What's actually broken? | [React Accessibility](../../21-frontend-web/react-accessibility.md#flashcards) |
| 18 | What are the three separate, independently-verifiable behaviors that make up "good focus management" for a modal? | [React Accessibility](../../21-frontend-web/react-accessibility.md#flashcards) |
| 19 | `React.memo` is applied to a component, but it still re-renders every time its parent does. What's the most likely cause? | [React Performance](../../21-frontend-web/react-performance.md#flashcards) |
| 20 | What specific cost does list virtualization reduce, and how would you verify an implementation is actually working? | [React Performance](../../21-frontend-web/react-performance.md#flashcards) |
| 21 | What's the difference in outcome between changing an element's TYPE vs. changing a PROP at the same JSX position? | [React Reconciliation and Fiber](../../21-frontend-web/react-reconciliation-and-fiber.md#flashcards) |
| 22 | What does React's batching guarantee actually promise? | [React Reconciliation and Fiber](../../21-frontend-web/react-reconciliation-and-fiber.md#flashcards) |
| 23 | When would you reach for `useTransition` over `useDeferredValue`, and vice versa? | [Concurrent React](../../21-frontend-web/react-concurrent-rendering.md#flashcards) |
| 24 | What kind of "not ready yet" does Suspense handle, and how is that different from what `useTransition` handles? | [Concurrent React](../../21-frontend-web/react-concurrent-rendering.md#flashcards) |
| 25 | A test queries an element with `container.querySelector('.field-wrap input')`. What's the concrete risk? | [React Testing](../../21-frontend-web/react-testing.md#flashcards) |
| 26 | `fetchUser` is mocked and a test asserts the final rendered output. What additional assertion makes this a genuine interaction check? | [React Testing](../../21-frontend-web/react-testing.md#flashcards) |
| 27 | A component's `onRetry` prop should be required only when `variant === 'error'`. Why is a discriminated union stronger than an optional field? | [TypeScript with React](../../21-frontend-web/react-typescript.md#flashcards) |
| 28 | A `useReducer`'s action type union gains a new member, but the reducer's `switch` isn't updated. What compiler mechanism catches this? | [TypeScript with React](../../21-frontend-web/react-typescript.md#flashcards) |

---

## Related

- [`21-frontend-web-foundations.md`](21-frontend-web-foundations.md)
- [`21-frontend-web-nextjs.md`](21-frontend-web-nextjs.md)
- [`20-interview-preparation.md`](20-interview-preparation.md)
- [`08-testing.md`](08-testing.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
